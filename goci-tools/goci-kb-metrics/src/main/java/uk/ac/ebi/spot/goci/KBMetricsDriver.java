package uk.ac.ebi.spot.goci;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.checker.IRITreeProcessor;
import uk.ac.ebi.spot.goci.exception.MetricsCalculationException;
import uk.ac.ebi.spot.goci.kb.KBLoader;
import uk.ac.ebi.spot.goci.tree.IRINode;
import uk.ac.ebi.spot.goci.tree.IRITree;
import uk.ac.ebi.spot.goci.tree.IRITreeBuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 08/08/12
 */
@SpringBootApplication
public class KBMetricsDriver {
    private static URL _efoLocation;
    private static URL _gwasSchemaLocation;
    private static URL _kbLocation;
    private static int _watershedCutoff;

    private static OutputStream _out;


    @Autowired
    private KBLoader loader;

    @Autowired
    private IRITreeBuilder treeBuilder;

    @Autowired
    private IRITreeProcessor treeProcessor;


    public static void main(String[] args) {
        System.out.println("Starting KB Metrics renderer...");
        ApplicationContext ctx = SpringApplication.run(KBMetricsDriver.class, args);
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
                try {
                    this.generateMetricsReport(_out);
                    System.out.println("Metrics report complete!");

                    if (_out != System.out) {
                        _out.close();
                    }
                }
                catch (MetricsCalculationException e) {
                    System.err.println("Failed to calculate metrics - " + e.getMessage());
                    e.printStackTrace();
                    System.exit(1);
                }
                catch (IOException e) {
                    System.err.println("Failed to close output stream - report may not have written correctly");
                    System.exit(-1);
                }
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
                help.printHelp("evaluate", options, true);
                parseArgs += 1;
            }
            else {
                System.out.println("Metrics will be calculated with the following options...");
                for (Option opt : cl.getOptions()) {
                    System.out.println("\t" + opt.getLongOpt() + ": " + opt.getValue() + " (" + opt.getArgName() + ")");
                }

                if (cl.hasOption("efo")) {
                    _efoLocation = new URL(cl.getOptionValue("efo"));
                    System.out.println(_efoLocation);
                }
                else {
                    System.err.println("-efo (EFO location) argument is required");
                    help.printHelp("evaluate", options, true);
                    parseArgs += 2;
                }

                if (cl.hasOption("gwas")) {
                    _gwasSchemaLocation = new File(cl.getOptionValue("gwas")).toURI().toURL();
                    System.out.println(_gwasSchemaLocation);

                }
                else {
                    System.err.println("-gwas (GWAS Schema File) argument is required");
                    help.printHelp("evaluate", options, true);
                    parseArgs += 2;
                }

                if (cl.hasOption("kb")) {
                    _kbLocation = new File(cl.getOptionValue("kb")).toURI().toURL();
                    System.out.println(_kbLocation);

                }
                else {
                    System.err.println("-kb (Knowledgebase File) argument is required");
                    help.printHelp("evaluate", options, true);
                    parseArgs += 2;
                }

                if (cl.hasOption("w")) {
                    _watershedCutoff = Integer.parseInt(cl.getOptionValue("w"));
                    //                    System.setProperty("watershed.cutoff", String.valueOf(_watershedCutoff));
                }
                else {
                    System.err.println("-w (Watershed cutoff) argument is required");
                    help.printHelp("evaluate", options, true);
                    parseArgs += 2;
                }

                if (cl.hasOption("out")) {
                    String outOpt = cl.getOptionValue("out");
                    _out = new BufferedOutputStream(new FileOutputStream(new File(outOpt)));
                }
                else {
                    System.out.println("No output file specified, report will be written to standard out");
                    _out = System.out;
                }
            }
        }
        catch (ParseException e) {
            System.err.println("Failed to read supplied arguments (" + e.getMessage() + ")");
            help.printHelp("evaluate", options, true);
            parseArgs += 4;
        }
        catch (FileNotFoundException e) {
            System.err.println("Failed to read supplied arguments - file not found (" + e.getMessage() + ")");
            help.printHelp("evaluate", options, true);
            parseArgs += 5;
        }
        catch (MalformedURLException e) {
            System.err.println("Failed to read supplied arguments - a supplied argument was not a valid URL " +
                                       "(" + e.getMessage() + ")");
            help.printHelp("evaluate", options, true);
            parseArgs += 6;
        }
        return parseArgs;
    }

    private static Options bindOptions() {
        Options options = new Options();

        // help
        Option helpOption = new Option("h", "help", false, "Print the help");
        options.addOption(helpOption);

        // add output file arguments
        Option efoLocationOption = new Option(
                "efo",
                "efoLocation",
                true,
                "The location to load EFO from");
        efoLocationOption.setArgName("URL");
        efoLocationOption.setRequired(true);
        options.addOption(efoLocationOption);

        Option gwasSchemaFileOption = new Option(
                "gwas",
                "gwasSchemaFile",
                true,
                "The file to load the GWAS schema ontology from");
        gwasSchemaFileOption.setArgName("file");
        gwasSchemaFileOption.setRequired(true);
        options.addOption(gwasSchemaFileOption);

        Option kbFileOption = new Option(
                "kb",
                "kbDataFile",
                true,
                "The file to load the GWAS knowledgebase data from");
        kbFileOption.setArgName("file");
        kbFileOption.setRequired(true);
        options.addOption(kbFileOption);

        Option watershedOption = new Option(
                "w",
                "watershed",
                true,
                "The watershed cutoff - nodes with more than this value will be rendered");
        watershedOption.setArgName("integer");
        watershedOption.setRequired(true);
        options.addOption(watershedOption);

        Option outFileOption = new Option(
                "out",
                "outputFile",
                true,
                "The file to write the report to");
        outFileOption.setArgName("file");
        outFileOption.setRequired(false);
        options.addOption(outFileOption);

        return options;
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }


    /**
     * Generate a report on the data metrics of the knowledgebase and write them this report to the supplied output
     * stream
     *
     * @param out the output stream to write the report to
     */
    public void generateMetricsReport(OutputStream out) throws MetricsCalculationException, IOException {
        try {
            // first, calculate data spread in KB
            Map<IRI, Integer> metrics = loader.quantifyKnowledgeBase(_efoLocation, _gwasSchemaLocation, _kbLocation);

            // build the efo tree
            IRITree tree = treeBuilder.buildIRITree(_efoLocation);

            // now tree is constructed, overlay counts onto it
            walkTreeAndAddCounts(tree.getRootNode(), metrics);

            // process the tree and write the "ideal legend" report
            treeProcessor.processTree(tree, out);

            // write the tree into a report
            tree.prettyPrint(out);
        }
        catch (OWLOntologyCreationException e) {
            throw new MetricsCalculationException(
                    "Failed to calculate metrics because one or more ontologies failed to load " +
                            "(" + e.getMessage() + ")", e);
        }
        catch (URISyntaxException e) {
            throw new MetricsCalculationException(
                    "Failed to calculate metrics - a reference to a supplied location was not valid " +
                            "(" + e.getMessage() + ")", e);
        }
    }

    private void walkTreeAndAddCounts(IRINode node, Map<IRI, Integer> metrics) {
        // walk to child nodes first
        for (IRINode childNode : node.getChildNodes()) {
            walkTreeAndAddCounts(childNode, metrics);
        }

        // get counts for all children
        int childCount = 0;
        for (IRINode childNode : node.getChildNodes()) {
            childCount += childNode.getUsageCount();
        }

        // get count for this node
        int nodeCount = 0;
        IRI iri = node.getIRI();
        if (metrics.containsKey(iri)) {
            nodeCount = metrics.get(iri);
        }

        // set count for this node
        node.setUsageCount(childCount + nodeCount);
    }
}
