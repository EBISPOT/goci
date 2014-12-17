#!/bin/sh

    base=${0%/*}/..;
    current=`pwd`;
    java=${java.location};
    args="${java.args}";

    for file in `ls $base/lib`
    do
      jars=$jars:$base/lib/$file;
    done

    classpath="$jars:$base/config";

    $java $args -classpath $classpath uk.ac.ebi.spot.goci.GOCIUriLabelCheckerDriver $@ 2>&1;
    exit $?;