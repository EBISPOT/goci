package uk.ac.ebi.fgpt.goci.pussycat;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxClassExpressionParser;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.manager.PussycatManager;
import uk.ac.ebi.fgpt.goci.pussycat.metrics.DateTimeStamp;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.session.OntologyLoadingCacheableReasonerSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.ReasonerSession;

import java.io.IOException;
import java.util.*;

/**
 * Hello world!
 */
public class GOCIPussycatMetricsDriver {

    public static void main(String[] args) {
//        try {
//            // parse arguments
//            int parseArgs = parseArguments(args);
//            if (parseArgs == 0) {
//                // execute publisher
        GOCIPussycatMetricsDriver driver = new GOCIPussycatMetricsDriver();
        try {
            driver.runBenchmark();
        }
        catch (Exception e) {
            System.err.println("Benchmarking failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
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
    }

//    private static int parseArguments(String[] args) {
//        CommandLineParser parser = new GnuParser();
//        HelpFormatter help = new HelpFormatter();
//        Options options = bindOptions();
//
//        int parseArgs = 0;
//        try {
//            CommandLine cl = parser.parse(options, args, true);
//
//            // check for mode help option
//            if (cl.hasOption("")) {
//                // print out mode help
//                help.printHelp("benchmark", options, true);
//                parseArgs += 1;
//            }
//            else {
//                // find -o option (for asserted output file)
//                if (cl.hasOption("o")) {
////                    String assertedOutputFileName = cl.getOptionValue("o");
////         //           assertedOntologyFile = new File(assertedOutputFileName);
////
////                    if (cl.hasOption("i")) {
////                        String inferredOutputFileName = cl.getOptionValue("i");
////                        inferredOntologyFile = new File(inferredOutputFileName);
////                    }
////
////                    if(cl.hasOption("p")){
////                        String pvalueFilter = cl.getOptionValue("p");
////           //             FilterProperties.setPvalueFilter(pvalueFilter);
////                    }
////
////                    if(cl.hasOption("d")) {
////                        String dateFilter = cl.getOptionValue("d");
////             //           FilterProperties.setDateFilter(dateFilter);
////                    }
//                }
//                else {
//                    System.err.println("-o (ontology output file) argument is required");
//                    help.printHelp("benchmark", options, true);
//                    parseArgs += 2;
//                }
//            }
//        }
//        catch (ParseException e) {
//            System.err.println("Failed to read supplied arguments");
//            help.printHelp("publish", options, true);
//            parseArgs += 4;
//        }
//        return parseArgs;
//    }
//
//    private static Options bindOptions() {
//        Options options = new Options();
//
//        // help
//        Option helpOption = new Option("h", "help", false, "Print the help");
//        options.addOption(helpOption);
//
//        // add output file arguments
//        Option outputFileOption = new Option("o", "output", true,
//                "The output file to write the published ontology to");
//        outputFileOption.setArgName("file");
//        outputFileOption.setRequired(true);
//        options.addOption(outputFileOption);
//
//        Option inferredOutputFileOption = new Option("i", "inferred", true,
//                "The output file to write the inferred version of the published ontology to");
//        inferredOutputFileOption.setArgName("file");
//        options.addOption(inferredOutputFileOption);
//
//        Option pvalueFilterOption = new Option("p", "pvalue", true, "The minimum p-value on which to filter the
// knowledge base, in format nE-x, e.g. 5E-8");
//        options.addOption(pvalueFilterOption);
//
//        Option dateFilterOption = new Option("d", "date", true, "The date on which to filter the knowledge base,
// in format YYYY-MM-DD");
//        options.addOption(dateFilterOption);
//
//        return options;
//    }

    private OntologyConfiguration config;
    private ReasonerSession reasonerSession;
    //    private BenchmarkPussycatSession pussycatSession;
    private PussycatSession pussycatSession;
    private PussycatManager pussycatManager;
    private RenderletNexus renderletNexus;


    private Logger log = LoggerFactory.getLogger(getClass());
    private Logger bm_log = LoggerFactory.getLogger("benchmark.output.log");

    protected Logger getLog() {
        return log;
    }


    public GOCIPussycatMetricsDriver() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("goci-pussycat.xml");
        config = ctx.getBean("config", OntologyConfiguration.class);
        reasonerSession = ctx.getBean("reasonerSession", OntologyLoadingCacheableReasonerSession.class);
        pussycatSession = ctx.getBean("pussycatSession", PussycatSession.class);
        pussycatManager = ctx.getBean("pussycatManager", PussycatManager.class);

    }

    public void runBenchmark() throws IOException {
        getLog().info("Running benchmarking code");

        long start, end;
        start = System.currentTimeMillis();
        while (!reasonerSession.isReasonerInitialized()) {
            try {
                getLog().info("No reasoner session available, holding");
                synchronized (this) {
                    wait(30000);
                }
            }
            catch (InterruptedException e) {
                // do nothing
            }
        }

        end = System.currentTimeMillis();
        double time = ((double) (end - start)) / 1000;
        bm_log.info("Reasoner session acquired after " + time + " s");

        setRenderletNexus();
        getLog().info("Renderlet nexus set");

        Map<OWLClassExpression, String> allSVG = new HashMap<OWLClassExpression, String>();

        Properties dates = new Properties();
        dates.load(GOCIPussycatMetricsDriver.class.getClassLoader().getResourceAsStream("filters.properties"));

        Enumeration queryDates = dates.propertyNames();
        OWLClassExpression query = null;

        while (queryDates.hasMoreElements()) {
            query = createClassExpression((String) dates.get(queryDates.nextElement()));
            bm_log.info(DateTimeStamp.getCurrentTimeStamp() + " Acquiring SVG for OWL class expression " + query);
            String svg = "";
            boolean nexusReady = true;
            do {
                try {
                    getLog().info("Performing SVG rendering for '" + query + "'");
                    svg = pussycatSession.performRendering(query, renderletNexus);
                }
                catch (PussycatSessionNotReadyException e) {
                    getLog().error("Pussycat session not ready yet - waiting to generate SVG for '" + query + "'");
                    nexusReady = false;
                    synchronized (this) {
                        try {
                            wait(30000);
                        }
                        catch (InterruptedException e1) {
                            // do nothing
                        }
                    }
                    getLog().info("Retrying query for '" + query + "'");
                }
            } while (!nexusReady);
            allSVG.put(query, svg);
            bm_log.info(DateTimeStamp.getCurrentTimeStamp() + " Successfully acquired SVG");
        }

        bm_log.info(DateTimeStamp.getCurrentTimeStamp() + " " + allSVG.size() + " sets of SVG acquired");
        bm_log.info("Benchmarking complete!");
    }

    public void setRenderletNexus() {
        getLog().info("Acquiring renderlets");
        Collection<Renderlet> renderlets = pussycatSession.getAvailableRenderlets();
        getLog().info("There are " + renderlets.size() + " renderlets");

        try {
            renderletNexus = pussycatManager.createRenderletNexus(config, pussycatSession);
        }
        catch (PussycatSessionNotReadyException e) {
            getLog().error("Unable to set renderlet nexus", e);
            throw new RuntimeException(e);
        }

        for (Renderlet r : renderlets) {
            renderletNexus.register(r);
        }
    }

    public OWLClassExpression createClassExpression(String yearMonth) {
        if (yearMonth.equals("all")) {
            return config.getOWLDataFactory().getOWLThing();
        }
        else {
            OWLOntologyManager manager = config.getOWLOntologyManager();
            OWLDataFactory df = config.getOWLDataFactory();

            List<OWLAnnotationProperty> properties = Arrays.asList(df.getRDFSLabel());
            AnnotationValueShortFormProvider annoSFP = new AnnotationValueShortFormProvider(
                    properties, new HashMap<OWLAnnotationProperty, List<String>>(), manager);
            ShortFormEntityChecker checker = new ShortFormEntityChecker(
                    new BidirectionalShortFormProviderAdapter(manager, manager.getOntologies(), annoSFP));
            ManchesterOWLSyntaxClassExpressionParser parser = new ManchesterOWLSyntaxClassExpressionParser(df, checker);

            String date =
                    "has_publication_date some dateTime[< \"" + yearMonth + "-01T00:00:00+00:00\"^^dateTime]";
            try {
                getLog().debug("Attempting to parse date expression\n\t'" + date + "'");
                OWLClassExpression dateExpression = parser.parse(date);

                OWLClass study = df.getOWLClass(IRI.create(OntologyConstants.STUDY_CLASS_IRI));
                OWLClassExpression studies = df.getOWLObjectIntersectionOf(study, dateExpression);

                OWLObjectProperty part_of = df.getOWLObjectProperty(IRI.create(OntologyConstants.PART_OF_PROPERTY_IRI));
                OWLObjectSomeValuesFrom part_of_assoc = df.getOWLObjectSomeValuesFrom(part_of, studies);

                OWLClass association = df.getOWLClass(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));
                OWLClassExpression trait_associations = df.getOWLObjectIntersectionOf(association, part_of_assoc);

                getLog().debug("Query put together succesfully");

                return trait_associations;
            }
            catch (ParserException e) {
                getLog().error("Bad date " + yearMonth, e);
                throw new RuntimeException("Bad date " + yearMonth, e);
            }
        }
    }
}


