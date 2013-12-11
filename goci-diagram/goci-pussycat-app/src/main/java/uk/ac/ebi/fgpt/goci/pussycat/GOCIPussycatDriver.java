package uk.ac.ebi.fgpt.goci.pussycat;

import org.apache.commons.cli.*;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.manager.PussycatManager;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.session.OntologyLoadingCacheableReasonerSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.ReasonerSession;
import uk.ac.ebi.fgpt.goci.pussycat.utils.DateTimeStamp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

//import uk.ac.ebi.fgpt.goci.pussycat.metrics.DateTimeStamp;


public class GOCIPussycatDriver {

    private static String ontologyInputFile;
    private static String svgOutputLocation;
    private static String efoLocation;

    public static void main(String[] args) {

        try {
            // parse arguments
            int parseArgs = parseArguments(args);
            if (parseArgs == 0) {

                String input = ontologyInputFile;
                File output = new File(svgOutputLocation);
                String efo = efoLocation;

                System.setProperty("goci.ontology.inputPath", input);
                System.setProperty("goci.svg.outputPath", output.getAbsolutePath());
                System.setProperty("efo.inputPath", efo);

                     // backup old SVG directory
                try{
                    if(output.exists()){
                        System.out.println("SVG output directory " + output.getAbsolutePath() + " already exists");
                        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
                        String backupFileName = output.getName().concat(".backup.").concat(dateStr);
                        File backupFile = new File(output.getAbsoluteFile().getParentFile(), backupFileName);

                        Path oldoutput = output.toPath();
                        Path newoutput = backupFile.toPath();
                        if (!Files.exists(newoutput)) {
                            System.out.print(
                                    "Backing up " + oldoutput.toString() + " to " + newoutput.toString() + "...");

                            Files.move(oldoutput,
                                    newoutput,
                                    StandardCopyOption.REPLACE_EXISTING,
                                    StandardCopyOption.ATOMIC_MOVE);
                            System.out.println("ok!");
                        }
                        else {
                            System.out.println("Backup already exists");
                        }    
                    }
                }
                catch (IOException e) {
                    System.err.println("Failed to backup Load failed: " + e.getMessage());
                    e.printStackTrace();
                    System.exit(1);
                }

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

        GOCIPussycatDriver driver = new GOCIPussycatDriver();
        try {
            driver.runPussycat();
        }
        catch (Exception e) {
            System.err.println("Diagram rendering failed: " + e.getMessage());
            e.printStackTrace();
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
                    svgOutputLocation = cl.getOptionValue("o");
                }
                else {
                    svgOutputLocation =  "/ebi/microarray/home/fgpt/goci-home/diagram/cache/diagram-latest/";
                }
                if (cl.hasOption("i")) {
                    ontologyInputFile = cl.getOptionValue("i");
                }
                else{
                    ontologyInputFile = "/ebi/microarray/home/fgpt/goci-home/ontology/gwas-data-latest.owl";
                }
                if(cl.hasOption("e")){
                    efoLocation = cl.getOptionValue("e");
                }
                else{
                    efoLocation = "www.ebi.ac.uk/efo";
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
                "The output directory to write the SVG to");
        outputFileOption.setArgName("file");
        outputFileOption.setRequired(true);
        options.addOption(outputFileOption);

        //add input file arguments
        Option inputFileOption = new Option("i", "inferred", true,
                "The input file to read the ontology from");
        inputFileOption.setArgName("file");
        options.addOption(inputFileOption);

        //add input file arguments
        Option efoFileOption = new Option("e", "efo", true,
                "The location from which to read the Experimental Factor Ontology from");
        efoFileOption.setArgName("file");
        options.addOption(efoFileOption);

        return options;
    }

    private OntologyConfiguration config;
    private ReasonerSession reasonerSession;
    private PussycatSession pussycatSession;
    private PussycatManager pussycatManager;
    private RenderletNexus renderletNexus;


    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public GOCIPussycatDriver() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("goci-pussycat.xml");
        config = ctx.getBean("config", OntologyConfiguration.class);
        reasonerSession = ctx.getBean("reasonerSession", OntologyLoadingCacheableReasonerSession.class);
        pussycatSession = ctx.getBean("pussycatSession", PussycatSession.class);
        pussycatManager = ctx.getBean("pussycatManager", PussycatManager.class);
    }

    public void runPussycat() throws IOException {
        getLog().info("Starting session");

        long start, end;
        start = System.currentTimeMillis();
        while (!reasonerSession.isReasonerInitialized()) {
            try {
                getLog().debug("No reasoner session available, holding");
                synchronized (this) {
                    wait(300000);
                }
            }
            catch (InterruptedException e) {
                // do nothing
            }
        }

        end = System.currentTimeMillis();
        double time = ((double) (end - start)) / 1000;
        log.info("Reasoner session acquired after " + time + " s");

        setRenderletNexus();
        getLog().info("Renderlet nexus set");

        OWLClassExpression query = config.getOWLDataFactory().getOWLThing();

        boolean nexusReady = true;
        do {
            try {
                getLog().debug("Performing SVG rendering for '" + query + "'");
                pussycatSession.performRendering(query, renderletNexus);
                nexusReady = true;
            }
            catch (PussycatSessionNotReadyException e) {
                getLog().debug("Pussycat session not ready yet - waiting to generate SVG for '" + query + "'");
                nexusReady = false;
                synchronized (this) {
                    try {
                        wait(300000);
                    }
                    catch (InterruptedException e1) {
                        // do nothing
                    }
                }
                getLog().debug("Retrying query for '" + query + "'");
            }
        } while (!nexusReady);

        log.info(DateTimeStamp.getCurrentTimeStamp() + " Successfully acquired SVG");

        getLog().info("Diagram generation complete!");
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
}
