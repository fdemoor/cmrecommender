#!/bin/bash

echo "Retrieving parameters"

DATASET_PATH=datasets/test.csv
NB_FOLDS=10
OUTPUT_DIRECTORY=output/test
SEED=27638663697938

mkdir -p $OUTPUT_DIRECTORY

params="
-d $DATASET_PATH
-N $NB_FOLDS
-s $SEED
"

echo "Running"

sbt "-DOUTPUT_DIR=$OUTPUT_DIRECTORY" "run $params"
