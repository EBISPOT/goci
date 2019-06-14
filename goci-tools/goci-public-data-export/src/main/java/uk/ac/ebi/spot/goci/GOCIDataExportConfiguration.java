package uk.ac.ebi.spot.goci;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Created by Dani on 11/01/17.
 */

@Component
public class GOCIDataExportConfiguration {

    @Bean
    @ConfigurationProperties(prefix="datasource.public")
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .build();
    }
}
