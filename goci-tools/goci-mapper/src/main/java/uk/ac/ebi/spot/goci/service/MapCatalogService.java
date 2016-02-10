package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.repository.AssociationReportRepository;

import java.util.Collection;

/**
 * Created by emma on 05/02/2016.
 *
 * @author emma
 *         <p>
 *         Service used to map all associations in the catalog.
 */
@Service
public class MapCatalogService {

    private AssociationReportRepository associationReportRepository;

    // Services
    private AssociationQueryService associationService;
    private MappingErrorComparisonService mappingErrorComparisonService;
    private MappingService mappingService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public MapCatalogService(AssociationReportRepository associationReportRepository,
                             AssociationQueryService associationService,
                             MappingErrorComparisonService mappingErrorComparisonService,
                             MappingService mappingService) {
        this.associationReportRepository = associationReportRepository;
        this.associationService = associationService;
        this.mappingErrorComparisonService = mappingErrorComparisonService;
        this.mappingService = mappingService;
    }

    /**
     * Get all associations in database and map
     *
     * @param performer name of curator/job carrying out the mapping
     */
    public void mapCatalogContents(String performer) throws EnsemblMappingException {

        // Get all old association reports so we can compare with new ones, do this before we remap
        Collection<AssociationReport> oldAssociationReports = associationReportRepository.findAll();

        // Get all associations via service
        Collection<Association> associations = associationService.findAllAssociations();
        getLog().info("Mapping all associations in database, total number: " + associations.size());
        try {
            mappingService.validateAndMapAllAssociations(associations, performer);
        }
        catch (EnsemblMappingException e) {
            throw new EnsemblMappingException("Attempt to map all associations failed", e);
        }
        mappingErrorComparisonService.compareOldVersusNewErrors(oldAssociationReports);
    }
}
