#!/bin/bash

echo "Retrieving parameters"

DATASET_PATH=datasets/test.csv
NB_FOLDS=10
OUTPUT_DIRECTORY=output/cosine_kEval
SEED=27638663697938
USE_CM=0
GAMMA=0.1
ERROR=1.0
DEPTH=3

mkdir -p $OUTPUT_DIRECTORY

if [ "$USE_CM" -eq "1" ]; then
  CM="-CM"
else
  CM=""
fi

params="
-d $DATASET_PATH
-N $NB_FOLDS
-s $SEED
$CM
-depth $DEPTH
-gamma $GAMMA
-error $ERROR
"

echo "Running"

sbt "-DOUTPUT_DIR=$OUTPUT_DIRECTORY" "run $params"
