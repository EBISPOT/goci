package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.PublishedStudy;
import uk.ac.ebi.spot.goci.service.mail.MailSendingService;

import java.io.IOException;
import java.util.List;

/**
 * Created by dwelter on 29/06/16.
 */

@Service
public class DataReleaseQCService {

    private SolrQueryService solrQueryService;
    private MailSendingService mailSendingService;


    @Autowired
    public DataReleaseQCService(SolrQueryService solrQueryService,
                                MailSendingService mailSendingService) {
        this.solrQueryService = solrQueryService;
        this.mailSendingService = mailSendingService;
    }


    //service that is provides the link between the driver and the actual individual QC services



    public void emailLatestPublishedStudies(){
        try {
            List<PublishedStudy> publishedStudies = solrQueryService.getPublishedStudies();

            mailSendingService.sendSummaryEmail(publishedStudies);

        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    public SolrQueryService getSolrQueryService() {
        return solrQueryService;
    }

    public void setSolrQueryService(SolrQueryService solrQueryService) {
        this.solrQueryService = solrQueryService;
    }

    public MailSendingService getMailSendingService() {
        return mailSendingService;
    }

    public void setMailSendingService(MailSendingService mailSendingService) {
        this.mailSendingService = mailSendingService;
    }

    public void verifyDiagram() {}

    public void verifySolrIndex() {}

    public void verifyKnowledgeBase() {}

    public void runFullQCPipeline() {}
}
