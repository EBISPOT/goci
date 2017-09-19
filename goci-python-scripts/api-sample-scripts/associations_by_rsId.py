import requests, argparse
import json

import properties



def retrieve_data(rsIds):

    base_url = properties.base_url
    snps = properties.snps
    search = properties.rsidSearch

    print("rs ID, functional class, merged, mergedInto")
    print(" chromosome number, bp location, chromosome region")
    print(" gene name, isIntergenic, isUpstream, isDownstream, distanceToSnp")

    print(" pvalue, pvalue description, risk frequency, odds ratio, beta coefficient, beta unit, beta direction, range, SE, SNP type")
    print("   haplotypeSnoCunt, description")
    print("    risk allele, risk allele frequency, description")
    print("   author-reported genes")
    print("   author, publication date, title, journal, pubmed Id, accession Id, initial sample size, replication sample size, disease trait")
    print("   Efo trait, Efo URI")

    for id in rsIds:
        url = base_url+snps+search+id



        # It is a good practice not to hardcode the credentials. So ask the user to enter credentials at runtime
        snpResponse = requests.get(url)
        #print (snpResponse.status_code)

        # For successful API call, response code will be 200 (OK)
        if(snpResponse.ok):

            snpData = json.loads(snpResponse.content)
            print(snpData["rsId"] + ", " +snpData["functionalClass"] + ", " + str(snpData["merged"]) + ", " + str(snpData["mergedInto"]))

            loc = snpData["locations"]
            gc = snpData["genomicContexts"]

            for f in range(len(loc)):
                print("    " + str(loc[f]["chromosomeName"]) + ", " + str(loc[f]["chromosomePosition"]) + ", " + str(loc[f]["region"]["name"]))

            for g in range(len(gc)):
                print("    " + str(gc[g]["gene"]["geneName"]) + ", " + str(gc[g]["isIntergenic"]) + ", " + str(gc[g]["isUpstream"]) + ", " + str(gc[g]["isDownstream"]) + ", " + str(gc[g]["distance"]))


            assocs = snpData["_links"]["associationsBySnpSummary"]["href"]

            assocResp = requests.get(assocs)

            if(assocResp.ok):
                aData = json.loads(assocResp.content)["_embedded"]["associations"]

                for y in range(len(aData)):
                    a = aData[y]

                    print(" " + str(a["pvalue"]) + ", "+  str(a["pvalueDescription"]) + ", "+ a["riskFrequency"]  + ", "+ str(a["orPerCopyNum"])  + ", "+ str(a["betaNum"])
                          + ", "+ str(a["betaUnit"])  + ", "+ str(a["betaDirection"])  + ", "+ str(a["range"])  + ", "+ str(a["standardError"])  + ", "+ str(a["snpType"]))

                    # Get all loci for this association & downstream data
                    loci = a["loci"]
                    s = a["study"]
                    efo = a["efoTraits"]

                    for l in range(len(loci)):
                        print("   " + str(loci[l]["haplotypeSnpCount"]) + ", " + str(loci[l]["description"]))

                        for ra in range(len(loci[l]["strongestRiskAlleles"])):
                            print("    " + loci[l]["strongestRiskAlleles"][ra]["riskAlleleName"] + ", "+ str(loci[l]["strongestRiskAlleles"][ra]["riskFrequency"]))

                        for a in range(len(loci[l]["authorReportedGenes"])):
                            print("    " + loci[l]["authorReportedGenes"][a]["geneName"])

                    print("    " + str(s["author"]) + "; "+  str(s["publicationDate"]) + "; "+ str(s["title"]) + "; "+ str(s["publication"]) + "; "+
                          str(s["pubmedId"]) + "; "+ str(s["accessionId"]) + "; "+  str(s["initialSampleSize"])+ "; "+ str(s["replicationSampleSize"]) + "; "+ str(s["diseaseTrait"]["trait"]))

                    for e in range(len(efo)):
                        print("    " + efo[e]["trait"] + ", " + efo[e]["uri"])


        else:
        # If response code is not ok (200), print the resulting http error code with description
            snpResponse.raise_for_status()




if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('rsIds', nargs='+')
    args = parser.parse_args()

    retrieve_data(args.rsIds)