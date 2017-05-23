package uk.ac.ebi.spot.goci.curation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import uk.ac.ebi.spot.goci.config.GOCIMailConfiguration;


import java.util.Properties;

/**
 * Created by emma on 10/02/15.
 *
 * @author emma
 *         <p>
 *         Email configuration, properties are stored in application.properties. Property values injected directly into
 *         beans using the @Value annotation:
 *         <p>
 *         The config is stored in a common place to avoid duplication @cinzia
 */
@Configuration
@Import(GOCIMailConfiguration.class)
public class MailConfiguration {

}
