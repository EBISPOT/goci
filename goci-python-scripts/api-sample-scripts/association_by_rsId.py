import argparse
import json
import requests

import properties


def retrieve_data(pubmedIds):

    base_url = properties.base_url
    snps = properties.snps
    search = properties.rsidSearch

    print("rs ID, risk allele, risk allele frequency, functional class")
    print(" gene names")
    print(" chromosome number, bp location, chromosome region")

    print(" risk allele, risk allele frequency, functional class")
    print("  pvalue, pvalue description, risk frequency, odds ratio, beta coefficient, beta unit, beta direction, range, SE, SNP type")
    print("   Author, publication date, title, journal, pubmed Id, accession Id, initial sample size, replication sample size")


    for id in pubmedIds:
        url = base_url+snps+search+id



        # It is a good practice not to hardcode the credentials. So ask the user to enter credentials at runtime
        snpResponse = requests.get(url)
        #print (snpResponse.status_code)

        # For successful API call, response code will be 200 (OK)
        if(snpResponse.ok):

            snpData = json.loads(snpResponse.content)
            print(snpData["rsId"])
            getContext(snpData)
            
            ras = snpData["_links"]["riskAlleles"]["href"]

            rasResp = requests.get(ras)

            if(rasResp.ok):
                rData = json.loads(rasResp.content)["_embedded"]["riskAlleles"]

                for r in range(len(rData)):

                    # As there is only one SNP object associated with a risk allele, the result of this call is not _embedded
                    print(" "+ rData[r]["riskAlleleName"] + ", "+ str(rData[r]["riskFrequency"])  + ", "+ snpData["functionalClass"])

                    lociURL = rData[r]["_links"]["loci"]["href"]
                    getAssociations(lociURL)

    else:
        # If response code is not ok (200), print the resulting http error code with description
        snpResponse.raise_for_status()



def getContext(snpData):
        geneLink = snpData["_links"]["genes"]["href"]
        locationsLink = snpData["_links"]["locations"]["href"]

        geneResp = requests.get(geneLink)
        locResp = requests.get(locationsLink)

        if(geneResp.ok):
            geneData = json.loads(geneResp.content)["_embedded"]["genes"]

            geneNames = str()
            for g in range(len(geneData)):
                geneNames = geneNames+geneData[g]["geneName"]+", "

            print(" " + geneNames)

        if(locResp.ok):
            locData = json.loads(locResp.content)["_embedded"]["locations"]

            for l in range(len(locData)):
                chromLink = locData[l]["_links"]["region"]["href"]

                chromResp = requests.get(chromLink)

                if(chromResp.ok):
                    region = json.loads(chromResp.content)

                    print(" " + str(locData[l]["chromosomeName"]) + ", " + str(locData[l]["chromosomePosition"]) + ", " + str(region["name"]))


def getAssociations(url):
    lociResp = requests.get(url)

    if(lociResp.ok):
        lData = json.loads(lociResp.content)["_embedded"]["loci"]

        for l in range(len(lData)):
            assocs = lData[l]["_links"]["association"]["href"]
            assocResp = requests.get(assocs)

            if(assocResp.ok):
                a = json.loads(assocResp.content)

                print("   " + str(a["pvalue"]) + ", "+  str(a["pvalueDescription"]) + ", "+ a["riskFrequency"]  + ", "+ str(a["orPerCopyNum"])  + ", "+ str(a["betaNum"])
                      + ", "+ str(a["betaUnit"])  + ", "+ str(a["betaDirection"])  + ", "+ str(a["range"])  + ", "+ str(a["standardError"])  + ", "+ a["snpType"])


                studyUrl = a["_links"]["study"]["href"]

                studyResp = requests.get(studyUrl)

                if(studyResp.ok):
                    s = json.loads(studyResp.content)

                    print("    " + s["author"] + "; "+  s["publicationDate"] + "; "+ s["title"] + "; "+ s["publication"]
                          + "; "+  s["pubmedId"] + "; "+ s["accessionId"] + "; "+  str(s["initialSampleSize"])+ "; "+ str(s["replicateSampleSize"]))





if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('rsIds', nargs='+')
    args = parser.parse_args()

    retrieve_data(args.rsIds)