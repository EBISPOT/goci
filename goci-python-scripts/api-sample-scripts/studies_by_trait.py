import argparse
import json
import requests

import properties


def retrieve_data(pubmedIds):

    base_url = properties.base_url
    efoTraits = properties.efoTraits
    search = properties.uriSearch

    # Header
    print("EFO trait, URI")
    print(" Author, publication date, title, journal, pubmed Id, accession Id, initial sample size, replication sample size")
  
    for id in pubmedIds:
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
                
                studiesURL = e["_links"]["studies"]["href"]

                studyResponse = requests.get(studiesURL)

                if(studyResponse.ok):
                    studyData = json.loads(studyResponse.content)["_embedded"]["studies"]

                    for x in range(len(studyData)):

                        s = studyData[x]

                        # Print study data summary
                        print(" " + s["author"] + "; "+  s["publicationDate"] + "; "+ s["title"] + "; "+ s["publication"]
                          + "; "+  s["pubmedId"] + "; "+ s["accessionId"] + "; "+  str(s["initialSampleSize"])+ "; "+ str(s["replicateSampleSize"]))
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