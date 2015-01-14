package uk.ac.ebi.spot.goci.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.ebi.spot.goci.ui.model.GwasStudy;

import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 02/05/13
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
public class JDBCGwasStudyDAOTest {

    private JdbcTemplate template;
    private JDBCGwasStudyDAO dao;
    private String pmid, table;
    private GwasStudy study;

    public static final String STUDY_SELECT =
            "select ID, PMID, AUTHOR, STUDYDATE, PUBLICATION, LINKTITLE from ";



    public static final String STUDY_INSERT =
            "insert into UNCLASSIFIEDSTUDY ( " +
                    "PMID, AUTHOR, STUDYDATE, PUBLICATION, LINKTITLE, ELIGIBILITY) " +
                    "values (?, ?, ?, ?, ?, ?)";



    @Before
    public void setUp(){

        template = mock(JdbcTemplate.class);
        pmid = "123456";
        table = "gwasstudies";

        dao = new JDBCGwasStudyDAO();

        study = mock(GwasStudy.class);


        //when(dao.getStudyByPubMedID(pmid,table)).thenReturn(study);



    }

    @Test
    public void testStudyExists(){
 //        assertEquals(template, dao.getJdbcTemplate());
        System.out.println("this works");
//
//        dao.studyExists(pmid);
//
//        verify(dao).getStudyByPubMedID(pmid,table);




    }
}
