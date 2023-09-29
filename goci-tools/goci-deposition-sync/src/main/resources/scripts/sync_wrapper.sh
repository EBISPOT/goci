
base=${0%/*}/;
#proxy_settings="-Dhttp.proxyHost=www-proxy.ebi.ac.uk -Dhttp.proxyPort=3128 -Dhttp.nonProxyHosts=*.ebi.ac.uk -Dftp.proxyHost=www-proxy.ebi.ac.uk -Dftp.proxyPort=3128 -Dftp.nonProxyHosts=*.ebi.ac.uk -DproxyHost=www-proxy.ebi.ac.uk -DproxyPort=3128 -DproxySet=true";

java -Dlogback.statusListenerClass=ch.qos.logback.core.status.OnConsoleStatusListener -Dspring.config.location=/hps/software/users/parkinso/spot/gwas/prod/sw/deposition_sync/config/application.properties -Dlogging.file.path=/hps/nobackup/parkinso/spot/gwas/logs/deposition_sync  -cp $base -jar $base/goci-deposition-sync.jar $@
#java -Dlogback.statusListenerClass=ch.qos.logback.core.status.OnConsoleStatusListener -Dspring.config.location=/nfs/spot/sw/dev/gwas-dev2/config/application.properties,$base/config/application.properties $proxy_settings -cp $base -jar $base/goci-deposition-sync.jar unpublished $@
exit $?