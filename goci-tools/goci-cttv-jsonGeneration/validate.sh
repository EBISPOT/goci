#!/bin/bash

#
# Validate a gwas.json file
#
# To run : 
# ./validate.sh PATH_TO_DIR_CONTAINING_GWAS_JSON_FILE PATH_TO_LOCAL_CHECKOUT_OF_CTTV_JSON_SCHEMA
# eg. : 
#  ./validate.sh /Users/catherineleroy/cttv1.1.2_generation_test/goci_GOCI-925/goci-tools/goci-cttv-json-generator/ /Users/catherineleroy/Applications/json_schema/
#
# !!!!! PATH_TO_DIR_CONTAINING_GWAS_JSON_FILE must contain the gwas.json file and must be a writable directory so that this script can create a gwas-test.json containing only one json string for testing. !!!!!!!!
#
# To get a local copy of the cttv schema just do : 
# git clone  https://github.com/CTTV/json_schema.git
#
# To run this script you will need to have z-schema installed. To install it : 
#  npm install --global z-schema
#
#

GWAS_JSON_PATH=$1/gwas.json
CTTV_SCHEMA_PATH=$2/examples
GWAS_JSON_TEST_PATH=$1/gwas-test.json


cd $CTTV_SCHEMA_PATH

LINE_COUNT=`wc -l < $GWAS_JSON_PATH`

echo $LINE_COUNT

COUNTER=1
while [  $COUNTER -lt $LINE_COUNT ]; do
	echo "checking json string number : $COUNTER out of $LINE_COUNT"
    head -n $COUNTER $GWAS_JSON_PATH | tail -n 1 > $GWAS_JSON_TEST_PATH
	EXIT_CODE=`z-schema ../src/genetics.json $GWAS_JSON_TEST_PATH`

	pwd
	if [ "$EXIT_CODE" -ne "0" ]; then
		echo 'problem with file $GWAS_JSON_TEST_PATH'
		exit 1
	fi
	echo "Checking done, all OK!"
    echo
    echo
	let COUNTER=COUNTER+1
done

exit 0;
