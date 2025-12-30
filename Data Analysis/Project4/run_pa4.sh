#!/usr/bin/env bash
# run_pa4.sh - Final, validated, and compliant pipeline for CS:GO data analysis.


mkdir -p logs
script_name=$(basename "$0" .sh)
exec > >(tee "logs/${script_name}.log") 2>&1

set -euo pipefail

if [ "$#" -ne 1 ]; then
  echo "Error: Missing input file." >&2
  echo "Usage: $0 <path_to_input_csv>" >&2
  exit 1
fi

RAW_DATA="$1"
if [ ! -f "$RAW_DATA" ] || [ ! -r "$RAW_DATA" ]; then
  echo "Error: Input file '$RAW_DATA' not found or not readable." >&2
  exit 1
fi

mkdir -p out
CLEANED_DATA="out/csgo_rounds_cleaned.tsv"
FILTERED_DATA="out/filtered_rounds.tsv"
SAMPLE_FILE="out/cleaning_sample.txt"

echo "--> Step 1: Cleaning and normalizing data with SED..."
{
  echo "--- BEFORE (first 5 lines) ---"
  head -n 5 "$RAW_DATA"
  echo
  echo "--- AFTER (first 5 lines) ---"
  head -n 5 "$RAW_DATA" | sed -E 's/,"([0-9]+),([0-9]+)"/,\1\2/g; s/,/\t/g; s/^[ \t]+//; s/[ \t]+$//'
} > "$SAMPLE_FILE"

sed -E 's/,"([0-9]+),([0-9]+)"/,\1\2/g; s/,/\t/g; s/^[ \t]+//; s/[ \t]+$//' "$RAW_DATA" > "$CLEANED_DATA"
echo " ✓ Cleaned data saved to $CLEANED_DATA"
echo " ✓ Before/after sample saved to $SAMPLE_FILE"
echo ""

echo "--> Step 2: Generating EDA tables from cleaned data..."
( echo -e "count\tmap\twinner" && \
  awk -F'\t' '
    NR > 1 {
      map_counts[$4]++
      winner_counts[$97]++
      combo = $4 "\t" $97
      combo_counts[combo]++
    }
    END {
      print "count\tmap" > "out/freq_maps.tsv"
      for (m in map_counts) print map_counts[m], m >> "out/freq_maps.tsv"
      print "count\tround_winner" > "out/freq_round_winner.tsv"
      for (w in winner_counts) print winner_counts[w], w >> "out/freq_round_winner.tsv"
      for (c in combo_counts) print combo_counts[c], c
    }' "$CLEANED_DATA" | sort -nr | head -n 10 \
) > out/top_10_map_winner_combos.tsv

awk 'BEGIN{OFS="\t"} {print $4, $97, $80, $81}' "$CLEANED_DATA" > out/skinny_table_economy.tsv
echo " ✓ EDA tables created in out/"
echo ""

echo "--> Step 3: Applying quality filters and emitting filtered TSV..."
awk -F'\t' 'NR==1 || ($1 < 175 && ($80 > 0 || $81 > 0))' "$CLEANED_DATA" > "$FILTERED_DATA"
echo " ✓ Filtered data saved to $FILTERED_DATA"
echo ""

echo "--> Step 4: Calculating win rates by economic advantage..."
awk -F'\t' '
NR > 1 {
  money_diff = $80 - $81
  if (money_diff > 15000) bucket="HIGH (>$15k)"
  else if (money_diff > 5000) bucket="MEDIUM ($5k-$15k)"
  else if (money_diff >= -5000) bucket="EVEN (-$5k to $5k)"
  else bucket="DISADVANTAGE (< -$5k)"
  total[bucket]++
  if ($97 == "CT") ct[bucket]++
}
END {
  printf "eco_advantage_bucket\ttotal_rounds\tct_wins\tct_win_rate\n"
  buckets[1]="HIGH (>$15k)"; buckets[2]="MEDIUM ($5k-$15k)"
  buckets[3]="EVEN (-$5k to $5k)"; buckets[4]="DISADVANTAGE (< -$5k)"
  for (i=1;i<=4;i++){
    b=buckets[i]
    if (total[b]>0){
      printf "%s\t%d\t%d\t%.2f\n", b, total[b], ct[b]+0, (ct[b]+0)/total[b]
    }
  }
}' "$FILTERED_DATA" > out/winrate_by_eco_advantage.tsv
echo " ✓ Economic advantage ratios created in out/"
echo ""

echo "--> Step 5: Analyzing bomb planted status frequency..."
( echo -e "bomb_planted_status\tcount" && \
  awk -F'\t' '
    NR > 1 {
      s = ($5 == "") ? "UNKNOWN" : $5
      counts[s]++
    }
    END { for (k in counts) print k "\t" counts[k] }
  ' "$FILTERED_DATA" | sort -k2,2nr \
) > out/bomb_planted_frequency.tsv
echo " ✓ Bomb planted frequency analysis created in out/"
echo ""

echo "--> Step 6: Discovering numeric signals per map..."
( echo -e "map\tavg_winner_survivors\tavg_loser_survivors" && \
  awk -F'\t' '
    NR > 1 {
      map=$4; win=$97; ct_alive=$94; t_alive=$95
      n[map]++
      if (win=="CT"){ wsum[map]+=ct_alive; lsum[map]+=t_alive }
      else           { wsum[map]+=t_alive;  lsum[map]+=ct_alive }
    }
    END {
      for (m in n){
        printf "%s\t%.2f\t%.2f\n", m, wsum[m]/n[m], lsum[m]/n[m]
      }
    }' "$FILTERED_DATA" | sort -k2,2nr \
) > out/map_signal_summary.tsv
echo " ✓ Map signal summary created in out/"
echo ""

echo "Script finished successfully. All outputs are in the 'out/' directory."
ls -l out/


