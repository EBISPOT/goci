package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import uk.ac.ebi.spot.goci.model.Association;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         This is a small program to upload a batch of SNPs from a .xlsx spreadsheet. Note that the spreadsheet must be
 *         of .xlsx format!
 *         <p>
 *         Created from code originally written by Dani. Adapted to fit with new curation system.
 */
public interface AssociationBatchLoaderService {

    Collection<Association> processData(String fileName) throws InvalidFormatException, IOException;
}
