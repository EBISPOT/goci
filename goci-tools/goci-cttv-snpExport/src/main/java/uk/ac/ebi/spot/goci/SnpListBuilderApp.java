package uk.ac.ebi.spot.goci;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.service.SnpGetter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by catherineleroy on 16/02/2016.
 */
@SpringBootApplication
public class SnpListBuilderApp {

    @Autowired
    private SnpGetter snpGetter;

    private static String outputFilePath = "";

    @Bean
    CommandLineRunner run() {
        return strings -> {
            long start_time = System.currentTimeMillis();

            int parseArgs = parseArguments(strings);

            if (parseArgs == 0) {
                // execute publisher
                this.getAndSave(outputFilePath);
            } else {
                // could not parse arguments, exit with exit code >1 (depending on parsing problem)
                System.err.println("Failed to parse supplied arguments");
                System.exit(1 + parseArgs);
            }

            long end_time = System.currentTimeMillis();
            String time = String.format("%.1f", ((double) (end_time - start_time)) / 1000);
            System.out.println("Got snp in " + time + " s. - application will now exit");
        };
    }


    public static void main(String[] args) {
        System.out.println("Starting Goci snp list builder...");
        ApplicationContext ctx = SpringApplication.run(SnpListBuilderApp.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
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
                    outputFilePath = cl.getOptionValue("o");
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
            parseArgs += 3;
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
                "The output file to write the snp rsIds to");
        outputFileOption.setArgName("file");
        outputFileOption.setRequired(true);
        options.addOption(outputFileOption);



        return options;
    }


    public void getAndSave(String outputFilePath) throws IOException {
        Collection<String> rsIds = snpGetter.getSnpRsIdList();
        String content = "This is the content to write into file";

//        File file = new File("/Users/catherineleroy/Documents/github_project/goci/goci-tools/goci-cttv-export/target/snp.txt");
        File file = new File(outputFilePath);

        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        for (String rsId : rsIds){
            bw.write(rsId + "\n");
        }

        bw.close();

    }

}
