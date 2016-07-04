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
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.service.DataReleaseQCService;

import java.util.Arrays;

/**
 * Created by dwelter on 28/06/16.
 */

@SpringBootApplication
public class GOCIDataReleaseQCDriver {

    private DataReleaseQCService dataReleaseQCService;
    private OperationMode opMode;


    private Logger log = LoggerFactory.getLogger(getClass());

    public GOCIDataReleaseQCDriver(DataReleaseQCService dataReleaseQCService) {
        this.dataReleaseQCService = dataReleaseQCService;
    }


    public static void main(String[] args) {
        System.out.println("Starting Goci data publisher...");
        ApplicationContext ctx = SpringApplication.run(GOCIDataReleaseQCDriver.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }


    @Bean CommandLineRunner run() {
        return strings -> {
            long start_time = System.currentTimeMillis();
            System.out.println("Building indexes with supplied params: " + Arrays.toString(strings));
            int parseArgs = parseArguments(strings);
            if (parseArgs == 0) {
                switch (opMode) {
                    case ALL :
                        dataReleaseQCService.runFullQCPipeline();
                        break;
                    case SUMMARY_EMAIL:
                        dataReleaseQCService.emailLatestPublishedStudies();
                        break;
                    case KNOWLEDGE_BASE:
                        dataReleaseQCService.verifyKnowledgeBase();
                        break;
                    case SOLR_INDEX:
                        dataReleaseQCService.verifySolrIndex();
                        break;
                    case DIAGRAM:
                        dataReleaseQCService.verifyDiagram();
                        break;
                    default:
                        System.err.println("No operation mode specified");
                        parseArgs += 1;
                }

            }
            else {
                // could not parse arguments, exit with exit code >1 (depending on parsing problem)
                System.err.println("Failed to parse supplied arguments");
                System.exit(1 + parseArgs);
            }
            long end_time = System.currentTimeMillis();
            String time = String.format("%.1f", ((double) (end_time - start_time)) / 1000);
            System.out.println("Indexing building complete in " + time + " s. - application will now exit - exit code " + parseArgs);
        };
    }


    private int parseArguments(String[] args) {
        CommandLineParser parser = new GnuParser();
        HelpFormatter help = new HelpFormatter();
        Options options = bindOptions();

        int parseArgs = 0;
        try {
            CommandLine cl = parser.parse(options, args, true);

            // check for mode help option
            if (cl.hasOption("")) {
                // print out mode help
                help.printHelp("qc", options, true);
                parseArgs += 1;
            }
            else {
                // find -o option (for asserted output file)
                if (cl.hasOption("a")) {
                    this.opMode = OperationMode.ALL;
                }

                else if (cl.hasOption("k")) {
                    this.opMode = OperationMode.KNOWLEDGE_BASE;
                }

                else if (cl.hasOption("e")) {
                    this.opMode = OperationMode.SUMMARY_EMAIL;
                }

                else if (cl.hasOption("d")) {
                    this.opMode = OperationMode.DIAGRAM;
                }
                else if (cl.hasOption("s")) {
                    this.opMode = OperationMode.SOLR_INDEX;
                }
                else {
                    System.err.println("-o (ontology output file) argument is required");
                    help.printHelp("publish", options, true);
                    parseArgs += 2;
                }
            }
        }
        catch (ParseException e) {
            System.err.println("Failed to read supplied arguments");
            help.printHelp("publish", options, true);
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
                                             "The output file to write the published ontology to");
        outputFileOption.setArgName("file");
        outputFileOption.setRequired(true);
        options.addOption(outputFileOption);

        Option pvalueFilterOption = new Option("p",
                                               "pvalue",
                                               true,
                                               "The minimum p-value on which to filter the knowledge base, in format nE-x, e.g. 5E-8");
        options.addOption(pvalueFilterOption);

        Option dateFilterOption =
                new Option("d", "date", true, "The date on which to filter the knowledge base, in format YYYY-MM-DD");
        options.addOption(dateFilterOption);

        return options;
    }

    private Logger getLog() {
        return log;
    }

    private enum OperationMode {
        ALL,
        SUMMARY_EMAIL,
        KNOWLEDGE_BASE,
        SOLR_INDEX,
        DIAGRAM
    }
}
