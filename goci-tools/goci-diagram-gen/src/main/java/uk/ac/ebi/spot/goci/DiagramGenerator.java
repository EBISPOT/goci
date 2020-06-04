package uk.ac.ebi.spot.goci;

import org.apache.commons.cli.*;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.pussycat.controller.PussycatGOCIController;

@SpringBootApplication
public class DiagramGenerator implements CommandLineRunner {

    @Autowired
    private PussycatGOCIController controller;
    private String pValueMin;
    private String pValueMax;
    private String dateMin;
    private String dateMax;
    private String outFile;

    public static void main(String[] args) {
        System.out.println("Starting diagram generator...");
        SpringApplicationBuilder builder = new SpringApplicationBuilder(DiagramGenerator.class);
        SpringApplication app = builder.web(false).addCommandLineProperties(true).build(args);
        ApplicationContext ctx = app.run(args);
        System.out.println("Application executed successfully!");
    }

    @Override
    public void run(String... strings) throws Exception {
        bindOptions();
        parseArguments(strings);
        controller.saveAssociationsFile(pValueMin, pValueMax, dateMin, dateMax, outFile);
    }

    @Bean
    public HttpClient httpClient(){
        HttpClient client = HttpClientBuilder.create().disableContentCompression().build();
        return client;
    }
    private Options bindOptions() {
        Options options = new Options();

        // help
        Option helpOption = new Option("h", "help", false, "Use -m option to run mapping on contents of GWAS catalog");
        options.addOption(helpOption);

        // options are...
        Option outputOption = new Option("o", "output", true, "output file ");
        outputOption.setArgName("output");
        outputOption.setRequired(true);
        options.addOption(outputOption);

        Option dateMinOption = new Option("dm", "datemin", true, "start date for generation");
        dateMinOption.setArgName("dateMin");
        dateMinOption.setRequired(false);
        options.addOption(dateMinOption);

        Option dateMaxOption = new Option("dmx", "datemax", true, "end date for generation");
        dateMaxOption.setArgName("dateMax");
        dateMaxOption.setRequired(false);
        options.addOption(dateMaxOption);

        Option pValueMinOption = new Option("pm", "pvaluemin", true, "lower bound p-value");
        pValueMinOption.setArgName("pValueMin");
        pValueMinOption.setRequired(false);
        options.addOption(pValueMinOption);

        Option pValueMaxOption = new Option("pmx", "pvaluemax", true, "upper bound p-value");
        pValueMaxOption.setArgName("pValueMax");
        pValueMaxOption.setRequired(false);
        options.addOption(pValueMaxOption);

        return options;
    }

    private void parseArguments(String[] arguments) {
        CommandLineParser parser = new GnuParser();
        HelpFormatter help = new HelpFormatter();
        Options options = bindOptions();

        try {
            CommandLine cl = parser.parse(options, arguments, true);

            // check for mode help option
            if (cl.hasOption("h")) {
                // print out mode help
                help.printHelp("diagram-gen", options, true);
            } else {
                System.out.println("Running diagram-gen with the following options...");
                for (Option opt : cl.getOptions()) {
                    System.out.println("\t" + opt.getLongOpt() + (opt.hasArg() ? ": " + opt.getValue() : "") + " (" +
                            opt.getArgName() + ")");
                }
                outFile = cl.getOptionValue("o");
                dateMin = cl.getOptionValue("dm");
                dateMax = cl.getOptionValue("dmx");
                pValueMin = cl.getOptionValue("pm");
                pValueMax = cl.getOptionValue("pmx");
            }
        } catch (ParseException e) {
            System.err.println("Failed to read supplied arguments (" + e.getMessage() + ")");
            help.printHelp("diagram-gen", options, true);
        }
    }

}
