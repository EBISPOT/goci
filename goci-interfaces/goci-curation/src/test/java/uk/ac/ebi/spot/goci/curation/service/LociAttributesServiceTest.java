package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.LocusBuilder;
import uk.ac.ebi.spot.goci.builder.RiskAlleleBuilder;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.repository.GeneRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;
import uk.ac.ebi.spot.goci.repository.RiskAlleleRepository;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by emma on 15/08/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class LociAttributesServiceTest {

    @Mock
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    @Mock
    private GeneRepository geneRepository;

    @Mock
    private RiskAlleleRepository riskAlleleRepository;

    @Mock
    private LocusRepository locusRepository;

    private LociAttributesService lociAttributesService;

    private static final Locus LOCUS = new LocusBuilder().setId((long) 100).build();

    private static final RiskAllele RISK_ALLELE = new RiskAlleleBuilder().setId((long) 120).build();

    @Before
    public void setUp() throws Exception {
        lociAttributesService = new LociAttributesService(singleNucleotidePolymorphismRepository,
                                                          geneRepository,
                                                          riskAlleleRepository,
                                                          locusRepository);
    }

    @Test
    public void createGene() throws Exception {

    }

    @Test
    public void createRiskAllele() throws Exception {

    }

    @Test
    public void deleteRiskAllele() throws Exception {
        lociAttributesService.deleteRiskAllele(RISK_ALLELE);
        verify(riskAlleleRepository, times(1)).delete(RISK_ALLELE);
    }

    @Test
    public void deleteLocus() throws Exception {
        lociAttributesService.deleteLocus(LOCUS);
        verify(locusRepository, times(1)).delete(LOCUS);
    }

    @Test
    public void createSnp() throws Exception {

    }

}