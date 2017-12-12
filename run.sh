#!/bin/bash


################################################################################
# Parameters with argument, change the values below if needed                  #
################################################################################

#DATASET_PATH=datasets/test.csv
DATASET_PATH=datasets/ml-100k/ratings.csv
                  # Path to the dataset .csv file
NB_FOLDS=10       # Number of folds in RMSE cross-validation
SEED=7263789638   # Seed for PRNG (currently not used)
q=1.0        # Privacy/accuracy trade-off parameter
OUTPUT_DIRECTORY=output/ml-100k/cosineCM_${q}_dist
#OUTPUT_DIRECTORY=output/ml-100k/cosine_kEval
                  # Path to the folder where to output logs

################################################################################
# Boolean parameters, comment a line to remove the associated option           #
################################################################################

CM="-CM"          # Use count-min sketch based similarity, cosine otherwise
DIST="-runDist"    # Compute several distributions of the dataset
KEVAL="-runK"     # Run evaluation with different k (as in kNN) values


################################################################################
# Create necessary folders if not existing                                     #
# Generate the parameter string                                                #
# Set some JVM options                                                         #
# Run the experiment                                                           #
################################################################################

mkdir -p "ser"
mkdir -p $OUTPUT_DIRECTORY

params="
-d $DATASET_PATH
-N $NB_FOLDS
-s $SEED
$CM
-q $q
$DIST
$KEVAL
"

export SBT_OPTS="-Xmx256M -Xms256M"
#export SBT_OPTS="-Xmx16G -Xms1G"

echo "Running.."

sbt "-DOUTPUT_DIR=$OUTPUT_DIRECTORY" "run $params"

echo "Done."
