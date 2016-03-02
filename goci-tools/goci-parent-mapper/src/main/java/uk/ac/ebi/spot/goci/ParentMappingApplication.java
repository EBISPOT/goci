package uk.ac.ebi.spot.goci;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.export.CatalogSpreadsheetExporter;
import uk.ac.ebi.spot.goci.model.TraitEntity;
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

//    @Autowired
//    private ReasonedOntologyLoader ontologyLoader;

    private static File outputFile;


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
            int parseArgs = parseArguments(strings);
            if (parseArgs == 0) {
                // execute mapper
                this.doMappingsExport(outputFile);
            }
            else {
                // could not parse arguments, exit with exit code >1 (depending on parsing problem)
                System.err.println("Failed to parse supplied arguments");
                System.exit(1 + parseArgs);
            }
            System.out.println("Mapping complete");
        };
    }

    private static int parseArguments(String[] args) {
        CommandLineParser parser = new GnuParser();
        HelpFormatter help = new HelpFormatter();
        Options options = bindOptions();

        int parseArgs = 0;
        try {
            CommandLine cl = parser.parse(options, args, true);

            // check for mode help option
            if (cl.hasOption("")) {
                // print out mode help
                help.printHelp("mapper", options, true);
                parseArgs += 1;
            }
            else {
                // find -o option (for asserted output file)
                if (cl.hasOption("o")) {
                    outputFile = new File(cl.getOptionValue("o"));

                }
                else {
                    System.err.println("-o (output file) argument is required");
                    help.printHelp("mapper", options, true);
                    parseArgs += 2;
                }
            }
        }
        catch (ParseException e) {
            System.err.println("Failed to read supplied arguments");
            help.printHelp("mapper", options, true);
            parseArgs += 4;
        }
        return parseArgs;
    }

    private static Options bindOptions() {
        Options options = new Options();

        // help
        Option helpOption = new Option("h", "help", false, "Print the help");
        options.addOption(helpOption);

        // add output file arguments
        Option outputFileOption = new Option("o", "output", true,
                                             "The output file to write the mapped traits to");
        outputFileOption.setArgName("file");
        outputFileOption.setRequired(true);
        options.addOption(outputFileOption);

        return options;
    }



    void doMappingsExport(File mappingsFile) throws IOException {
        getLog().debug("Collecting all traits from the database");
        Map<String, List<TraitEntity>> unmappedTraits = termLoadingService.getTraits();

        getLog().debug("Successfully retrieved " + unmappedTraits.keySet().size() + " unmapped traits");
        List<TraitEntity> mappedTraits = parentMappingService.mapTraits(unmappedTraits);

        getLog().debug("All traits mapped");
        String[][] data = transformMappings(mappedTraits);
        getLog().debug("Ready to write to file");
        catalogSpreadsheetExporter.writeToFile(data, mappingsFile);
    }


    String[][] transformMappings(List<TraitEntity> mappedTraits){
        List<String[]> lines = new ArrayList<>();
        String[] header = {"Disease trait", "EFO term", "EFO URI", "Parent term", "Parent URI"};

        lines.add(header);
        for(TraitEntity trait : mappedTraits){
            String[] line = new String[5];

            line[0] = trait.getTrait();
            line[1] = trait.getEfoTerm();
            line[2] = trait.getUri();
            line[3] = trait.getParentName();
            line[4] = trait.getParentUri();

            lines.add(line);
        }
        return lines.toArray(new String[lines.size()][]);
    }



}
