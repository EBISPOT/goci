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
#         tab delimited file containing line like : snp_dbSnp_rsId[\t]ensembl_gene_label[\t]ensembl_gene_id
#         eg. : rs10255878	AP1S2P1	ENSG00000226046
#               rs10256972	C7orf50	ENSG00000146540
#               rs10259085	C1GALT1	ENSG00000106392

jsonFilePath = sys.argv[1]
snp2geneMappingFilePath = sys.argv[2]
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
			if not line[0] in snpGeneMappings:
				snpGeneMappings[line[0]] = list()
			gene2soLabel = line[1] + ":" + line[2];
			snpGeneMappings[line[0]].append(gene2soLabel)




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

fullJson = list()

json = {}

noMappingCount = 0
totalCount = 0

soName2soId = {"intron_variant": "http://identifiers.org/so/SO:0001627", "missense_variant": "http://identifiers.org/so/SO:0001583", "synonymous_variant" : "http://identifiers.org/so/SO:0001819", "upstream_gene_variant" : "http://identifiers.org/so/SO:0001631"}

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
					geneid,so = gene2so.split(":")
					print "Generating JSON for gene '" + geneid + " -> SNP '" + snpid + "'"
					gene = {}
					snp = {}
					trait = {}

					soId = soName2soId[so]
					print "soId" + soId
					provenance = {}

					gene["about"] = ["http://identifiers.org/ensembl/" + geneid]
					geneprov={}
					geneprov["evidence_codes"] = ["http://identifiers.org/eco/ECO:0000177", "http://identifiers.org/eco/ECO:0000053", soId, "http://identifiers.org/eco/cttv_mapping_pipeline"]
					geneprov["association_score"] ={"probability" : {"value" : None, "method" : None}, "pvalue": {"value" : None, "method" : None}}


					snp["about"] = ["http://identifiers.org/dbsnp/" + snpid]
					snpprov = {}
					snpprov["evidence_codes"] = ["http://purl.obolibrary.org/obo/eco/GWAS","http://identifiers.org/eco/ECO:0000033", "http://identifiers.org/eco/ECO:0000205"]
					pval=0
					provenance_type = {}


					for i in range(len(line)):
						if i == 1:
							print "provenance", line[i]
							provenance_type = {"literature":{"pubmed_refs": ["http://identifiers.org/pubmed/" + line[i]]}, "expert": { "status": True} }
							snpprov["provenance_type"] = provenance_type
						if i == 2:
							snpprov["date_asserted"] = line[i]
							print "date = ", line[i]
						if i == 3:
							pval = line[i]
							snpprov["association_score"] = {"pvalue": {"value" : line[i], "method" : None}}
						if i == 4:
							efo = line[i]
							isOrphanet = efo.find("Orphanet")
							if isOrphanet == -1:
								efo = efo[25:]
								efo = "http://identifiers.org/efo/" + efo

							trait["about"] = [efo]
							print "efo = ", efo


					json = {"validated_against_schema_version": "1.1"};

					json["unique_association_fields"] = {
					"snp":"http://identifiers.org/dbsnp/" + snpid,
					"object": efo,
					"study_name":"cttv009_gwas_catalog",
					"pubmed_refs":"http://identifiers.org/pubmed/" + pid,
					"pvalue":pval,
					"biological_subject":"http://identifiers.org/ensembl/" + geneid}

					json["biological_subject"] = gene

					evidencechain = list()
					evidencechain.append({"biological_subject" : gene, "biological_object" : snp, "evidence" : geneprov})
					evidencechain.append({"biological_subject" : snp, "biological_object" : trait, "evidence" : snpprov})


					json["biological_object"] = trait

					#2015-2-5T13:38:44
					d = datetime.datetime.now()
					year = str(d.year)
					month = str(d.month)
					day = str(d.day)
					hour = str(d.hour)
					minute = str(d.minute)
					second = str(d.second)
					dateAsserted = year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + second;

					json["evidence"] = {"date_asserted" : dateAsserted , "is_associated" : True, "association_score" : {"probability" : {"value" : None, "method" : None}, "pvalue": {"value" : None, "method" : None}}, "provenance_type" : provenance_type, "evidence_chain" : evidencechain,  "evidence_codes": ["http://purl.obolibrary.org/obo/eco/GWAS", soId,"http://identifiers.org/eco/ECO:0000177","http://identifiers.org/eco/ECO:0000205", "http://identifiers.org/eco/ECO:0000033", "http://identifiers.org/eco/cttv_mapping_pipeline"]}
					print "appending to full json" + snpid
					fullJson.append(json);
					if snpid == "rs975730" :
						print "json = " + str(json)
						print "\n\n\n fullJson" + str(fullJson)
						print "terminate"
					if snpid == "rs13266634" :
						print "json = " + str(json)
						#print "\n\n\n fullJson" + str(fullJson)
						#print "terminate"
					#fullJson.append(json);



print "print full json"
out = open(jsonFilePath, "a")
oneJsonString = str(fullJson)
oneJsonString = oneJsonString.replace("'", "\"");
oneJsonString = oneJsonString.replace("True", "true")
oneJsonString = oneJsonString.replace("None", "null")
out.write(oneJsonString)
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

