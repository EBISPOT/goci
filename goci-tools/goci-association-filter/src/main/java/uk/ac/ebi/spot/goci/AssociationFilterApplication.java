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
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;

/**
 * Created by dwelter on 05/04/16.
 */
public class AssociationFilterApplication {

    private static File outputFile;


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
            System.out.println("About to filter the input list");
            int parseArgs = parseArguments(strings);
            if (parseArgs == 0) {
                // execute mapper
//                this.doMappingsExport(outputFile);
            }
            else {
                // could not parse arguments, exit with exit code >1 (depending on parsing problem)
                System.err.println("Failed to parse supplied arguments");
                System.exit(1 + parseArgs);
            }
            System.out.println("Filtering complete");
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
        outputFileOption.setArgName("file");
        outputFileOption.setRequired(true);
        options.addOption(outputFileOption);

        return options;
    }

}
