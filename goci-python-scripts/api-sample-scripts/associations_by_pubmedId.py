import requests, argparse
import json

import properties


def retrieve_data(pubmedIds):

    base_url = properties.base_url
    studies = properties.studies
    search = properties.pmidSearch

    # Header
    print("Author, publication date, title, journal, pubmed Id, accession Id, initial sample size, replication sample size, disease trait")
    print("Efo trait, Efo URI")
    print(" pvalue, pvalue description, risk frequency, odds ratio, beta coefficient, beta unit, beta direction, range, SE, SNP type")
    print("   haplotypeSnoCunt, description")
    print("    risk allele, risk allele frequency, description")
    print("   author-reported genes")
    print("  rsId, functional class, merged, mergedInto")
    print("   chromosome number, bp location, chromosome region")
    print("   gene name, isIntergenic, isUpstream, isDownstream, distanceToSnp")

    for id in pubmedIds:
        url = base_url+studies+search+id

        # It is a good practice not to hardcode the credentials. So ask the user to enter credentials at runtime
        studyResponse = requests.get(url)

        # For successful API call, response code will be 200 (OK)
        if(studyResponse.ok):

            # Loading the response data into a dict variable
            # json.loads takes in only binary or string variables so using content to fetch binary content
            # Loads (Load String) takes a Json file and converts into python data structure (dict or list, depending on JSON)
            data = (json.loads(studyResponse.content))["_embedded"]["studies"]

            for x in range(len(data)):

                s = data[x]
                # Print study data summary
                print(s["author"] + "; "+  s["publicationDate"] + "; "+ s["title"] + "; "+ s["publication"]
                       + "; "+  s["pubmedId"] + "; "+ s["accessionId"] + "; "+  s["initialSampleSize"]+ "; "+ str(s["replicationSampleSize"]) + "; "+ s["diseaseTrait"]["trait"])

                efo = s["_links"]["efoTraits"]["href"]
                traitResp = requests.get(efo)

                if(traitResp.ok):
                    efoData = json.loads(traitResp.content)["_embedded"]["efoTraits"]

                    for t in range(len(efoData)):
                        print(efoData[t]["trait"] + ", " + efoData[t]["uri"])


                # Get all associations for this study
                assocs = s["_links"]["associationsByStudySummary"]["href"]

                assocResp = requests.get(assocs)

                if(assocResp.ok):
                    aData = json.loads(assocResp.content)["_embedded"]["associations"]
                    for y in range(len(aData)):
                        a = aData[y]

                        print(" " + str(a["pvalue"]) + ", "+  str(a["pvalueDescription"]) + ", "+ a["riskFrequency"]  + ", "+ str(a["orPerCopyNum"])  + ", "+ str(a["betaNum"])
                               + ", "+ str(a["betaUnit"])  + ", "+ str(a["betaDirection"])  + ", "+ str(a["range"])  + ", "+ str(a["standardError"])  + ", "+ a["snpType"])

                        # Get all loci for this association & downstream data
                        loci = a["loci"]
                        snps = a["snps"]


                        for l in range(len(loci)):
                              print("   " + str(loci[l]["haplotypeSnpCount"]) + ", " + loci[l]["description"])

                              for s in range(len(loci[l]["strongestRiskAlleles"])):
                                print("    " + loci[l]["strongestRiskAlleles"][s]["riskAlleleName"] + ", "+ str(loci[l]["strongestRiskAlleles"][s]["riskFrequency"]))

                              for a in range(len(loci[l]["authorReportedGenes"])):
                                 print("    " + loci[l]["authorReportedGenes"][a]["geneName"])


                        for p in range(len(snps)):
                            print("  "+ snps[p]["rsId"]  + ", "+ snps[p]["functionalClass"] + ", " + str(snps[p]["merged"]) + ", " + str(snps[p]["mergedInto"]))

                            loc = snps[p]["locations"]
                            gc = snps[p]["genomicContexts"]

                            for f in range(len(loc)):
                                print("    " + str(loc[f]["chromosomeName"]) + ", " + str(loc[f]["chromosomePosition"]) + ", " + str(loc[f]["region"]["name"]))

                            for g in range(len(gc)):
                                print("    " + str(gc[g]["gene"]["geneName"]) + ", " + str(gc[g]["isIntergenic"]) + ", " + str(gc[g]["isUpstream"]) + ", " + str(gc[g]["isDownstream"]) + ", " + str(gc[g]["distance"]))







        else:
            # If response code is not ok (200), print the resulting http error code with description
            studyResponse.raise_for_status()




if __name__ == "__main__":
    # Parse the pubmed ID inputs
    parser = argparse.ArgumentParser()
    parser.add_argument('pubmedIds', nargs='+')
    args = parser.parse_args()

    retrieve_data(args.pubmedIds)