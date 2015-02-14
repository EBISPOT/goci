
package uk.ac.ebi.spot.goci.model;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.spot.goci.exception.DocumentEmbeddingException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.fail;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/02/15
 */
public class EmbeddableDocumentTest {
    private Study study;
    private SingleNucleotidePolymorphism snp;

    private StudyDocument studyDoc;
    private SnpDocument snpDoc;

    @Before
    public void setUp() {
        Housekeeping h = new Housekeeping();
        h.setLastUpdateDate(new Date());
        h.setPublishDate(new Date());
        this.study = new Study("author", new Date(), "publication", "title", "initial sample size", "replicate " +
                "sample size", "platform", "123456", false, false, false, null, Collections.<EfoTrait>emptyList(),
                                Collections.<SingleNucleotidePolymorphism>emptyList(), h);
        study.setId(1l);
        this.studyDoc = new StudyDocument(study);
        this.snp = new SingleNucleotidePolymorphism("rs1234", "1", "1234567", 1l, "intron",
                                                                            new Date(), Collections.emptyList());
        snp.setId(2l);
        this.snpDoc = new SnpDocument(snp);
    }

    @Test
    public void testEmbed() {
        try {
            snpDoc.embed(studyDoc);
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
}
