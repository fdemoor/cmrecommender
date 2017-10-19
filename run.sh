#!/bin/bash


################################################################################
# Parameters with argument, change the values below if needed                  #
################################################################################

#DATASET_PATH=datasets/test.csv
DATASET_PATH=datasets/ml-100k/ratings.csv
                  # Path to the dataset .csv file
NB_FOLDS=10       # Number of folds in RMSE cross-validation
SEED=7263789638   # Seed for PRNG (currently not used)
GAMMA=0.01        # Gamma value required if CM sketches are used
BETAC=0.40        # Cosine error value required if CM skeches are used
BETAP=0.30        # Point error value required if CM skeches are used
OUTPUT_DIRECTORY=output/test/cosineCM_${GAMMA}_${BETAC}_${BETAP}_kEval
#OUTPUT_DIRECTORY=output/test/cosine_kEval
                  # Path to the folder where to output logs

################################################################################
# Boolean parameters, comment a line to remove the associated option           #
################################################################################

CM="-CM"          # Use count-min sketch based similarity, cosine otherwise
#PDIST="-pDist"    # Compute profile size distribution of the dataset
KEVAL="-runK"     # Run evaluation with different k (as in kNN) values
#EWEVAL="-runEW"   # Run error / width evaluation


################################################################################
# Create necessary folders if not existing                                     #
# Generate the parameter string                                               #
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
-gamma $GAMMA
-bc $BETAC
-bp $BETAP
$PDIST
$KEVAL
$EWEVAL
"

export SBT_OPTS="-Xmx256M -Xms256M"
#export SBT_OPTS="-Xmx16G -Xms1G"

echo "Running.."

sbt "-DOUTPUT_DIR=$OUTPUT_DIRECTORY" "run $params"

echo "Done."
