#!/bin/bash

# the file that exports the info that we want from the targeted array file
# $1 the .tsv targeted array file to be parsed

file=$1

awk -F"\t" -vOFS="\t" 'BEGIN {pubmedid=""; efo=""; pval=0; size=""; snpcount=100000; rsid="";}{

if (FNR == 1){
    for(i=1; i<=NF; i++){
        if($i == "PUBMEDID"){
            pubmedid = i;
        }
        else if($i == "URI"){
            efo = i;
        }
        else if ($i == "P-VALUE"){
            pval = i;
        }
        else if ($i == "INITIAL SAMPLE SIZE"){
            size = i;
        }
        else if ($i == "SNPS"){
            rsid = i;
        }
        else if ($i ~ /PLATFORM/) {
            snpcount = i;
        } 
    }
} else {
    ss = $size;
    gsub(/,/,"", ss)
    split(ss, a, " ");
    total = 0;
    for(x in a){
        if(a[x] ~ /[0-9]/){
            total = total + a[x];
        }
    }
   
    gsub(/E/,"e",$pval);
    
    snpc = $snpcount;
    gsub(/[A-Za-z\[\]\~\(\)\ ]/, "", snpc);

    if($efo ~ /,/){
        traits = $efo;
        split(traits, t, ",");
        for (x in t){
             print $rsid, $pubmedid, t[x], $pval, total, snpc;
        }
    } else {
        print $rsid, $pubmedid, $efo, $pval, total, snpc;
    }   
}

}' $file 
