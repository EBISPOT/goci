package uk.ac.ebi.spot.goci.model;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.exception.DocumentEmbeddingException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/02/15
 */
public class EmbeddableDocumentTest {
    private Study study;
    private Association association;
    private AssociationReport associationReport;
    private DiseaseTrait diseaseTrait;

    private StudyDocument studyDoc;
    private AssociationDocument associationDoc;
    private DiseaseTraitDocument traitDoc;

    @Before
    public void setUp() {
        Author firstAuthor = new Author();
        firstAuthor.setFullname("Doe John");
        firstAuthor.setFullnameStandart("Doe John");
        firstAuthor.setInitials("J");
        firstAuthor.setLastName("Doe");
        firstAuthor.setFirstName("Joe");
        firstAuthor.setOrcid("0000-0000-0000-0001");
        Publication publication = new Publication();
        publication.setPubmedId("123456");
        publication.setFirstAuthor(firstAuthor);
        publication.setTitle("title");
        publication.setPublication("publication");
        PublicationAuthors publicationAuthors = new PublicationAuthors();
        publicationAuthors.setAuthor(firstAuthor);
        publicationAuthors.setPublication(publication);
        publicationAuthors.setSort(1);
        List<PublicationAuthors> publicationAuthorsList = new ArrayList<>();
        publicationAuthorsList.add(publicationAuthors);
        publication.setPublicationAuthors(publicationAuthorsList);
        Housekeeping h = new Housekeeping();
        h.setLastUpdateDate(new Date());
        h.setCatalogPublishDate(new Date());

        this.study = new Study("initial sample size",
                               "replicate sample size",
//                               false,
//                               false,
                               false,
                               true,
                               false,
                               null,
                               "qualifier",
                               false,
                               false,
                               "study design comment",
                               "GCST999999",
                               false,
                               false,
                               false,
                               Collections.EMPTY_LIST,
                               Collections.EMPTY_LIST,
                               Collections.EMPTY_LIST,
                               null,
                               Collections.EMPTY_LIST,
                               h, null, Collections.EMPTY_LIST, Collections.EMPTY_LIST,
                               Collections.EMPTY_LIST);

        study.setPublicationId(publication);
        study.setId(1l);
        this.studyDoc = new StudyDocument(study);

        this.association = new AssociationBuilder().setPvalueExponent(1)
                .setPvalueMantissa(1)
                .setRiskFrequency(String.valueOf(0.5))
                .setPvalueDescription("(test)")
                .setRange("[NR]")
                .setOrPerCopyNum((float) 1.04)
                .setOrPerCopyRecip((float) 0.94)
                .setMultiSnpHaplotype(false)
                .setSnpInteraction(false)
                .setSnpApproved(false)
                .setSnpType("novel")
                .build();
        association.setId(2l);
        this.associationDoc = new AssociationDocument(association);

        this.diseaseTrait = new DiseaseTrait((long) 870, "Breast cancer");
        this.traitDoc = new DiseaseTraitDocument(diseaseTrait);
    }

    @Test
    public void testEmbed() {
        try {
            studyDoc.embed(associationDoc);
        }
        catch (DocumentEmbeddingException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testEmbedTrait() {
        try {
            traitDoc.embed(associationDoc);
        }
        catch (DocumentEmbeddingException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testIntrospection() {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(studyDoc.getClass());
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                System.out.println("Display name: " + pd.getDisplayName());
                System.out.println("Name: " + pd.getName());
                System.out.println("Read method: " + pd.getReadMethod());
                System.out.println("\t" + pd);
            }

        }
        catch (IntrospectionException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testTraitIntrospection() {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(traitDoc.getClass());
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                System.out.println("Display name: " + pd.getDisplayName());
                System.out.println("Name: " + pd.getName());
                System.out.println("Read method: " + pd.getReadMethod());
                System.out.println("\t" + pd);
            }

        }
        catch (IntrospectionException e) {
            e.printStackTrace();
            fail();
        }
    }
}
