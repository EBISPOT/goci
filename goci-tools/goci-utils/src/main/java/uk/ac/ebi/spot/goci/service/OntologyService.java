package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * Created by dwelter on 30/06/15.
 */


@Service
@Component
public class OntologyService {

    @NotNull @Value("${efo.location}")
    private Resource efoResource;


    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }
}
