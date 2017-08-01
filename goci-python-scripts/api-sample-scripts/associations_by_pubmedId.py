import argparse
import json
import requests

import properties


def retrieve_data(pubmedIds):

    base_url = properties.base_url
    studies = properties.studies
    search = properties.pmidSearch

    # Header
    print("Author, publication date, title, journal, pubmed Id, accession Id, initial sample size, replication sample size")
    print(" pvalue, pvalue description, risk frequency, odds ratio, beta coefficient, beta unit, beta direction, range, SE, SNP type")
    print("  rs ID, risk allele, risk allele frequency, functional class")
    print("   gene names")
    print("    chromosome number, bp location, chromosome region")

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
                       + "; "+  s["pubmedId"] + "; "+ s["accessionId"] + "; "+  s["initialSampleSize"]+ "; "+ s["replicateSampleSize"])


                # Get all associations for this study
                assocs = s["_links"]["associations"]["href"]

                assocResp = requests.get(assocs)

                if(assocResp.ok):
                    aData = json.loads(assocResp.content)["_embedded"]["associations"]
                    for y in range(len(aData)):
                        a = aData[y]

                        print(" " + str(a["pvalue"]) + ", "+  str(a["pvalueDescription"]) + ", "+ a["riskFrequency"]  + ", "+ str(a["orPerCopyNum"])  + ", "+ str(a["betaNum"])
                               + ", "+ str(a["betaUnit"])  + ", "+ str(a["betaDirection"])  + ", "+ str(a["range"])  + ", "+ str(a["standardError"])  + ", "+ a["snpType"])

                        # Get all loci for this association & downstream data
                        loci = a["_links"]["loci"]["href"]
                        getLoci(loci)
    else:
            # If response code is not ok (200), print the resulting http error code with description
            studyResponse.raise_for_status()



def getLoci(url):
    lociResp = requests.get(url)

    if(lociResp.ok):
        lData = json.loads(lociResp.content)["_embedded"]["loci"]

        for l in range(len(lData)):
            ras = lData[l]["_links"]["strongestRiskAlleles"]["href"]

            rasResp = requests.get(ras)

            if(rasResp.ok):
                rData = json.loads(rasResp.content)["_embedded"]["riskAlleles"]

                for r in range(len(rData)):

                    snpResp = requests.get(rData[r]["_links"]["snp"]["href"])

                    if(snpResp.ok):
                        # As there is only one SNP object associated with a risk allele, the result of this call is not _embedded
                        snpData = json.loads(snpResp.content)
                        print("  "+ snpData["rsId"]  + ", "+ rData[r]["riskAlleleName"] + ", "+ str(rData[r]["riskFrequency"])  + ", "+ snpData["functionalClass"])


                        geneLink = snpData["_links"]["genes"]["href"]
                        locationsLink = snpData["_links"]["locations"]["href"]

                        geneResp = requests.get(geneLink)
                        locResp = requests.get(locationsLink)

                        if(geneResp.ok):
                            geneData = json.loads(geneResp.content)["_embedded"]["genes"]

                            geneNames = str()
                            for g in range(len(geneData)):
                                geneNames = geneNames+geneData[g]["geneName"]+", "

                            print("  " + geneNames)

                        if(lociResp.ok):
                            locData = json.loads(locResp.content)["_embedded"]["locations"]

                            for l in range(len(locData)):
                                chromLink = locData[l]["_links"]["region"]["href"]

                                chromResp = requests.get(chromLink)

                                if(chromResp.ok):
                                    region = json.loads(chromResp.content)

                                    print("    " + str(locData[l]["chromosomeName"]) + ", " + str(locData[l]["chromosomePosition"]) + ", " + region["name"])




if __name__ == "__main__":
    # Parse the pubmed ID inputs
    parser = argparse.ArgumentParser()
    parser.add_argument('pubmedIds', nargs='+')
    args = parser.parse_args()

    retrieve_data(args.pubmedIds)