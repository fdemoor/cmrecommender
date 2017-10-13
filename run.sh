#!/bin/bash

echo "Retrieving parameters"

DATASET_PATH=datasets/test.csv
NB_FOLDS=10
OUTPUT_DIRECTORY=output/test/cosine_kEval
SEED=27638663697938
USE_CM=1
GAMMA=0.01
ERROR=10.0
DEPTH=3

mkdir -p "ser"
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

export SBT_OPTS="-Xmx256M -Xms256M"

echo "Running"

sbt "-DOUTPUT_DIR=$OUTPUT_DIRECTORY" "run $params"
