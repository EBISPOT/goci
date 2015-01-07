package uk.ac.ebi.spot.cttv.gwas.rest.model;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import uk.ac.ebi.spot.cttv.gwas.rest.utils.Constants;

import java.io.File;
import java.io.IOException;

/**
 * Created by catherineleroy on 17/12/2014.
 */
public class GwasJson {

    private final String json;
    private final String creationDate;

    public GwasJson(String json, String creationDate){
        this.json = json;
        this.creationDate = creationDate;
    }

    public String getJson(){
        return this.json;
    }

    public String getCreationDate(){
        return this.creationDate;
    }
}
