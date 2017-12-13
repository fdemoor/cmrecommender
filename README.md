# Privacy-preserving recommender system using count-min sketches 

This project aims to evaluate the privacy / accuracy trade-off of a recommender system that uses a new privacy approach, based on count-min sketches.

## Getting started

### Prerequisites

We use a modified version of Apache Mahout, available [here](https://github.com/fdemoor/mahout).
The jars obtained after compilation of Mahout modules *mr* and *math* are to be put in the *lib* folder.

To run the Python scripts, the following libraries are required: numpy, matplotlib, seaborn, statistics.

### Installing and running

The project is built using sbt. Type `sbt "compile"` to compile.

A bash script is used to run, simply type `./run.sh`.
Parameters can be changed by editing the values of some variables in the script (dataset path, output path, sketch based cosine similarity or classic cosine, JVM options, ..).

## Python Scripts (*/scripts* folder)

### plot.py

This script plots the RMSE values.
You can specify the folder where the data is located (*basedir* variable on line 13) and the names of the different data categories you want to plot (*dirs* variable on line 64).
Simply type `python plot.py` to run.
The output plots will be saved in the data base folder.

### dist.py

This script plots some distribution information about the dataset.
You can specify the data on lines 15 and 16 (*q* and *basedir* variables).
The output plots will be saved in the data base folder.
It plots the profile size distribution, and (if the sketch based cosine was used) the exact retrieve probability / guessability for each user.
Simply type `python dist.py` to run.

### filterProfileSize.py

This script is used to produce a new dataset from another one by filtering users based on their profile size.
The parameters can be changed in the first lines of the script, i.e. the input dataset, the destination folder, and the filtering.
Simply type `python filterProfileSize.py` to run.

## Python Scripts (*/scripts/cm_simulations* folder)

You can run one of the *run_.py* scripts.
Various parameters can be set at the beginning of the script.
The result is output in a serialized file *data.p*.
You can then type `python plot_results.py <data.p>` to produce the graphs.
