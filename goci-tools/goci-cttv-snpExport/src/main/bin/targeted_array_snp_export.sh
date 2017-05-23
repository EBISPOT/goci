#!/bin/bash

# This script is meant for the SNP export of targeted array studies. 
# The output should be redirected to a file and appended to the snp export that is generated from the goci-cttv-snpExport tool in goci 
# $1 the .tsv file with the targeted array studies

file=$1

awk -F"\t" -vOFS="\t" 'BEGIN {chrN=""; from=0; to=0; ref="NA"; allele=""; strand="NA"; type="NA"; rsid=""; fclass="";}{

if (FNR == 1){
    for(i=1; i<=NF; i++){
        if($i == "CHR_ID"){
            chrN = i;
        }
        else if($i == "CHR_POS"){
            from = i;
            to = i;
        }
        else if ($i == "STRONGEST SNP-RISK ALLELE"){
            allele = i;
        }
        else if ($i == "SNPS"){
            rsid = i;
        }
        else if ($i == "CONTEXT"){
            fclass = i;
        }

    }
} else {
    if ($from ~ /:/){
        split ($from, a, ":");
        $from = a[1];
        $to = a[2];
    }
    if($allele ~ /-/){
        split ($allele, b, "-");
        if (b[2] == "?"){
            b[2] = "NA";
        }
        $allele = b[2];
    }
    if ($chrN == ""){
        $chrN = "NA";
    }
    if ($from == ""){
        $from = "NA";
    }

    if ($to == ""){
        $to = "NA";
    }

    if ($allele == ""){
        $allele = "NA";
    }

    if ($rsid == ""){
        $rsid = "NA";
    }

    if ($fclass == ""){
        $fclass = "NA";
    }
   print $chrN, $from, $to, ref, $allele, strand, type, $rsid, $fclass;
}

}' $file 
