FROM openjdk:8u212-jdk-alpine
RUN addgroup -S goci && adduser -S goci -G goci


# Create log file directory and set permission
RUN if [ ! -d /home/goci/logs/ ];then mkdir -p /home/goci/logs/gwas_pussycat/;fi
RUN chown -R goci:goci /home/goci/

VOLUME /tmp

ADD application.properties \
goci-pussycat/src/main/resources/build.properties \
goci-sparql-renderlets/src/main/resources/prefix.properties \
goci-pussycat/target/goci-pussycat-?.?.?.jar \
/home/goci/

WORKDIR /home/goci
# Move project artifact
USER goci
# Launch application server
ENTRYPOINT exec $JAVA_HOME/bin/java -Xmx4g -Dcatalina.base=/home/goci \
-Dspring.profiles.active=$ENVIRONMENT \
-Dspring.jmx.enabled=false \
 -Dlogback.statusListenerClass=ch.qos.logback.core.status.OnConsoleStatusListener \
 -Xmx4g \
 -DentityExpansionLimit=10000 \
 -Djavax.servlet.request.encoding=UTF-8 \
 -Dfile.encoding=UTF-8 \
 -Dspring.config.location=application.properties,build.properties,prefix.properties \
 -jar goci-pussycat-?.?.?.jar

EXPOSE 8080

