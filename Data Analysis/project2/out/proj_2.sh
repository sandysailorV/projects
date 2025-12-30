
# Step 1: Extract the header and save it to a new sample file
head -n 1 csgo_round_snapshots.csv >out/csgo_sample_1k.csv


# Step 2: Stream the rest of the file (skip header), shuffle, take 1000lines, and append
tail -n +2 csgo_round_snapshots.csv | shuf -n 1000 >> data/samples/csgo_sample_1k.csv


# We use tail -n +2 to skip the header row of the CSV
# cut -d',' -f4: Selects the 4th column ('map')
# sort | uniq -c: Counts unique occurrences
# sort -nr: Sorts numerically in reverse to see the most played maps
echo "Calculating frequency of maps played:" > out/freq_maps.txt
(echo "count,map" && tail -n +2 csgo_sample_1k.csv | cut -d ',' -f4 | sort | uniq -c | sort -nr)  | tee -a out/freq_maps.txt >> out/freq_maps.txt

echo "Calculating frequency of round winners (CT vs T):" > freq_round_winner.txt

 (echo -e "count\tround_winner" &&
  tail -n +2 csgo_sample_1k.csv | cut -d ',' -f97 | sort | uniq -c | sort -nr | while read -r count label; do printf "%s\t%s\n" "$count" "$label" done) | tee freq_round_winner.txt


# cut -d',' -f4,97: Selects map (col 4) and round_winner (col 97)
# sort | uniq -c: Counts unique combinations
echo "Finding top 10 map-winner combinations:" >> out/top_10_map_winner_combos.txt


echo "Finding top 10 map-winner combinations:" >> top_10_map_winner_combos.txt

(  echo -e "count\tmap\twinner"
  tail -n +2 csgo_sample_1k.csv | cut -d ',' -f4,97 | sort | uniq -c | sort -nr | head -n 10 ) | tee -a out/top_10_map_winner_combos.txt


# cut –d f1,5,97: Selects time_left (col 1), bomb_planted (col 5), and winner (col 97)
#sort -u: sorts the combinations 

echo -e "Comparing time, bomb_planted, and round_winner" > skinny_table_time_bomb_winner.txt

(echo -e "time\tbomb_planted\tround_winner" &&  tail -n +2 csgo_sample_1k.csv | cut -d',' -f1,5,97 | sort -u | tr ',' '\t') | column -t -s $'\t' | tee -a skinny_table_time_bomb_winner.txt




# cut –d f12,13,97: Selects ct_helmets(col 12), bomb_planted (col 5), and winner (col 97)
#sort -u: sorts the combinations 


echo -e "Comparing ct_helmets and t_helmets with round_winner to identify whether helmet presence on either side correlates with winning a round."   >> skinny_table_helmet_winner.txt

(echo -e "ct_helmets\tt_helmets\tround_winner" && tail -n +2 csgo_sample_1k.csv | cut -d',' -f12,13,97 | sort -u | tr ',' '\t') | column -t -s $'\t' | tee skinny_table_helmet_winner.txt

