package uk.ac.ebi.fgpt.goci.service;


import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.fgpt.goci.dao.GwasStudyDAO;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;
import uk.ac.ebi.fgpt.goci.model.GwasStudy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.fail;
import static org.mockito.Mockito.*;

public class GwasPubmedSearcherTest
{

    private GwasPubMedDispatcherService dispatcherService;
    private GwasStudyDAO studyDao;
    private GwasPubmedSearcher searcher;
    private GwasStudy study, study2;



//    builds up the test
    @Before
    public void setUp(){
         try{

            String samplePMID = "12345678";
             String samplePMID2 = "87654321";


             Collection<String> samplePMIDs = Collections.singletonList(samplePMID);
             study = mock(GwasStudy.class);


             Map<String, GwasStudy> sampleStudies = new HashMap<String, GwasStudy>();
             sampleStudies.put(samplePMID, study);


            dispatcherService = mock(GwasPubMedDispatcherService.class);


            when(dispatcherService.dispatchSearchQuery()).thenReturn(samplePMIDs);



            studyDao = mock(GwasStudyDAO.class);

            when(studyDao.studyExists(samplePMID)).thenReturn(false);
             when(studyDao.studyExists(samplePMID2)).thenReturn(false);


            when(dispatcherService.dispatchSummaryQuery(samplePMIDs)).thenReturn(sampleStudies);

             searcher = new GwasPubmedSearcher();
             searcher.setDispatcherService(dispatcherService);
             searcher.setStudyDAO(studyDao);


         }

         catch(Exception e){
             e.printStackTrace();
             fail("Test failed");
         }
    }

    ////    closes the test
    //        @After
    //    public void tearDown(){
    //
    //    }

//    does the actual test
    @Test
    public void testDispatchSearch() throws DispatcherException {
        searcher.dispatchSearch();

        verify(studyDao).saveStudy(study);

    }


}
