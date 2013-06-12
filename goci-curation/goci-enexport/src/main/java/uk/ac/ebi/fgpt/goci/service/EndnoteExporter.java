package uk.ac.ebi.fgpt.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 07/06/13
 * Time: 15:29
 * To change this template use File | Settings | File Templates.
 */
public class EndnoteExporter {
    private PMIDAcquisitionService pmidacquisition;
    private PubMedQueryService exporterService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void setPmidacquisition(PMIDAcquisitionService pmidacquisition) {
        this.pmidacquisition = pmidacquisition;
    }

    public PMIDAcquisitionService getPmidacquisition() {
        return pmidacquisition;
    }

    public void setExporterService(PubMedQueryService exporterService) {
        this.exporterService = exporterService;
    }

    public PubMedQueryService getExporterService() {
        return exporterService;
    }

    public String dispatchQuery(String table) throws DispatcherException{
        getLog().debug("Dispatching PubMed queries...");
        ArrayList<String> pubmedIDs = (ArrayList<String>) getPmidacquisition().getPMIDs(table);
        getLog().debug("Query returned " + pubmedIDs.size() + " studies...");

        String exportData = getExporterService().dispatchFetchQuery(pubmedIDs);

        return exportData;
    }
}

