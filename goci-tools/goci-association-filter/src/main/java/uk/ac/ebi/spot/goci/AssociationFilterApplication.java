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
import uk.ac.ebi.spot.goci.service.DataImportExportService;
import uk.ac.ebi.spot.goci.service.FilterDataProcessingService;

import java.io.File;

/**
 * Created by dwelter on 05/04/16.
 */
@SpringBootApplication
public class AssociationFilterApplication {

    private static File outputFile;
    private static File inputFile;
    private static Boolean prune = false;
    private static Double threshold = 1e-5;

    @Autowired
    private DataImportExportService dataImportExportService;

    @Autowired
    private FilterDataProcessingService filterDataProcessingService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public static void main(String... args) {
        System.out.println("Starting Association Filter...");

        ApplicationContext ctx = SpringApplication.run(AssociationFilterApplication.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }

    @Bean CommandLineRunner run() {
        return strings -> {

            int parseArgs = parseArguments(strings);
            if (parseArgs == 0) {
                System.out.println("About to filter the input listagainst threshold " + threshold);
                getLog().debug("About to filter the input list against threshold " + threshold);

                this.doFiltering(inputFile, outputFile);
            }
            else {
                // could not parse arguments, exit with exit code >1 (depending on parsing problem)
                System.err.println("Failed to parse supplied arguments");
                getLog().error("Failed to parse supplied arguments");
                System.exit(1 + parseArgs);
            }
            System.out.println("Filtering complete");
            getLog().debug("Filtering complete");

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
                help.printHelp("filter", options, true);
                parseArgs += 1;
            }
            else {
                // find -o option (for asserted output file)
                if (cl.hasOption("o")) {
                    outputFile = new File(cl.getOptionValue("o"));

                }
                else {
                    System.err.println("-o (output file) argument is required");
                    help.printHelp("filter", options, true);
                    parseArgs += 2;
                }
                if (cl.hasOption("f")) {
                    inputFile = new File(cl.getOptionValue("f"));
                }
                else {
                    System.err.println("-f (input file) argument is required");
                    help.printHelp("filter", options, true);
                    parseArgs += 2;
                }
                if(cl.hasOption("p")) {
                    prune = true;
                }
                if(cl.hasOption("t")){
                    threshold = Double.valueOf(cl.getOptionValue("t"));
                }
            }
        }
        catch (ParseException e) {
            System.err.println("Failed to read supplied arguments");
            help.printHelp("filter", options, true);
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
                                             "The output file to write the filtered list to");
        outputFileOption.setArgName("output");
        outputFileOption.setRequired(true);
        options.addOption(outputFileOption);

        Option inOption = new Option(
                "f",
                "file",
                true,
                "Input file - file where data to be filtered can be found");
        inOption.setArgName("file");
        inOption.setRequired(true);
        options.addOption(inOption);

        Option pruneOption = new Option(
                "p",
                "prune",
                false,
                "Prune output - removes associations with p-value < 1E-5 from the result output");
        pruneOption.setRequired(false);
        options.addOption(pruneOption);

        Option thresholdOption = new Option(
                "t",
                "threshold",
                true,
                "Filter only associations below the provided threshold. If no threshold is provided, the default of 1e-5 will be used.");
        thresholdOption.setRequired(false);
        options.addOption(thresholdOption);

        return options;
    }

    private void doFiltering(File inputFile, File outputFile) {
        try {
            getLog().info("Reading input file");
            String[][] data = dataImportExportService.readFromFile(inputFile);
            getLog().info("Input file processed, starting data transformation and filtering process");
            String[][] transformAssocations = filterDataProcessingService.filterInputData(data, threshold, prune);
            getLog().info("Exporting filtered data to file");
            dataImportExportService.writeToFile(transformAssocations, outputFile);
        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }





}
