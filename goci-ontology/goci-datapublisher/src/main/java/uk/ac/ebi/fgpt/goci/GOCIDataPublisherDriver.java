package uk.ac.ebi.fgpt.goci;

import org.apache.commons.cli.*;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.service.DefaultGWASOWLPublisher;
import uk.ac.ebi.fgpt.goci.service.GWASOWLPublisher;

import java.io.File;

/**
 * A driver class, containing a {@link #main(String[])} method, that can be used to run the datapublisher to convert the
 * GWAS catalog data into OWL.
 *
 * @author Tony Burdett
 * @date 26/01/12
 */
public class GOCIDataPublisherDriver {
    private static File assertedOntologyFile;
    private static File inferredOntologyFile;

    public static void main(String[] args) {
        try {
            // parse arguments
            int parseArgs = parseArguments(args);
            if (parseArgs == 0) {
                // execute publisher
                GOCIDataPublisherDriver driver = new GOCIDataPublisherDriver();
                driver.publishAndSave(assertedOntologyFile, inferredOntologyFile);
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
                help.printHelp("publish", options, true);
                parseArgs += 1;
            }
            else {
                // find -o option (for asserted output file)
                if (cl.hasOption("o")) {
                    String assertedOutputFileName = cl.getOptionValue("o");
                    assertedOntologyFile = new File(assertedOutputFileName);

                    if (cl.hasOption("i")) {
                        String inferredOutputFileName = cl.getOptionValue("i");
                        inferredOntologyFile = new File(inferredOutputFileName);
                    }
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

        Option inferredOutputFileOption = new Option("i", "inferred", true,
                                                     "The output file to write the inferred version of the published ontology to");
        inferredOutputFileOption.setArgName("file");
        options.addOption(inferredOutputFileOption);

        return options;
    }

    private GWASOWLPublisher publisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    private Logger getLog() {
        return log;
    }

    public GOCIDataPublisherDriver() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("goci-datapublisher.xml");
        publisher = ctx.getBean("publisher", DefaultGWASOWLPublisher.class);
    }

    public void publishAndSave(File assertedOntologyFile, File inferredOntologyFile) throws RuntimeException {
        try {
            // publishAndSave the data
            System.out.println("Attempting to convert and publish GWAS data as OWL...");
            OWLOntology ontology = publisher.publishGWASData();

            // and save the result
            System.out.print("Ontology converted, saving asserted results...");
            publisher.saveGWASData(ontology, assertedOntologyFile);
            System.out.println("..done!");

            if (inferredOntologyFile != null) {
                // now get the inferred view
                System.out.println("Evaluating inferred view...");
                OWLReasoner reasoner = publisher.publishGWASDataInferredView(ontology);

                // now save inferred view
                System.out.print("Ontology fully classified, saving inferred results...");
                publisher.saveGWASDataInferredView(reasoner, inferredOntologyFile);
                System.out.println("..done!");
            }
        }
        catch (OWLConversionException e) {
            System.err.println("Failed to publish data to OWL: " + e.getMessage());
            getLog().error("Failed to publish data to OWL: ", e);
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            System.err.println("Failed to publish data to OWL (an unexpected exception occurred): " + e.getMessage());
            getLog().error("Failed to publish data to OWL (an unexpected exception occurred): ", e);
            throw new RuntimeException(e);
        }
    }
}
