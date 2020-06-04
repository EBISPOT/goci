package uk.ac.ebi.spot.goci;

import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import uk.ac.ebi.spot.goci.ontology.owl.ReasonedOntologyLoader;
import uk.ac.ebi.spot.goci.service.SolrIndexer;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
public class SolrIndexerApplication implements CommandLineRunner {
    @Autowired SolrIndexer solrIndexer;
    @Value("${solr_index.all_threads:false}")
    private boolean allThreads = false;

    @Autowired
    private ReasonedOntologyLoader reasonedOntologyLoader;

    // list of publications to load
    private static String [] pmids = {};

    public static void main(String[] args) {
        System.out.println("Starting Solr indexing application...");
        ApplicationContext ctx = new SpringApplicationBuilder(SolrIndexerApplication.class).web(false).run(args);
        //ApplicationContext ctx = SpringApplicationBuilder.run(SolrIndexerApplication.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }

    @Override
    public void run(String... args) throws Exception {

            // parse any command line arguments
            int parseArgs = parseArguments(args);
            //reasonedOntologyLoader.init();
            //reasonedOntologyLoader.waitUntilReady();

            long start_time = System.currentTimeMillis();
            System.out.println("Building indexes with supplied params: " + Arrays.toString(args));
            solrIndexer.enableSysOutLogging();
            System.out.print("Converting all GWAS database objects...");

            int docCount = 0;
            if (pmids.length > 0) {
                System.out.print("Loading selected publications..." + Arrays.toString(pmids));
                //docCount = solrIndexer.fetchAndIndexPublications(Arrays.asList(pmids));
                docCount = solrIndexer.fetchAndIndexAllThreads(Arrays.asList(pmids));
            }
            else {
                System.out.print("Converting all GWAS database objects...");
                if(allThreads) {
                    docCount = solrIndexer.fetchAndIndexAllThreads();
                }else{
                    docCount = solrIndexer.fetchAndIndex();
                }
            }
            System.out.println("done!\n");
            long end_time = System.currentTimeMillis();
            String time = String.format("%.1f", ((double) (end_time - start_time)) / 1000);
            System.out.println("Successfully mapped " + docCount + " documents into the GWAS solr index\n");
            System.out.println("Indexing building complete in " + time + " s. - application will now exit");
    }


    private static int parseArguments(String[] args) {

        CommandLineParser parser = new GnuParser();
        HelpFormatter help = new HelpFormatter();
        Options options = bindOptions();

        int parseArgs = 0;
        try {
            CommandLine cl = parser.parse(options, args, true);

            // check for mode help option
            if (cl.hasOption("") || cl.hasOption("h")) {
                // print out mode help
                help.printHelp("build-solr-index.sh", options, true);
                parseArgs += 1;
            }
            else {
                // find -p option to see if we are to force load publications
                if (cl.hasOption("p") ) {
                    pmids = cl.getOptionValues("p");
                    parseArgs +=1;
                }
            }

        }
        catch (ParseException e) {
            System.err.println("Failed to read supplied arguments");
            help.printHelp("publish", options, true);
            parseArgs += 1;
        }
        return parseArgs;
    }

    private static Options bindOptions() {
        Options options = new Options();

        // help
        Option helpOption = new Option("h", "help", false, "Print the help");
        options.addOption(helpOption);

        // force update
        Option force = new Option("p", "pmid", true,
                "List the publication to force update");
        force.setRequired(false);
        force.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(force);

        return options;
    }


}
