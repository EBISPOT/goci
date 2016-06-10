package uk.ac.ebi.spot.goci.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.utils.TranslateAuthorUploadHeaders;
import uk.ac.ebi.spot.goci.utils.TranslateCuratorUploadHeaders;
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
        TranslateUploadHeaders translateUploadHeaders;

        switch (validationLevel) {
            case "full":
                translateUploadHeaders = new TranslateCuratorUploadHeaders();
                uploadSheetProcessor = new CuratorSheetProcessorImpl(translateUploadHeaders);
                break;
            case "author":
                translateUploadHeaders = new TranslateAuthorUploadHeaders();
                uploadSheetProcessor = new AuthorSheetProcessorImpl(translateUploadHeaders);
                break;
            default:
                translateUploadHeaders = new TranslateCuratorUploadHeaders();
                uploadSheetProcessor = new CuratorSheetProcessorImpl(translateUploadHeaders);
                break;
        }
        return uploadSheetProcessor;
    }
}
