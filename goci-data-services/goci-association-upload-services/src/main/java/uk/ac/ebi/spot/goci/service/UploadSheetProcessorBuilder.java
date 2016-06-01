package uk.ac.ebi.spot.goci.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.utils.TranslateUploadHeaders;

/**
 * Created by emma on 01/06/2016.
 *
 * @author emma
 *         <p>
 *         Builder that returns the required processor service based on the level set by client input
 */
@Service
public class UploadSheetProcessorBuilder {

    /**
     * Return required spreadsheet processor
     *
     * @param validationLevel Level of required checks. At present we only have one level but there is potential for
     *                        different flavours of checking
     */
    public UploadSheetProcessor buildProcessor(String validationLevel) {

        UploadSheetProcessor uploadSheetProcessor;

        // TODO COULD POTENTIALLY HAVE DIFFERENT TRANSALTE HEADER METHODS SET HERE
        TranslateUploadHeaders translateUploadHeaders = new TranslateUploadHeaders();

        switch (validationLevel) {
            case "full":
                uploadSheetProcessor = new SheetProcessorImpl(translateUploadHeaders);
                break;
            case "author":
                uploadSheetProcessor = new SheetProcessorImpl(translateUploadHeaders);
                break;
            default:
                uploadSheetProcessor = new SheetProcessorImpl(translateUploadHeaders);
                break;
        }
        return uploadSheetProcessor;
    }
}
