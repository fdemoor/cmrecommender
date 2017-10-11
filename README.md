# Privacy-preserving recommender system using count-min sketches 

This project aims to evaluate the privacy / accuracy trade-off of a recommender system that uses a new privacy approach, based on count-min sketches.

## Getting started

### Prerequisites

We use a modified version of Apache Mahout, available [here](https://github.com/fdemoor/mahout).
The jars obtained after compilation of Mahout modules *mr* and *math* are to be put in the *lib* folder.

### Installing and running

The project is built using sbt. Type `sbt "compile"` to compile.

A bash script is used to run, simply type `./run.sh`. Parameters can be changed by editing the values of some variables in the script.
