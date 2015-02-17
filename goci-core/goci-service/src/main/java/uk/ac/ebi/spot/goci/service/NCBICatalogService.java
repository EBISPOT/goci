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


    public Collection<CatalogSummaryView> getCatalogSummaryViewsSendToNcbi() {
        CurationStatus curationStatus = curationStatusRepository.findByStatus("Send to NCBI");
        Long statusId = curationStatus.getId();
        Collection<CatalogSummaryView> catalogSummaryViews =
                catalogSummaryViewRepository.findByCurationStatus(statusId);
        return catalogSummaryViews;
    }


}
