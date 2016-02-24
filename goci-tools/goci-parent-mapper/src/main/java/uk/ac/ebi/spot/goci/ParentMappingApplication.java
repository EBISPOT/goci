package uk.ac.ebi.spot.goci;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.export.CatalogSpreadsheetExporter;
import uk.ac.ebi.spot.goci.model.Trait;
import uk.ac.ebi.spot.goci.service.ParentMappingService;
import uk.ac.ebi.spot.goci.service.TermLoadingService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Dani on 15/02/2016.
 *
 * @author Dani
 *
 */


@SpringBootApplication
public class ParentMappingApplication {

    @Autowired
    private TermLoadingService termLoadingService;

    @Autowired
    private ParentMappingService parentMappingService;

    @Autowired
    private CatalogSpreadsheetExporter catalogSpreadsheetExporter;

    private File outputFile;


    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public static void main(String... args) {
        System.out.println("Starting Parent Mapper...");
        ApplicationContext ctx = SpringApplication.run(ParentMappingApplication.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }

    @Bean CommandLineRunner run() {
        return strings -> {
            System.out.println("About to map all database values");
            this.doMappingsExport(outputFile);
            System.out.println("Mapping complete");
        };
    }


    void doMappingsExport(File mappingsFile) throws IOException {
        Map<String, List<Trait>> unmappedTraits = termLoadingService.getTraits();
        List<Trait> mappedTraits = parentMappingService.mapTraits(unmappedTraits);
        String[][] data = transformMappings(mappedTraits);
        catalogSpreadsheetExporter.writeToFile(data, mappingsFile);
    }


    String[][] transformMappings(List<Trait> mappedTraits){
        List<String[]> lines = new ArrayList<>();
        String[] header = {"Disease trait", "EFO term", "EFO URI", "Parent term"};

        lines.add(header);
        for(Trait trait : mappedTraits){
            String[] line = new String[4];

            line[0] = trait.getTrait();
            line[1] = trait.getEfoTerm();
            line[2] = trait.getUri();
            line[3] = trait.getParent();

            lines.add(line);
        }
        return lines.toArray(new String[lines.size()][]);
    }



}
