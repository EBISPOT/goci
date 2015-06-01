package uk.ac.ebi.spot.goci;

import org.apache.commons.cli.*;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.exception.OWLConversionException;
import uk.ac.ebi.spot.goci.owl.ReasonedOntologyLoader;
import uk.ac.ebi.spot.goci.utils.FilterProperties;
import uk.ac.ebi.spot.goci.owl.OntologyLoader;
import uk.ac.ebi.spot.goci.service.GwasOwlPublisher;



import java.io.File;
import java.net.URI;
import java.util.Arrays;

/**
 * A driver class, containing a {@link #main(String[])} method, that can be used to run the datapublisher to convert the
 * GWAS catalog data into OWL.
 *
 * @author Tony Burdett Date 26/01/12
 */
@SpringBootApplication
public class GOCIDataPublisherDriver {
    private static File assertedOntologyFile;
    private static File inferredOntologyFile;

    @Autowired
    private GwasOwlPublisher gwasOwlPublisher;

    public GwasOwlPublisher getGwasOwlPublisher(){
        return gwasOwlPublisher;
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    //ReasonedOntologyLoader or AssertedOntologyLoader
    private OntologyLoader ontologyLoader = new ReasonedOntologyLoader();


    public static void main(String[] args) {
        System.out.println("Starting Goci data publisher...");
        ApplicationContext ctx = SpringApplication.run(GOCIDataPublisherDriver.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }

    @Bean CommandLineRunner run() {
        return strings -> {
            long start_time = System.currentTimeMillis();
            System.out.println("Building indexes with supplied params: " + Arrays.toString(strings));
            int parseArgs = parseArguments(strings);
                        if (parseArgs == 0) {
                            // execute publisher

                            this.publishAndSave(assertedOntologyFile, inferredOntologyFile);
            //                driver.loadAndPrintStats(assertedOntologyFile); // AJCB - This just dumps out some stats about the knowledgebase provided
                        }
                        else {
                            // could not parse arguments, exit with exit code >1 (depending on parsing problem)
                            System.err.println("Failed to parse supplied arguments");
                            System.exit(1 + parseArgs);
                        }
            long end_time = System.currentTimeMillis();
            String time = String.format("%.1f", ((double) (end_time - start_time)) / 1000);
            System.out.println("Indexing building complete in " + time + " s. - application will now exit");
        };
    }

    // JDBCSingleNucleotidePolymorphsm to be replaced by SingleNucleotidePolymorphismRepository
//    public static void main(String[] args) {
//        try {
//
////            ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"SpringBeans.xml"});
//
//            // parse arguments
//            int parseArgs = parseArguments(args);
//            if (parseArgs == 0) {
//                // execute publisher
//
//                GOCIDataPublisherDriver driver = new GOCIDataPublisherDriver();
//                driver.publishAndSave(assertedOntologyFile, inferredOntologyFile);
////                driver.loadAndPrintStats(assertedOntologyFile); // AJCB - This just dumps out some stats about the knowledgebase provided
//            }
//            else {
//                // could not parse arguments, exit with exit code >1 (depending on parsing problem)
//                System.err.println("Failed to parse supplied arguments");
//                System.exit(1 + parseArgs);
//            }
//        }
//        catch (Exception e) {
//            // failed to execute, exit with exit code 1
//            System.err.println("An unexpected error occurred\n\t(" + e.getMessage() + ")");
//            System.exit(1);
//        }
//    }

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

                    if(cl.hasOption("p")){
                        String pvalueFilter = cl.getOptionValue("p");
                        FilterProperties.setPvalueFilter(pvalueFilter);
                    }

                    if(cl.hasOption("d")) {
                        String dateFilter = cl.getOptionValue("d");
                        FilterProperties.setDateFilter(dateFilter);
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

//        Option inferredOutputFileOption = new Option("i", "inferred", true,
//                                                     "The output file to write the inferred version of the published ontology to");
//        inferredOutputFileOption.setArgName("file");
//        options.addOption(inferredOutputFileOption);

        Option pvalueFilterOption = new Option("p", "pvalue", true, "The minimum p-value on which to filter the knowledge base, in format nE-x, e.g. 5E-8");
        options.addOption(pvalueFilterOption);

        Option dateFilterOption = new Option("d", "date", true, "The date on which to filter the knowledge base, in format YYYY-MM-DD");
        options.addOption(dateFilterOption);

        return options;
    }



    private Logger getLog() {
        return log;
    }


    public void publishAndSave(File assertedOntologyFile, File inferredOntologyFile) throws RuntimeException {
        try {
            // publishAndSave the data
            System.out.println("Attempting to convert and publish GWAS data as OWL...");

            if(getGwasOwlPublisher() == null){
                System.out.println("getGwasOwlPublisher is NULL!!!!!!!!!");
            }else{
                System.out.println("getGwasOwlPublisher is NOT NULL!!!!!!!!!");

            }
            OWLOntology ontology = getGwasOwlPublisher().publishGWASData();

            // and save the result
            System.out.print("Ontology converted, saving asserted results...");
            getGwasOwlPublisher().saveGWASData(ontology, assertedOntologyFile);
            System.out.println("..done!");

            if (inferredOntologyFile != null) {
                // now get the inferred view
               System.out.println("Evaluating inferred view...");
                OWLReasoner reasoner = getGwasOwlPublisher().publishGWASDataInferredView(ontology);

                // now save inferred view
                System.out.print("Ontology fully classified, saving inferred results...");
                getGwasOwlPublisher().saveGWASDataInferredView(reasoner, inferredOntologyFile);
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

    public void loadAndPrintStats(File assertedOntologyFile) {
        try {
            // load ontology
            URI gwasDataURI = assertedOntologyFile.toURI();
            getLog().info("Loading GWAS data from " + gwasDataURI);

            OWLOntology ontology = ontologyLoader.getOntology();

            // print some stats
            int individualCount = ontology.getIndividualsInSignature().size();
            int axiomCount = ontology.getAxiomCount();

            System.out.println("Ontology '" + ontology.getOntologyID().getOntologyIRI() + "' contains:\n\t" +
                                       individualCount + " indivuals,\n\t" +
                                       axiomCount + " axioms");
        }
        catch (Exception e) {
            System.err.println("Failed to publish data to OWL (an unexpected exception occurred): " + e.getMessage());
            getLog().error("Failed to publish data to OWL (an unexpected exception occurred): ", e);
            throw new RuntimeException(e);
        }
    }
}
