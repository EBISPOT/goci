package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.CatalogSummaryView;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.repository.CatalogSummaryViewRepository;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;

import java.util.Collection;

/**
 * Created by emma on 17/02/15.
 *
 * @author emma
 *         <p>
 *         Returns collection of views of various studies that have status "Send to NCBI"
 */
@Service
public class NCBICatalogService {

    private CatalogSummaryViewRepository catalogSummaryViewRepository;
    private CurationStatusRepository curationStatusRepository;

    @Autowired
    public NCBICatalogService(CatalogSummaryViewRepository catalogSummaryViewRepository,
                              CurationStatusRepository curationStatusRepository) {
        this.catalogSummaryViewRepository = catalogSummaryViewRepository;
        this.curationStatusRepository = curationStatusRepository;
    }


    // Query CATALOG_SUMMARY_VIEW table in database
    // and locate any views with status "Send to NCBI"
    public Collection<CatalogSummaryView> getCatalogSummaryViewsWithStatusSendToNcbi() {
        // Get status
        CurationStatus curationStatus = curationStatusRepository.findByStatus("Send to NCBI");
        Long statusId = curationStatus.getId();

        // Use id of status to search view
        Collection<CatalogSummaryView> catalogSummaryViews =
                catalogSummaryViewRepository.findByCurationStatusOrderByStudyIdDesc(statusId);
        return catalogSummaryViews;
    }


}
