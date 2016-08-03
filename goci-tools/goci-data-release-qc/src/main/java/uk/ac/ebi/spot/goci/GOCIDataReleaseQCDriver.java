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
import uk.ac.ebi.spot.goci.service.DataReleaseQCService;

import java.util.Arrays;

/**
 * Created by dwelter on 28/06/16.
 */

@SpringBootApplication
public class GOCIDataReleaseQCDriver {

    @Autowired
    private DataReleaseQCService dataReleaseQCService;
    private OperationMode opMode;


    private Logger log = LoggerFactory.getLogger(getClass());

//    @Autowired
//    public GOCIDataReleaseQCDriver(DataReleaseQCService dataReleaseQCService) {
//        this.dataReleaseQCService = dataReleaseQCService;
//    }


    public static void main(String[] args) {
        System.out.println("Starting GOCI data release QC pipeline...");
        ApplicationContext ctx = SpringApplication.run(GOCIDataReleaseQCDriver.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }


    @Bean CommandLineRunner run() {
        return strings -> {
            long start_time = System.currentTimeMillis();
            System.out.println("Running QC pipeline with supplied params: " + Arrays.toString(strings));
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
            System.out.println("QC pipeline complete in " + time + " s. - application will now exit - exit code " + parseArgs);
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
        Option allQCOption = new Option("a", "all", false,
                                             "Run the full QC pipeline");
        options.addOption(allQCOption);

        Option emailOption = new Option("e", "email",
                                               false,
                                               "Check the most recently published studies and email them out");
        options.addOption(emailOption);

        Option knowledgeBaseOption =
                new Option("k", "knowledgebase", false, "Run knowledge base QC tasks");
        options.addOption(knowledgeBaseOption);

        Option diagramOption = new Option("d", "diagram", false, "Run diagram QC tasks");
        options.addOption(diagramOption);

        Option solrOption = new Option("s", "solr", false, "Run Solr QC tasks");
        options.addOption(solrOption);

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
