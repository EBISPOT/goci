#!/bin/bash

# Constructs the final input to be given to the evidence generator java goci tool

# file 1 is snp to gene file generated from OTAR, should be cleaned from the VEP errors
# file 2 is the .tsv targeted array file 

file1=$1
file2=$2

./targeted_array_info_export.sh $file2 > info.txt

file2="info.txt";

while read line
do
    rsid=$(echo "$line" | awk 'BEGIN{FS="\t"}{print $1}')

    if  grep -q "$rsid" $file2 ;then
        grep "$rsid" $file2 | while read -r l; do
        
            pmid=$(echo "$l" | awk 'BEGIN{FS="\t"}{print $2}')
            efo=$(echo "$l" | awk 'BEGIN{FS="\t"}{print $3}')
            pval=$(echo "$l" | awk 'BEGIN{FS="\t"}{print $4}')
            size=$(echo "$l" | awk 'BEGIN{FS="\t"}{print $5}')
            snpN=$(echo "$l" | awk 'BEGIN{FS="\t"}{print $6}')

            echo -e "$line\t1\t$pmid\t$efo\t$pval\t$size\t$snpN"
        done
    fi
done < $file1
