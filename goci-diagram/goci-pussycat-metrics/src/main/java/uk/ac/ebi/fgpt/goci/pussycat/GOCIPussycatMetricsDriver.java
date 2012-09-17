package uk.ac.ebi.fgpt.goci.pussycat;

import org.apache.commons.cli.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.service.GWASOWLPublisher;

/**
 * Hello world!
 *
 */
public class GOCIPussycatMetricsDriver
{
  //  private static File inferredOntologyFile;

    public static void main(String[] args) {
        try {
            // parse arguments
            int parseArgs = parseArguments(args);
            if (parseArgs == 0) {
                // execute publisher
                GOCIPussycatMetricsDriver driver = new GOCIPussycatMetricsDriver();
                driver.runBenchmark();
//                driver.loadAndPrintStats(assertedOntologyFile); // AJCB - This just dumps out some stats about the knowledgebase provided
            }
            else {
                // could not parse arguments, exit with exit code >1 (depending on parsing problem)
                System.err.println("Failed to parse supplied arguments");
                System.exit(1 + parseArgs);
            }
        }
        catch (Exception e) {
            // failed to execute, exit with exit code 1
            System.err.println("An unexpected error occurred\n\t(" + e.getMessage() + ")");
            System.exit(1);
        }
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
                help.printHelp("benchmark", options, true);
                parseArgs += 1;
            }
            else {
                // find -o option (for asserted output file)
                if (cl.hasOption("o")) {
//                    String assertedOutputFileName = cl.getOptionValue("o");
//         //           assertedOntologyFile = new File(assertedOutputFileName);
//
//                    if (cl.hasOption("i")) {
//                        String inferredOutputFileName = cl.getOptionValue("i");
//                        inferredOntologyFile = new File(inferredOutputFileName);
//                    }
//
//                    if(cl.hasOption("p")){
//                        String pvalueFilter = cl.getOptionValue("p");
//           //             FilterProperties.setPvalueFilter(pvalueFilter);
//                    }
//
//                    if(cl.hasOption("d")) {
//                        String dateFilter = cl.getOptionValue("d");
//             //           FilterProperties.setDateFilter(dateFilter);
//                    }
                }
                else {
                    System.err.println("-o (ontology output file) argument is required");
                    help.printHelp("benchmark", options, true);
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

        Option inferredOutputFileOption = new Option("i", "inferred", true,
                "The output file to write the inferred version of the published ontology to");
        inferredOutputFileOption.setArgName("file");
        options.addOption(inferredOutputFileOption);

        Option pvalueFilterOption = new Option("p", "pvalue", true, "The minimum p-value on which to filter the knowledge base, in format nE-x, e.g. 5E-8");
        options.addOption(pvalueFilterOption);

        Option dateFilterOption = new Option("d", "date", true, "The date on which to filter the knowledge base, in format YYYY-MM-DD");
        options.addOption(dateFilterOption);

        return options;
    }

    private GWASOWLPublisher publisher;
    private OntologyConfiguration config;


    public GOCIPussycatMetricsDriver(){
        ApplicationContext ctx = new ClassPathXmlApplicationContext("goci-pussycat-metrics.xml");
        publisher = ctx.getBean("publisher", GWASOWLPublisher.class);
        config = ctx.getBean("config", OntologyConfiguration.class);

        /*set up something like a GOCIDataPublisherPussycatSession taht in turn intitalises a ReasonerSession*/
    }

    public void runBenchmark(){

    }

}


