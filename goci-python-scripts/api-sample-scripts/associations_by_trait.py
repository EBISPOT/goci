import requests, argparse
import json

import properties



def retrieve_data(uris):

    base_url = properties.base_url
    efoTraits = properties.efoTraits
    search = properties.uriSearch

    # Header
    print("EFO trait, URI")
    print(" pvalue, pvalue description, risk frequency, odds ratio, beta coefficient, beta unit, beta direction, range, SE, SNP type")
    print("   haplotypeSnoCunt, description")
    print("    risk allele, risk allele frequency, description")
    print("   author-reported genes")
    print("  rsId, functional class, merged, mergedInto")
    print("   chromosome number, bp location, chromosome region")
    print("   gene name, isIntergenic, isUpstream, isDownstream, distanceToSnp")
    print("   author, publication date, title, journal, pubmed Id, accession Id, initial sample size, replication sample size, disease trait")

    for id in uris:
        url = base_url+efoTraits+search+id

        # It is a good practice not to hardcode the credentials. So ask the user to enter credentials at runtime
        traitResponse = requests.get(url)

        # For successful API call, response code will be 200 (OK)
        if(traitResponse.ok):

            # Loading the response data into a dict variable
            # json.loads takes in only binary or string variables so using content to fetch binary content
            # Loads (Load String) takes a Json file and converts into python data structure (dict or list, depending on JSON)
            data = (json.loads(traitResponse.content))["_embedded"]["efoTraits"]

            for x in range(len(data)):

                e = data[x]

                print(e["trait"] + " " + e["uri"])

                associationsURL = e["_links"]["associationsByTraitSummary"]["href"]

                assocResponse = requests.get(associationsURL)

                if(assocResponse.ok):
                    aData = json.loads(assocResponse.content)["_embedded"]["associations"]
                    for y in range(len(aData)):
                        a = aData[y]

                        print(" " + str(a["pvalue"]) + ", "+  str(a["pvalueDescription"]) + ", "+ a["riskFrequency"]  + ", "+ str(a["orPerCopyNum"])  + ", "+ str(a["betaNum"])
                              + ", "+ str(a["betaUnit"]).replace(u'\xa0', u' ') + ", "+ str(a["betaDirection"]).replace(u'\xa0', u' ')
                             + ", "+ str(a["range"].replace(u'\xa0', u' '))
                              + ", "+ str(a["standardError"]).replace(u'\xa0', u' ')  + ", "+ a["snpType"])

                        # Get all loci for this association & downstream data
                        loci = a["loci"]
                        snps = a["snps"]
                        s = a["study"]


                        for l in range(len(loci)):
                            print("   " + str(loci[l]["haplotypeSnpCount"]) + ", " + loci[l]["description"])

                            for r in range(len(loci[l]["strongestRiskAlleles"])):
                                print("    " + loci[l]["strongestRiskAlleles"][r]["riskAlleleName"] + ", "+ str(loci[l]["strongestRiskAlleles"][r]["riskFrequency"]))

                            for a in range(len(loci[l]["authorReportedGenes"])):
                                print("    " + loci[l]["authorReportedGenes"][a]["geneName"])


                        for p in range(len(snps)):
                            print("  "+ str(snps[p]["rsId"])  + ", "+ str(snps[p]["functionalClass"]) + ", " + str(snps[p]["merged"]) + ", " + str(snps[p]["mergedInto"]))

                            loc = snps[p]["locations"]
                            gc = snps[p]["genomicContexts"]

                            for f in range(len(loc)):
                                print("    " + str(loc[f]["chromosomeName"]) + ", " + str(loc[f]["chromosomePosition"]) + ", " + str(loc[f]["region"]["name"]))

                            for g in range(len(gc)):
                                print("    " + str(gc[g]["gene"]["geneName"]) + ", " + str(gc[g]["isIntergenic"]) + ", " + str(gc[g]["isUpstream"]) + ", " + str(gc[g]["isDownstream"]) + ", " + str(gc[g]["distance"]))

                        print("    " + str(s["author"]) + "; "+  str(s["publicationDate"]) + "; "+ str(s["title"]) + "; "+ str(s["publication"]) + "; "+
                              str(s["pubmedId"]) + "; "+ str(s["accessionId"]) + "; "+  str(s["initialSampleSize"])+ "; "+ str(s["replicationSampleSize"]) + "; "+ str(s["diseaseTrait"]["trait"]))





            print("")
            print("")
    else:
        # If response code is not ok (200), print the resulting http error code with description
        traitResponse.raise_for_status()





if __name__ == "__main__":
    # Parse the pubmed ID inputs
    parser = argparse.ArgumentParser()
    parser.add_argument('uris', nargs='+')
    args = parser.parse_args()

    retrieve_data(args.uris)