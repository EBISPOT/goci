#!/bin/sh

# The directory from which this script is started which will be used as a workign directory
LOCAL_DIR=`pwd`

git clone https://github.com/tburdett/goci
cd goci
mvn clean