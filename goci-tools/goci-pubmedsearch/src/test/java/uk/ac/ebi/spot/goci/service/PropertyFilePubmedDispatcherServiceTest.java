package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 02/05/13
 * Time: 10:40
 * To change this template use File | Settings | File Templates.
 */
public class PropertyFilePubmedDispatcherServiceTest {

    @Before
    public void setUp(){

    }

    @Test
    public void testDispatchSearchQuery(){

//        try {
//            // int some fields for looping over results
//            int start = 0, count = -1;
//            Collection<String> results = new HashSet<String>();
//
//            String limitParams = "&retmax=" + PUBMED_MAXRECORDS + "&restart=" + start;
//            String dateParams = "&reldate=".concat(dateProp) + "&datetype=pdat";
//            getLog().debug("PubMed query is '" + searchString + dateParams + limitParams + "'");
//            Document response = doPubmedQuery(URI.create(searchString + dateParams + limitParams));
//
//            // parse resulting xml to discover count - we might need to loop in increments
//            NodeList countNodes = response.getElementsByTagName("Count");
//            // get total number of records
//            count = Integer.parseInt(countNodes.item(0).getTextContent());
//            getLog().debug("Total number of records in this query: " + count);
//
//            if (count == -1) {
//                throw new DispatcherException("Could not get number of records from PubMed");
//            }
//
//            int it = 1;
//            while (start < count) {
//                // redo query with limits until we reach the end
//                limitParams = "&retmax=" + PUBMED_MAXRECORDS + "&retstart=" + start;
//                getLog().debug("Iteration " + it + ": " + searchString + dateParams + limitParams);
//                response = doPubmedQuery(URI.create(searchString + dateParams + limitParams));
//
//                NodeList idNodes = response.getElementsByTagName("Id");
//                for (int i = 0; i < idNodes.getLength(); i++) {
//                    Node idNode = idNodes.item(i);
//                    results.add(idNode.getTextContent());
//                }
//
//                // increment start point
//                start += PUBMED_MAXRECORDS;
//                it++;
//            }
//
//            getLog().debug("Acquired " + results.size() + " PubMed IDs OK!");
//            return results;
//        }
//        catch (IOException e) {
//            throw new DispatcherException("Communication problem with PubMed", e);
//        }

    }
}
