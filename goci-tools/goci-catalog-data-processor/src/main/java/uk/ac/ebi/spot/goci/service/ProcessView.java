package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.CatalogSummaryView;

import java.util.Collection;

/**
 * Created by emma on 17/02/15.
 */
@Service
public class ProcessView {

    private NCBICatalogService ncbiCatalogService;

    @Autowired
    public ProcessView(NCBICatalogService ncbiCatalogService) {
        this.ncbiCatalogService = ncbiCatalogService;
    }

    public void createFileForNcbi() {

        Collection<CatalogSummaryView> views = ncbiCatalogService.getCatalogSummaryViewsSendToNcbi();

    }


}
