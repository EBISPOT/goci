package uk.ac.ebi.spot.goci;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
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
import uk.ac.ebi.spot.goci.service.MappingService;

/**
 * Created by emma on 13/08/2015.
 *
 * @author emma
 *         <p>
 *         Application to map all associations in the GWAS database.
 */
@SpringBootApplication
public class MappingApplication {

    @Autowired
    private MappingService mappingService;

    private OperationMode opMode;

    private static int exitCode;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public static void main(String... args) {
        System.out.println("Starting mapping service...");
        ApplicationContext ctx = SpringApplication.run(MappingApplication.class, args);
        int code = SpringApplication.exit(ctx, () -> exitCode);
        if (code > 0) {
            System.exit(code);
        }
    }


    @Bean CommandLineRunner run() {
        return strings -> {
            bindOptions();
            exitCode = parseArguments(strings);

            if (exitCode == 0) {
                switch (opMode) {
                    case MAPPING:
                        try {
                            doMapping();
                        }
                        catch (Exception e) {
                            System.err.println("Mapping failed(" + e.getMessage() + ")");
                            getLog().error("Mapping failed", e);
                            exitCode += 2;
                        }
                        break;
                    default:
                        System.err.println("No mapping argument supplied");
                        exitCode += 1;
                }
            }
        };
    }

    private void doMapping() {
        mappingService.mapCatalogContents();
    }

    private Options bindOptions() {
        Options options = new Options();

        // help
        Option helpOption = new Option("h", "help", false, "Use -m option to run mapping on contents of GWAS catalog");
        options.addOption(helpOption);

        // options are...
        // -m do mapping
        OptionGroup modeGroup = new OptionGroup();
        modeGroup.setRequired(true);

        Option mappingOption = new Option(
                "m",
                "mapping",
                false,
                "Map current association data in database");
        mappingOption.setRequired(true);
        modeGroup.addOption(mappingOption);
        options.addOptionGroup(modeGroup);

        return options;
    }

    private int parseArguments(String[] arguments) {
        CommandLineParser parser = new GnuParser();
        HelpFormatter help = new HelpFormatter();
        Options options = bindOptions();

        int parseArgs = 0;
        try {
            CommandLine cl = parser.parse(options, arguments, true);

            // check for mode help option
            if (cl.hasOption("")) {
                // print out mode help
                help.printHelp("goci-mapper", options, true);
                parseArgs += 1;
            }
            else {
                System.out.println("Running mapping with the following options...");
                for (Option opt : cl.getOptions()) {
                    System.out.println("\t" + opt.getLongOpt() +
                                               (opt.hasArg() ? ": " + opt.getValue() : "") +
                                               " (" + opt.getArgName() + ")");
                }

                // options are...
                // -m do mapping
                // required options
                if (cl.hasOption("m")) {
                    this.opMode = OperationMode.MAPPING;
                }

            }
        }
        catch (ParseException e) {
            System.err.println("Failed to read supplied arguments (" + e.getMessage() + ")");
            help.printHelp("goci-mapper", options, true);
            parseArgs += 1;
        }
        return parseArgs;
    }

    private enum OperationMode {
        MAPPING
    }

}
