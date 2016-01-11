package uk.ac.ebi.spot.goci.curation.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.curation.model.Ping;
import uk.ac.ebi.spot.goci.curation.service.MailService;

/**
 * Created by emma on 08/01/2016.
 * @author emma
 *
 * Daily check to see if Ensembl API is alive.
 */
@Component
public class DailyEnsemblPing {

    private MailService mailService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public DailyEnsemblPing(MailService mailService) {
        this.mailService = mailService;
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void pingEnsembl() {
        RestTemplate restTemplate = new RestTemplate();
        Ping ping = restTemplate.getForObject("http://rest.ensembl.org/info/ping?content-type=application/json",
                                              Ping.class);
        Integer num = ping.getPing();

        if (num != 1) {
            mailService.sendEnsemblPingFailureMail();
            getLog().error("Pinging Ensembl returned " + num);
        }
    }
}
