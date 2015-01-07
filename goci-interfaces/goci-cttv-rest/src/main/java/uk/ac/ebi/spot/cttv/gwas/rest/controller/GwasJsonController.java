package uk.ac.ebi.spot.cttv.gwas.rest.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.spot.cttv.gwas.rest.model.GwasJson;
import uk.ac.ebi.spot.cttv.gwas.rest.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;

/**
 * Created by catherineleroy on 17/12/2014.
 *
 * cd /Users/catherineleroy/Documents/CTTV_0009_pipeline/gwas-cttv-rest
 *
 * mvn clean package
 *
 * cp /Users/catherineleroy/Documents/CTTV_0009_pipeline/gwas-cttv-rest/target/cttvgwasrest.war ~/Applications/tomcat-8.0.15/webapps/
 *
 * ~/Applications/tomcat-8.0.15/bin/catalina.sh start
 *
 * http://localhost:8080/cttvgwasrest/rest/getJson
 *
 * ~/Applications/tomcat-8.0.15/bin/catalina.sh stop
 */

@RestController
public class GwasJsonController {

    /**
     * This is the method that will be called to deal with the following http request :<br>
     * http://my_server:my_port/cttvgwasrest/rest/getJson<br>
     * eg. : http://localhost:8080/cttvgwasrest/rest/getJson<br>
     * <br>
     * This will return something like below on your browser window :
     * {"json":"[{{\"biological_subject\": {\"about\": [\"http://identifiers.org/ensembl/ENSG00000108094\"]},<br>
     * \"validated_against_schema_version\": 1.1, \"biological_object\": {\"about\": [\"http://identifiers.org/efo/EFO_0003767\"]},<br>
     * \"unique_association_fields\": {\"trait\": \"http://identifiers.org/efo/EFO_0003767\", \"snp\": \"http://identifiers.org/dbsnp:rs11010067\"},<br>
     * \"evidence\": {\"association_score\": {\"pvalue\": {\"method\": null, \"value\": null}, \"probability\": <br>
     * {\"method\": null, \"value\": null}}, \"evidence_chain\": [{\"biological_subject\": <br>
     * {\"about\": [\"http://identifiers.org/ensembl/ENSG00000108094\"]}, \"biological_object\":<br>
     * {\"about\": [\"http://identifiers.org/dbsnp:rs11010067\"]}, \"evidence\": {\"evidence_codes\": [\"http://identifiers.org/eco/ECO:0000177\",<br>
     * \"http://identifiers.org/eco/ECO:0000053\"], \"association_score\": {\"pvalue\": {\"method\": null, \"value\": null}, <br>
     * \"probability\": {\"method\": null, \"value\": null}}}}, {\"biological_subject\": {\"about\": [\"http://identifiers.org/dbsnp:rs11010067\"]}<br>
     * , \"biological_object\": {\"about\": [\"http://identifiers.org/efo/EFO_0003767\"]}, \"evidence\": {\"association_score\": <br>
     * {\"pvalue\": {\"method\": null, \"value\": \"2.000000039082963e-25\"}}, \"provenance_type\": {\"literature\": <br>
     * {\"pubmed_refs\": [\"http://identifiers.org/pubmed/23128233\"]}, \"expert\": {\"status\": true}}, \"evidence_codes\":<br>
     * [\"http://identifiers.org/eco/ECO:0001113\", \"http://identifiers.org/eco/ECO:0000205\", \"http://identifiers.org/eco/ECO:0000033\"],<br>
     * \"date_asserted\": \"2012-11-01 00:00:00\"}}], \"provenance_type\": {\"literature\": {\"pubmed_refs\": <br>
     * [\"http://identifiers.org/pubmed/23128233\"]}, \"expert\": {\"status\": true}}, \"is_associated\": true, \"evidence_codes\":<br>
     * [\"http://identifiers.org/eco/ECO:0001113\", \"http://identifiers.org/eco/ECO:0000205\", \"http://identifiers.org/eco/ECO:0000033\"], <br>
     * \"date_asserted\": \"2014-11-18 13:31:00.430492\"}}]",<br>
     * "creationDate":"2014-11-25T10:24:46Z"}<br>
     *<br>
     * @return a GwasJson object holding the content and the creation date of the gwas.json file pointed out by Constants.JSON_FILE_PATH
     * @throws IOException
     */
    @RequestMapping("/getJson")
    public GwasJson getJson() throws IOException {
        return new GwasJson(this.getJsonFromFile(), this.getCreationDate());
    }

    /**
     * Return the creation date of the gwas.json file. This can be used for example to see when the file was last
     * generated.
     *
     * @return the creation date of the gwas.json file pointed out by Constants.JSON_FILE_PATH
     *
     * @throws IOException
     */
    public String getCreationDate() throws IOException {
        Path p = Paths.get(Constants.JSON_FILE_PATH);
        BasicFileAttributes view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
        return "" + view.creationTime();
    }

    /**
     * Return as a string the content of the gwas.json file pointed out by Constants.JSON_FILE_PATH
     *
     * @return the content of the gwas.json
     *
     * @throws IOException
     */
    public String getJsonFromFile() throws IOException {
        String json = "";
        File jsonFile = new File(Constants.JSON_FILE_PATH);
        LineIterator it = FileUtils.lineIterator(jsonFile, "UTF-8");
        try {
            while (it.hasNext()) {
                json  = json + it.nextLine();
                // do something with line
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
        return json;

    }


}
