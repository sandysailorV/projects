# Group10-CSGO-winrate 🎮

## **Dataset Description:** 
The dataset contains round snapshots collected from approximately 700 professional matches played in high-level CS:GO tournaments during 2019 and 2020. For each live round, snapshots were taken every 20 seconds until the round was completed. The goal of the project is to use the CSGO stats to predict round winrate and probability. 

## **Team Members:** 
- Amogh Sriram: Project Manager
- Derek Wang: Data Engineer 1
- Michelle Dao: Data Engineer 2
- Olsen Arliawan: Data Enginer 3
- Tirth Thakkar: Storyteller 

## Project Overview 📁
This project implements a scalable big data pipeline to analyze professional CS:GO match snapshots. We utilize a distributed **Apache Spark** environment on **Google Cloud Platform (GCP)** to:
**Predict Round Winners**: A Random Forest Machine Learning model predicting outcomes based on team economy, equipment, and live game state.
**Analyze Meta-Strategy**: A high-volume aggregation of weapon usage to identify winning loadout strategies.

## Infrastructure & Configuration
- Cloud Provider: Google Cloud Platform (GCP)
- Compute Resource: Google Compute Engine VM
- Machine Type: e2-standard-4 (4 vCPUs, 16 GB RAM)
- Operating System: Ubuntu 22.04 LTS
- Software Stack:
  - Java 17 (OpenJDK)
  - Python 3.10
  - Apache Spark (PySpark)
  - Google Cloud SDK (gsutil)

## Distributed Execution Config:
The Spark job is configured with .master("local[*]"). This forces Spark to ignore the single-machine nature of the VM and spawn executors across all 4 vCPUs, enabling parallel processing of the 122,000+ round snapshots.

## Data Architecture
Data Architecture
| Input Data | gs://team10-csgo-data/csgo_round_snapshots.csv | Raw dataset (~45MB) containing 97 features per round. |
| Processing | csgo_vm_job.py (On VM) | PySpark ETL and ML pipeline script. |
| Output Data | gs://team10-csgo-data/final_output/ | Processed artifacts including model metrics and analytical tables. |

## Quick Start: Execution Guide
Follow these steps to reproduce the distributed run on a fresh GCP VM.

1. Environment Setup
SSH into the VM and install the necessary dependencies for Spark and GCS.
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk python3-pip
pip3 install pyspark numpy pandas

2. Stage Data (Ingest)
Download the raw data from the Object Storage bucket to the local distributed environment.
mkdir csgo_project
cd csgo_project
gsutil cp gs://team10-csgo-data/csgo_round_snapshots.csv .

3. Run the Distributed Job
Execute the Spark pipeline. This script initializes the Spark Session, performs cleaning, trains the Random Forest model, and aggregates weapon stats.
python3 csgo_vm_job.py

4. Persist Results (Egest)
Upload the processed results back to the Cloud Storage Bucket for persistent storage.
gsutil cp -r out_metrics gs://team10-csgo-data/final_output/
gsutil cp -r out_weapons gs://team10-csgo-data/final_output/

## Final Output Artifacts: 
The pipeline generates two primary CSV artifacts located in the `final_output/` folder on GCS.

Artifact 1: Model Metrics (out_metrics)
A CSV file containing the Area Under ROC (AUC) score for the Random Forest model.
Current Performance: ~83.3% Accuracy.
Key Features Used: t_armor, t_helmets, ct_defuse_kits, map_idx, bomb_planted.

Artifact 2: Weapon Meta-Analysis (out_weapons)
A tabular breakdown of the most effective weapons for the Counter-Terrorist (CT) side.
Insight: The data reveals that while the M4A4 is the standard issue rifle, the AK-47 (a Terrorist weapon) appears in the Top 5, indicating that "scavenging" enemy weapons is a statistically significant winning strategy.

## Source Code Description (csgo_vm_job.py)
Initialization: Sets up Spark with 4GB driver memory and multi-core mastering.
Preprocessing:
Converts round_winner to binary labels (1.0/0.0).
Uses StringIndexer to convert Map names into numerical features.
Machine Learning:
Uses VectorAssembler to combine 15+ features (Economy, Live State, Map).
Trains a Random Forest Classifier (50 Trees).
Evaluates using Binary Classification Evaluator.
Aggregation:
Dynamically identifies 30+ weapon columns.
Uses Spark SQL transformations (stack, sum) to pivot and rank weapon efficiency without using inefficient Python loops.

## Evidence of Distributed Cloud Run ☁️

1. **Distributed Processing (CPU Usage)**  
   ![Distributed CPU Usage](https://github.com/sjsu-cs131-f25/Group10-CSGO-winprob/raw/main/final_project/Evidence%20of%20the%20Distributed%20Run/Distributed_Evidence.png)

2. **Successful Execution Logs**  
   ![Successful Execution Logs](https://github.com/sjsu-cs131-f25/Group10-CSGO-winprob/raw/main/final_project/Evidence%20of%20the%20Distributed%20Run/Final_Results.png)

3. **Spark Job Page**
  ![Spark Job Page](https://github.com/sjsu-cs131-f25/Group10-CSGO-winprob/blob/main/final_project/Evidence%20of%20the%20Distributed%20Run/SparkJobs1.png)
 ![Spark Job Page](https://github.com/sjsu-cs131-f25/Group10-CSGO-winprob/blob/main/final_project/Evidence%20of%20the%20Distributed%20Run/SparkJobs2.png)

4. **Cloud Storage Persistence**  
   ![Cloud Storage Persistence](https://github.com/sjsu-cs131-f25/Group10-CSGO-winprob/raw/main/final_project/Evidence%20of%20the%20Distributed%20Run/Cloud_Persistence.png)


