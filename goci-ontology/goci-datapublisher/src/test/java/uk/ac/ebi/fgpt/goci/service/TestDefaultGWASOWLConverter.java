package uk.ac.ebi.fgpt.goci.service;

import junit.framework.TestCase;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.lang.UniqueID;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.fgpt.goci.model.Study;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

import java.util.*;

/**
 * Javadocs go here.
 *
 * @author Junit Generation Plugin for Maven, written by Tony Burdett
 * @date 13-02-2012
 */
public class TestDefaultGWASOWLConverter extends TestCase {
    private DefaultGWASOWLConverter converter;

    private OWLOntology ontology;
    private Study study;
    private TraitAssociation association;
    private SingleNucleotidePolymorphism snp;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void setUp() {
        // add setup logic here
        this.converter = new DefaultGWASOWLConverter();

        try {
            ontology = converter.getManager().createOntology();
            // create some mock objects we can use to convert
            study = new MockStudy();
            association = new MockAssociation();
            snp = new MockSNP();
        }
        catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void tearDown() {
        // add logic required to terminate the class here
    }

    public void testGetLog() {
        assertNotNull(converter.getLog());
    }

    public void testGetMinter() {
        assertNotNull(converter.getMinter());
    }

    public void testGetManager() {
        assertNotNull(converter.getManager());
    }

    public void testGetDataFactory() {
        assertNotNull(converter.getDataFactory());
    }

    public void testCreateConversionOntology() {
        OWLOntology ontology1 = null;
        try {
            ontology1 = converter.createConversionOntology();
        }
        catch (OWLConversionException e) {
            e.printStackTrace();
            fail();
        }
        assertNotNull(ontology1);
        assertNotNull(ontology1.getOntologyID().getOntologyIRI());
    }

    public void testAddStudiesToOntology() {
        try {
            invokeAddStudiesToOntology(study);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void invokeAddStudiesToOntology(Study s) {
        // check there's nothing in the ontology
        assertEquals(0, ontology.getIndividualsInSignature().size());
        // add a single study
        converter.addStudiesToOntology(Collections.singletonList(s), ontology);
        // verify that the number of instances in the ontology has increased to one
        assertEquals(1, ontology.getIndividualsInSignature().size());
        // verify that the individual is an instance of the study class
        OWLNamedIndividual individual = ontology.getIndividualsInSignature().iterator().next();
        OWLClass studyCls = converter.getDataFactory().getOWLClass(IRI.create(OntologyConstants.STUDY_CLASS_IRI));
        assertTrue(individual.getTypes(ontology).contains(studyCls));
    }

    public void testAddAssociationsToOntology() {
        try {
            invokeAddAssociationsToOntology(association);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    public void invokeAddAssociationsToOntology(TraitAssociation ta) {
        // check there's nothing in the ontology
        assertEquals(0, ontology.getIndividualsInSignature().size());
        Collection<SingleNucleotidePolymorphism> snps = new ArrayList<SingleNucleotidePolymorphism>();
        // converter assumes snps will already be present
        snps.add(ta.getAssociatedSNP());
        converter.addSNPsToOntology(snps, ontology);
        // add a single association
        converter.addAssociationsToOntology(Collections.singletonList(ta), ontology);
        // verify that the number of instances in the ontology has increased to three
        assertEquals(5, ontology.getIndividualsInSignature().size());
        // verify that the individual is an instance of the association or trait class
        int traitCount = 0;
        int associationCount = 0;
        OWLClass associationCls =
                converter.getDataFactory().getOWLClass(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));
        OWLClass efCls =
                converter.getDataFactory().getOWLClass(IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI));
        for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
            if (individual.getTypes(ontology).contains(associationCls)) {
                associationCount++;
            }
            if (individual.getTypes(ontology).contains(efCls)) {
                traitCount++;
            }
        }
        assertEquals("Wrong number of trait individuals", 1, traitCount);
        assertEquals("Wrong number of association individuals", 1, associationCount);
    }

    public void testAddSNPsToOntology() {
        try {
            invokeAddSNPsToOntology(snp);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    public void invokeAddSNPsToOntology(SingleNucleotidePolymorphism snp) {
        // check there's nothing in the ontology
        assertEquals(0, ontology.getIndividualsInSignature().size());
        // add a single study
        converter.addSNPsToOntology(Collections.singletonList(snp), ontology);
        // verify that the number of instances in the ontology has increased to three (one snp, one chromosome, one band)
        assertEquals(3, ontology.getIndividualsInSignature().size());
        // verify that the individual is an instance of the study class
        int snpCount = 0;
        int chromCount = 0;
        int bandCount = 0;
        OWLClass snpCls = converter.getDataFactory().getOWLClass(IRI.create(OntologyConstants.SNP_CLASS_IRI));
        OWLClass chromCls =
                converter.getDataFactory().getOWLClass(IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI));
        OWLClass bandCls =
                converter.getDataFactory().getOWLClass(IRI.create(OntologyConstants.CYTOGENIC_REGION_CLASS_IRI));
        for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
            if (individual.getTypes(ontology).contains(snpCls)) {
                snpCount++;
            }
            if (individual.getTypes(ontology).contains(chromCls)) {
                chromCount++;
            }
            if (individual.getTypes(ontology).contains(bandCls)) {
                bandCount++;
            }
        }
        assertEquals("Wrong number of snp individuals", 1, snpCount);
        assertEquals("Wrong number of chromosome individuals", 1, chromCount);
        assertEquals("Wrong number of band individuals", 1, bandCount);
    }

    private class MockStudy implements Study {
        public String getAuthorName() {
            return "Jeff Nonymous";
        }

        @UniqueID
        public String getPubMedID() {
            return "1234";
        }

        public Date getPublishedDate() {
            return new Date();
        }

        public Collection<TraitAssociation> getIdentifiedAssociations() {
            return Collections.emptySet();
        }
    }

    private class MockAssociation implements TraitAssociation {
        @UniqueID
        private String getID() {
            return "1234";
        }

        public String getPubMedID() {
            return "5678";
        }

        public String getAssociatedSNPReferenceId() {
            return "snp-id";
        }

        public SingleNucleotidePolymorphism getAssociatedSNP() {
            return new MockSNP();
        }

        public OWLClass getAssociatedTrait() {
            return OWLManager.getOWLDataFactory()
                    .getOWLClass(IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI));
        }

        public float getPValue() {
            return 0;
        }

        public String getUnmappedGWASLabel() {
            return "foo";
        }
    }

    private class MockSNP implements SingleNucleotidePolymorphism {
        @UniqueID
        public String getRSID() {
            return "1234";
        }

        public String getChromosomeName() {
            return "chromosome 1";
        }

        public String getCytogeneticBandName() {
            return "1p27";
        }

        public String getSNPLocation() {
            return "123456789";
        }
    }
}
