#!/usr/bin/env bash

base=${0%/*}/;

# invoke maven versions plugin to increment project structure versions
mvn -f $base/pom.xml versions:set -DnewVersion=$1 || exit 1

# switch into goci-dependencies and increment all project module versions
mvn -f $base/goci-dependencies/pom.xml versions:set -DnewVersion=$1 || exit 1

# finally replace version number property in goci-dependencies pom with new version
sed -i 's/\(<goci.version>\)\([^<]*\)\(<\/goci.version>\)/\12.0.0-SNAPSHOT\3/g' $base/goci-dependencies/pom.xml || exit 1


