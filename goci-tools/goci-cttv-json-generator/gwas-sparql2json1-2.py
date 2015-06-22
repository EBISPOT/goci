__author__ = 'Tony Burdett'

import sys
import datetime

import urllib
import csv
import subprocess
import json

# usage : python gwas-sparql2json-1.py [1or0]
# ex : python gwas-sparql2json-1.py /my/path/gwas.json /my/path/snp2geneMapping.txt the/url/to/sparql/end/point
# ex : http://localhost:8890/
# arg 1 : the path to the 'to be created' gwas.json file
# arg 2 : path to the snp to gene mapping file
# tab delimited file containing line like : snp_dbSnp_rsId[\t]ensembl_gene_label[\t]ensembl_gene_id
#         eg. : rs10255878	AP1S2P1	ENSG00000226046
#               rs10256972	C7orf50	ENSG00000146540
#               rs10259085	C1GALT1	ENSG00000106392

jsonFilePath = sys.argv[1]
snp2geneMappingFilePath = sys.argv[2]
print snp2geneMappingFilePath
sparqlEndPoint = sys.argv[3]






# first, load the mappings file (rsid -> gene) as a dictionary
# rs227724        ENSG00000183691 upstream_gene_variant
# rs7503807       ENSG00000141564 intron_variant
# rs2240466       ENSG00000009954 intron_variant
# rs2488389       ENSG00000213047 intron_variant
# rs10508372      ENSG00000234752 upstream_gene_variant
snpGeneMappings = {}
with open(snp2geneMappingFilePath) as mappings:
    for a in xrange(1):
        next(mappings)
    for line in csv.reader(mappings, delimiter="\t"):
        if not line[0].startswith("#"):
            snp=line[0]
            snp=snp.strip()
            gene=line[1]
            gene=gene.strip()
            so_string = line[2]
            so_string = so_string.strip()
            print "line0=  ", snp
            print "line1=  ", gene
            print "line2=  ", line[2]
        if not snp in snpGeneMappings:
            snpGeneMappings[snp] = list()
        gene2soLabel = gene + ":" + so_string;
        snpGeneMappings[snp].append(gene2soLabel)

print "\n\n\n\n"


# clean existing output file
clean = open(jsonFilePath, "w")
clean.close()



# now get sparql results from GWAS catalog
# 2 triplets only
#sparqlurl = sparqlEndPoint + "/sparql?default-graph-uri=&query=PREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0APREFIX+rdfs%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0D%0APREFIX+owl%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0D%0APREFIX+xsd%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%0D%0APREFIX+dc%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0D%0APREFIX+dcterms%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Fterms%2F%3E%0D%0APREFIX+oban%3A+%3Chttp%3A%2F%2Fpurl.org%2Foban%2F%3E%0D%0APREFIX+ro%3A+%3Chttp%3A%2F%2Fwww.obofoundry.org%2Fro%2Fro.owl%23%3E%0D%0APREFIX+efo%3A+%3Chttp%3A%2F%2Fwww.ebi.ac.uk%2Fefo%2F%3E%0D%0APREFIX+gt%3A+%3Chttp%3A%2F%2Frdf.ebi.ac.uk%2Fterms%2Fgwas%2F%3E%0D%0APREFIX+gd%3A+%3Chttp%3A%2F%2Frdf.ebi.ac.uk%2Fdataset%2Fgwas%2F%3E%0D%0A%0D%0ASELECT+DISTINCT+%3Fsnp+%3Fpubmed_id+%3Fdate+%3Fpvalue+%3Ftrait%0D%0AWHERE+%7B%0D%0A++%3Fassociation+a+gt%3ATraitAssociation+%3B%0D%0A+++++++++++++++gt%3Ahas_p_value+%3Fpvalue+%3B%0D%0A+++++++++++++++oban%3Ahas_subject+%3Fsnp+%3B%0D%0A+++++++++++++++oban%3Ahas_object+%3Ftrait+%3B%0D%0A+++++++++++++++ro%3Apart_of+%3Fstudy+.%0D%0A++%0D%0A++%3Fstudy+gt%3Ahas_pubmed_id+%3Fpubmed_id+.%0D%0A++%3Fstudy+gt%3Ahas_publication_date+%3Fdate+.%0D%0A%7D%0D%0ALIMIT+2&format=text%2Ftab-separated-values&timeout=0&debug=on"

#all
#sparqlurl = sparqlEndPoint + "/sparql?default-graph-uri=&query=PREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0APREFIX+rdfs%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0D%0APREFIX+owl%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0D%0APREFIX+xsd%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%0D%0APREFIX+dc%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0D%0APREFIX+dcterms%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Fterms%2F%3E%0D%0APREFIX+oban%3A+%3Chttp%3A%2F%2Fpurl.org%2Foban%2F%3E%0D%0APREFIX+ro%3A+%3Chttp%3A%2F%2Fwww.obofoundry.org%2Fro%2Fro.owl%23%3E%0D%0APREFIX+efo%3A+%3Chttp%3A%2F%2Fwww.ebi.ac.uk%2Fefo%2F%3E%0D%0APREFIX+gt%3A+%3Chttp%3A%2F%2Frdf.ebi.ac.uk%2Fterms%2Fgwas%2F%3E%0D%0APREFIX+gd%3A+%3Chttp%3A%2F%2Frdf.ebi.ac.uk%2Fdataset%2Fgwas%2F%3E%0D%0A%0D%0ASELECT+DISTINCT+%3Fsnp+%3Fpubmed_id+%3Fdate+%3Fpvalue+%3Ftrait%0D%0AWHERE+%7B%0D%0A++%3Fassociation+a+gt%3ATraitAssociation+%3B%0D%0A+++++++++++++++gt%3Ahas_p_value+%3Fpvalue+%3B%0D%0A+++++++++++++++oban%3Ahas_subject+%3Fsnp+%3B%0D%0A+++++++++++++++oban%3Ahas_object+%3Ftrait+%3B%0D%0A+++++++++++++++ro%3Apart_of+%3Fstudy+.%0D%0A++%0D%0A++%3Fstudy+gt%3Ahas_pubmed_id+%3Fpubmed_id+.%0D%0A++%3Fstudy+gt%3Ahas_publication_date+%3Fdate+.%0D%0A%7D%0D%0A&format=text%2Ftab-separated-values&timeout=0&debug=on"


#all
sparqlurl = sparqlEndPoint + "/sparql?default-graph-uri=&query=%0D%0APREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0APREFIX+rdfs%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0D%0APREFIX+owl%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0D%0APREFIX+xsd%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%0D%0APREFIX+dc%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0D%0APREFIX+dcterms%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Fterms%2F%3E%0D%0APREFIX+oban%3A+%3Chttp%3A%2F%2Fpurl.org%2Foban%2F%3E%0D%0APREFIX+ro%3A+%3Chttp%3A%2F%2Fwww.obofoundry.org%2Fro%2Fro.owl%23%3E%0D%0APREFIX+efo%3A+%3Chttp%3A%2F%2Fwww.ebi.ac.uk%2Fefo%2F%3E%0D%0APREFIX+gt%3A+%3Chttp%3A%2F%2Frdf.ebi.ac.uk%2Fterms%2Fgwas%2F%3E%0D%0APREFIX+gd%3A+%3Chttp%3A%2F%2Frdf.ebi.ac.uk%2Fdataset%2Fgwas%2F%3E%0D%0A%0D%0ASELECT+DISTINCT+%3Fsnp+%3Fpubmed_id+%3Fdate+%3Fpvalue+%3Ftrait%0D%0AWHERE+%7B%0D%0A++%3Fassociation+a+gt%3ATraitAssociation+%3B%0D%0A+++++++++++++++gt%3Ahas_p_value+%3Fpvalue+%3B%0D%0A+++++++++++++++oban%3Ahas_subject+%3Fsnp+%3B%0D%0A+++++++++++++++oban%3Ahas_object+%3Ftrait+%3B%0D%0A+++++++++++++++ro%3Apart_of+%3Fstudy+.%0D%0A++%0D%0A++%3Fstudy+gt%3Ahas_pubmed_id+%3Fpubmed_id+.%0D%0A++%3Fstudy+gt%3Ahas_publication_date+%3Fdate+.%0D%0A%7D&format=text%2Ftab-separated-values&timeout=0&debug=on"

sparqlresults = urllib.urlretrieve(sparqlurl, "sparql-retrieved-results.txt")


json = {}

noMappingCount = 0
totalCount = 0

soName2soId = {"3_prime_UTR_variant" : "http://purl.obolibrary.org/obo/SO_0001624","5_prime_UTR_variant" : "http://purl.obolibrary.org/obo/SO_0001623","coding_sequence_variant" : "http://purl.obolibrary.org/obo/SO_0001580","frameshift_variant" : "http://purl.obolibrary.org/obo/SO_0001589","intron_variant" : "http://purl.obolibrary.org/obo/SO_0001627","missense_variant" : "http://purl.obolibrary.org/obo/SO_0001583","nearest_gene_five_prime_end" : "http://targetvalidation.org/sequence/nearest_gene_five_prime_end","non_coding_transcript_exon_variant" : "http://purl.obolibrary.org/obo/SO_0001792","splice_acceptor_variant" : "http://purl.obolibrary.org/obo/SO_0001574","splice_donor_variant" : "http://purl.obolibrary.org/obo/SO_0001575","splice_region_variant" : "http://purl.obolibrary.org/obo/SO_0001630","stop_gained" : "http://purl.obolibrary.org/obo/SO_0001587","stop_lost" : "http://purl.obolibrary.org/obo/SO_0001578","synonymous_variant" : "http://purl.obolibrary.org/obo/SO_0001819"}
soName2probability = {"3_prime_UTR_variant" : "1","5_prime_UTR_variant" : "1","coding_sequence_variant" : "1","frameshift_variant" : "1","intron_variant" : "1","missense_variant" : "1","nearest_gene_five_prime_end" : "1","non_coding_transcript_exon_variant" : "1","splice_acceptor_variant" : "1","splice_donor_variant" : "1","splice_region_variant" : "1","stop_gained" : "1","stop_lost" : "1","synonymous_variant" : "1"}

out = open(jsonFilePath, "a")

# parse results and serialize to JSON
with open(sparqlresults[0]) as results:
    for b in xrange(1):
        next(results)
    for line in csv.reader(results, delimiter="\t"):
        print "line = ", line;
        totalCount += 1
        if not line[0].startswith("#"):
            # grab snp, can we map?
            snpid = line[0]
            snpid = snpid[63:]
            pid = line[1]
            print ""
            print "pid=  ", pid
            print "snpid = ", snpid

        if not snpid in snpGeneMappings:
            noMappingCount += 1
            print "NO MAPPING snpid = " + snpid
        else:
            for gene2so in snpGeneMappings[snpid]:
                geneid, so = gene2so.split(":")
                print "Generating JSON for gene '" + geneid + " -> SNP '" + snpid + "'"
                soId = soName2soId[so]
                geneToVariantProbability=soName2probability[so]
                print "soId" + soId

                pmid = ""
                curated_date = ""
                efo_disease = ""
                for i in range(len(line)):
                    if i == 1:
                        pmid = line[i]
                    if i == 2:
                        curated_date = line[i]
                    if i == 3:
                        pval = line[i]
                    if i == 4:
                        efo_disease = line[i]
                        # if efo_disease.find("Orphanet") :
                        # elif efo_disease.find("HP") :
                        # elif efo_disease.find("DOID") :
                        # elif efo_disease.find("EFO") :
                        #     efo_disease = efo_disease[25:]
                        #     efo_disease = "http://www.ebi.ac.uk/efo/" + efo_disease
                        #
                        # if isOrphanet == -1:
                        #     efo_disease = efo_disease[25:]
                        #     efo_disease = "http://www.ebi.ac.uk/efo/" + efo_disease
                        print "efo_disease = ", efo_disease

                json["sourceID"] = "GWAS Catalog"
                json["validated_against_schema_version"] = "1.2"
                json["label"] = "genetics_evidence_string"
                json["access_level"]="public"

                json["unique_association_fields"] = {
                "variant": "http://identifiers.org/dbsnp/" + snpid,
                "object": efo_disease,
                "study_name": "cttv009_gwas_catalog",
                "pubmed_refs": "http://europepmc.org/abstract/MED/" + pmid,
                "pvalue": pval,
                "target": "http://identifiers.org/ensembl/" + geneid}

                target = {}
                target["id"] = ["http://identifiers.org/ensembl/" + geneid]
                target["target_type"] = "http://identifiers.org/cttv.target/gene"
                target["activity"] = "http://identifiers.org/cttv.activity/predicted_damaging"
                json["target"] = target

                variant = {}
                variant["id"] = ["http://identifiers.org/dbsnp/" + snpid]
                variant["type"] = "snp single";
                json["variant"] = variant

                disease = {}
                disease["id"] = [efo_disease]
                json["disease"] = disease

                evidence = {}

                evidence_probability = float(geneToVariantProbability) * float(pval)

                evidence_association_score = {}
                evidence_association_score = {}
                probability={}
                probability["value"]=evidence_probability
                method = {}
                method["description"]="This is just the product of the probability that the given snp is associated to the given gene (gene2variant association_score) by the P value of the variant to disease association (variant2disease association score)"
                probability["method"] = method
                evidence_association_score["probability"]=probability
                evidence["association_score"] = evidence_association_score




                # evidence_association_score = {}
                # evidence_rank = {}
                # evidence_rank["position"] = float(pval);
                # evidence_rank["sample_size"] = 1;
                # evidence_method = {}
                # evidence_method["url"] = ""
                # evidence_method["description"] = "Just the pvalue for the snp to disease association for the moment"
                # evidence_rank["method"] = evidence_method
                # evidence_association_score["rank"] = evidence_rank;
                # evidence["association_score"] = evidence_association_score;

                gene2variant = {}

                gene2variant_association_score = {}
                probability={}
                probability["value"]=float(geneToVariantProbability)
                method = {}
                method["description"]="This is a probability between 0 and 1 that the given variant is associated with the given gene. " \
                "It reflects the gene2variant.functional_consequence SequenceOntology term. Each of those SO terms " \
                "is arbritarily associated with a probability as follow : " \
                "3_prime_UTR_variant (http://purl.obolibrary.org/obo/SO_0001624) => 1," \
                "5_prime_UTR_variant : http://purl.obolibrary.org/obo/SO_0001623) => 1," \
                "coding_sequence_variant : http://purl.obolibrary.org/obo/SO_0001580) => 1," \
                "frameshift_variant : http://purl.obolibrary.org/obo/SO_0001589) => 1," \
                "intron_variant : http://purl.obolibrary.org/obo/SO_0001627) => 1," \
                "missense_variant : http://purl.obolibrary.org/obo/SO_0001583) => 1," \
                "nearest_gene_five_prime_end : http://targetvalidation.org/sequence/nearest_gene_five_prime_end) => 1," \
                "non_coding_transcript_exon_variant : http://purl.obolibrary.org/obo/SO_0001792) => 1," \
                "splice_acceptor_variant : http://purl.obolibrary.org/obo/SO_0001574) => 1," \
                "splice_donor_variant : http://purl.obolibrary.org/obo/SO_0001575) => 1," \
                "splice_region_variant : http://purl.obolibrary.org/obo/SO_0001630) => 1," \
                "stop_gained : http://purl.obolibrary.org/obo/SO_0001587) => 1," \
                "stop_lost : http://purl.obolibrary.org/obo/SO_0001578) => 1," \
                "synonymous_variant : http://purl.obolibrary.org/obo/SO_0001819) => 1."
                probability["method"] = method
                gene2variant_association_score["probability"]=probability
                gene2variant["association_score"] = gene2variant_association_score


            #     gene2variant_rank = {}
            #     gene2variant_rank["position"] = 1
            #     gene2variant_rank["sample_size"] = 6
            #     gene2variant_method = {}
            #     gene2variant_method["description"] = "our own method"
            #     gene2variant_rank["method"]=gene2variant_method
            #     gene2variant_association_score["rank"]=gene2variant_rank
            # # gene2variant_method = {}
            #     # gene2variant_method["description"] = "Probability of gene-SNP assignment by mapping software"
            #     # gene2variant_probability["method"] = gene2variant_method
            #     # gene2variant_association_score["probability"] = gene2variant_probability
            #     gene2variant["association_score"] = gene2variant_association_score


                #2015-2-5T13:38:44
                d = datetime.datetime.now()
                year = str(d.year)

                month = str(d.month)
                if len(month) == 1 :
                    month = "0"+month

                day = str(d.day)
                if len(day) == 1 :
                    day = "0"+day

                hour = str(d.hour)
                if len(hour) == 1 :
                    hour = "0"+hour

                minute = str(d.minute)
                if len(minute) == 1 :
                    minute = "0"+minute

                second = str(d.second)
                if len(second) == 1 :
                    second = "0"+second

                dateAsserted = year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + second + "+00:00"

                provenance_type = {}
                database = {}
                database["version"] = dateAsserted
                database["id"] = "GWAS Catalog"
                dbxref = {}
                dbxref["version"] = dateAsserted
                #TODO
                dbxref["id"] = "http://identifiers.org/gwascatalog"
                database["dbxref"] = dbxref
                provenance_type["database"] = database

                expert = {}
                expert["statement"] = "Primary submitter of data"
                expert["status"] = "true"
                provenance_type["expert"] = expert

                gene2variant["provenance_type"] = provenance_type

                gene2variant["is_associated"] = "true"

                #TODO
                gene2variant["date_asserted"] = dateAsserted
                gene2variant["evidence_codes"] = ["http://purl.obolibrary.org/obo/ECO_0000205","http://identifiers.org/eco/cttv_mapping_pipeline"]
                gene2variant["functional_consequence"] = soId;

                evidence["gene2variant"] = gene2variant

                variant2disease = {}
                variant2disease_association_score = {}
                variant2disease_pvalue = {}
                variant2disease_pvalue["value"] = float(pval)
                variant2disease_method = {}
                variant2disease_method["description"] = "The P value we get from the curated paper for the given variant to disease association. A P value is a value representing the significance of the odds ratio (odds of disease for individuals having a specific allele and the odds of disease for individuals who do not have that same allele) is typically calculated using a simple chi-squared test. Finding odds ratios that are significantly different from 1 is the objective of the GWA study because this shows that a SNP is associated with disease."
                variant2disease_method["url"] = "http://en.wikipedia.org/wiki/Genome-wide_association_study"
                variant2disease_pvalue["method"] = variant2disease_method
                variant2disease_association_score["pvalue"] = variant2disease_pvalue
                #todo
                # variant2disease_association_score["summed_total"] = pval
                variant2disease["association_score"] = variant2disease_association_score

                variant2disease["unique_experiment_reference"] = "http://europepmc.org/abstract/MED/" + pmid

                provenance_type = {}
                literature = {}
                literature["references"] = [{"id":"http://europepmc.org/abstract/MED/" + pmid}]
                provenance_type["literature"] = literature

                database = {}
                database["version"] = dateAsserted
                database["id"] = "GWAS Catalog"
                dbxref = {}
                #TODO
                dbxref["version"] = dateAsserted
                #TODO url?
                #TODO
                dbxref["id"] = "http://identifiers.org/gwascatalog"
                database["dbxref"] = dbxref
                provenance_type["database"] = database

                #TODO
                expert = {}
                expert["statement"] = "Primary submitter of data"
                expert["status"] = "True"
                provenance_type["expert"] = expert

                variant2disease["provenance_type"] = provenance_type

                variant2disease["is_associated"] = "True"
                #TODO
                variant2disease["date_asserted"] = dateAsserted
                variant2disease["evidence_codes"] = ["http://identifiers.org/eco/GWAS","http://purl.obolibrary.org/obo/ECO_0000205"]
                evidence["variant2disease"] = variant2disease

                json["evidence"] = evidence;

                jsonString = str(json)
                jsonString = jsonString.replace("'", "\"");
                jsonString = jsonString.replace("\"True\"", "true")
                jsonString = jsonString.replace("\"true\"", "true")
                jsonString = jsonString.replace("None", "null")
                out.write(jsonString)
                out.write("\n")


out.close()

#for oneJson in fullJson :
#    oneJsonString = str(oneJson)
##    if "rs975730" in oneJsonString:
#        print "rs97573  in json "
#    oneJsonString = oneJsonString.replace("'", "\"");
#    oneJsonString = oneJsonString.replace("True", "true")
#    oneJsonString = oneJsonString.replace("None", "null")
#    out.write(oneJsonString)
#out.write("\n")
#out.close()



print "No Mapping Count = "
print noMappingCount
print "Total Count = "
print totalCount

