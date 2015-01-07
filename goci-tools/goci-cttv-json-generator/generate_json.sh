#!/bin/sh

# The directory from which this script is started which will be used as a workign directory
LOCAL_DIR=`pwd`

# The  path to the github input_data_format/scripts/cttv0009_gwas_catalog directory
# If you don't have it : git clone https://github.com/CTTV/input_data_format
# However you should really have this project as it is where this script is stored :-)
THIS_SCRPIT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
echo $THIS_SCRPIT_DIR
INPUT_DATA_FORMAT_CTTV=$THIS_SCRPIT_DIR/..


# Create a release working directory with the date
DATE=`date +%Y-%m-%d`
RELEASE_DIR=$LOCAL_DIR/release_$DATE
mkdir $RELEASE_DIR

#
# The location of the publish.sh script
# If you don't have one : 
#    git clone  https://github.com/tburdett/goci
#    cd goci/goci-tools/goci-datapublisher/
#    mvn clean package
#    cd target
#    unzip goci-datapublisher.zip
#    cd bin
#    chmod gou+x publish.sh
#
PUBLISH_SCRIPT_DIR=THIS_SCRPIT_DIR/../goci-datapublisher/target/bin

# For this script to work you need to install virtuoso 6.1.8 first
# And the virtuoso-t executable need to be added to your path
# The virtuoso_accessible directory is a directory that is accessible by virtuoso (this can be configured in virtuoso config file)
VIRTUOSO_ACCESSIBLE=/Users/catherineleroy/Documents/virtuoso_accessible

# The path to the virtuoso/var/lib/virtuoso/db/virtuoso.ini file for virtuoso startup
VIRTUOSO_INI_DIR=/Users/catherineleroy/Applications/virtuoso/var/lib/virtuoso/db

cd $LOCAL_DIR

# root password to start virtuoso... mmmmhh... could do better in term of safety...
PASSWORD='wtmbahand8'

# Path to the 'to be created' gwas.json file
JSON_OUTPUT=$RELEASE_DIR/gwas.json

#Url to virtuoso server
HOST=http://localhost:8890

#Path to the snp to gene mapping file that is going to be created
MAPPING_FILE=$RELEASE_DIR/snp_2_gene_mapping.tab

#Path to the file containing the snps that did not map to any gene
FAILED_FILE=$RELEASE_DIR/failed_snp.tab

##############################
# Generate the gwas.owl file #
##############################
echo "Generate the gwas.owl file"
echo "..."
LOG_FILE=$RELEASE_DIR/publish.log
COMMAND="$PUBLISH_SCRIPT_DIR/publish.sh -p 1E-5 -o $VIRTUOSO_ACCESSIBLE/gwas.owl >> $LOG_FILE 2>&1"
eval $COMMAND
if [ $? -ne 0 ]
then
   echo "Command '$COMMAND' did not worked. Look at the log file : '$LOG_FILE'"
   #exit 1
fi
echo ""

#############################
# Start the virtuoso server #
#############################
echo "Start the virtuoso server"
echo "..."
cd $VIRTUOSO_INI_DIR
LOG_FILE=$RELEASE_DIR/start-virtuoso.log
COMMAND="echo $PASSWORD | sudo -S virtuoso-t -f >> $LOG_FILE 2>&1 &"
eval $COMMAND
if [ $? -ne 0 ]
then
   echo "Command '$COMMAND' did not worked. Look at the log file : '$LOG_FILE'"
   exit 1
fi
echo ""

#########################################
# Wait for the virtuoso server to start #
#########################################
echo "Wait for the virtuoso server to start"
echo "..."
echo ""
sleep 5

###########################################################
# Delete all the previously loaded triplets from virtuoso #
###########################################################

echo "Delete all the previously loaded triplets from virtuoso"
echo "..."
cd $LOCAL_DIR
LOG_FILE=$RELEASE_DIR/delete_all_triples.log
COMMAND="$THIS_SCRIPT_DIR/delete_all_triples.sh  >> $LOG_FILE 2>&1"
eval $COMMAND
if [ $? -ne 0 ]
then
   echo "Command '$COMMAND' did not worked. Look at the log file : '$LOG_FILE'"
   exit 1
fi
sleep 5
echo ""

############################################
# Load the triplets from the gwas.owl file #
############################################

echo "Load the triplets from the gwas.owl file"
echo "..."
LOG_FILE=$RELEASE_DIR/load_gwas_rdf.log
COMMAND="$THIS_SCRIPT_DIR/load_gwas_rdf.sh  >> $LOG_FILE 2>&1"
eval $COMMAND
if [ $? -ne 0 ]
then
   echo "Command '$COMMAND' did not worked. Look at the log file : '$LOG_FILE'"
   exit 1
fi
sleep 20
echo ""


####################################################################
# Use the newly loaded virtuoso to generate the gwas.json for cttv #
####################################################################

# Generate mapping
#------------------
echo "Use the newly loaded virtuoso to generate the gwas.json for cttv"
echo "..."
LOG_FILE=$RELEASE_DIR/snp_2_gene_mapping_generator.log
COMMAND="perl $THIS_SCRIPT_DIR/snp_2_gene_mapping_generator.pl $MAPPING_FILE $FAILED_FILE >> $LOG_FILE 2>&1" 
echo "bla $COMMAND"
eval $COMMAND
wait $!
echo ""

# Generate gwas
#--------------
echo "Generate the gwas.json file"
echo "..."
LOG_FILE=$RELEASE_DIR/gwas-sparql2json-bis.log
COMMAND="python $THIS_SCRIPT_DIR/gwas-sparql2json.py $JSON_OUTPUT $MAPPING_FILE $HOST  >> $LOG_FILE 2>&1"
eval $COMMAND
if [ $? -ne 0 ]
then
   echo "Command '$COMMAND' did not worked. Look at the log file : '$LOG_FILE'"
   exit 1
fi
echo "" 


#######################
# Switch off virtuoso #
#######################
echo "Shuting down virtuoso"
echo "..."
LOG_FILE=$RELEASE_DIR/shutdown_virtuoso.log
COMMAND="$THIS_SCRIPT_DIR/shutdown_virtuoso.sh  >> $LOG_FILE 2>&1"
eval $COMMAND
if [ $? -ne 0 ]
then
   echo "Command '$COMMAND' did not worked. Look at the log file : '$LOG_FILE'"
   exit 1
fi
echo ""
