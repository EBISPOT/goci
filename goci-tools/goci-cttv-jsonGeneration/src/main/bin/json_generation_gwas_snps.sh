#!/bin/bash

# Constructs the final input to be given to the evidence generator java goci tool

# file 1 is snp to gene file generated from OTAR, should be cleaned from the VEP errors
# file 2 is the .tsv file of snps that where exported from the goci snpExport tool and where not merged with the targeted array snps 

file1=$1
file2=$2

while read line
do
    rsid=$(echo "$line" | awk 'BEGIN{FS="\t"}{print $1}')

    if  grep -q "$rsid" $file2 ;then
        grep "$rsid" $file2 | while read -r l; do
        
            echo -e "$line\t0"
        done
    fi
done < $file1
