#!/bin/sh

base=${0%/*}/..;
current=`pwd`;
#java=/ebi/research/software/Linux_x86_64/opt/java/jdk1.7/bin/java;
java=java;
#args="-Dhttp.proxyHost=wwwcache.ebi.ac.uk -Dhttp.proxyPort=3128 -Dhttp.nonProxyHosts=*.ebi.ac.uk -DproxyHost=wwwcache.ebi.ac.uk -DproxyPort=3128 -DproxySet=true -Xmx16g";
args="";

for file in `ls $base/lib`
do
  jars=$jars:$base/lib/$file;
done

classpath="$jars:$base/config";

$java $args -classpath $classpath uk.ac.ebi.fgpt.goci.KBMetricsDriver $@ 2>&1;
exit $?;