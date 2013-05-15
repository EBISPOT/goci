#!/bin/sh

base=${0%/*}/..;
current=`pwd`;
java=java;
args="";

for file in `ls $base/lib`
do
 jars=$jars:$base/lib/$file;
done

classpath="$jars:$base/config";

$java $args -classpath $classpath uk.ac.ebi.fgpt.goci.PubmedSearchDriver $@ 2>&1