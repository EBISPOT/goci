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
import uk.ac.ebi.spot.goci.service.AssociationFileUploadService;

import java.io.File;

/**
 * Created by emma on 14/04/2016.
 *
 * @author emma
 *         <p>
 *         Application that can be used to upload a spreadsheet and validate it
 */
@SpringBootApplication
public class ValidatorApplication {

    @Autowired
    private AssociationFileUploadService associationFileUploadService;

    private File inputFile;

    private OperationMode opMode;

    private static int exitCode;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public static void main(String... args) {
        System.out.println("Starting association validator service...");
        ApplicationContext ctx = SpringApplication.run(ValidatorApplication.class, args);
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
                    case RUN:
                        try {
                            runUpload(inputFile);
                        }
                        catch (Exception e) {
                            System.err.println("Validation failed(" + e.getMessage() + ")");
                            getLog().error("Validation failed", e);
                            exitCode += 2;
                        }
                        break;
                    default:
                        System.err.println("No file argument supplied");
                        exitCode += 1;
                }
            }
        };
    }

    private void runUpload(File file) {
        // TODO DECIDE WHAT TO DO WITH ERRORS
        associationFileUploadService.processAssociationFile(file);
    }

    private Options bindOptions() {
        Options options = new Options();

        // help
        Option helpOption = new Option("h", "help", false, "Use -v option to upload and validate your file");
        options.addOption(helpOption);

        // options are...
        // -v do validation
        // -f file to validate
        OptionGroup modeGroup = new OptionGroup();
        modeGroup.setRequired(true);

        Option validatorOptions = new Option(
                "v",
                "validate",
                false,
                "Validate supplied file");

        validatorOptions.setRequired(true);
        modeGroup.addOption(validatorOptions);
        options.addOptionGroup(modeGroup);


        // add input file arguments
        OptionGroup fileGroup = new OptionGroup();
        fileGroup.setRequired(true);

        Option inOption = new Option(
                "f",
                "file",
                true,
                "Input file - file containing association data to be validated");
        inOption.setArgName("file");
        inOption.setRequired(false);
        fileGroup.addOption(inOption);
        options.addOptionGroup(fileGroup);

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
                help.printHelp("goci-association-file-validator", options, true);
                parseArgs += 1;
            }
            else {
                System.out.println("Running association-file-validator with the following options...");
                for (Option opt : cl.getOptions()) {
                    System.out.println("\t" + opt.getLongOpt() +
                                               (opt.hasArg() ? ": " + opt.getValue() : "") +
                                               " (" + opt.getArgName() + ")");
                }

                // required options
                if (cl.hasOption("v")) {
                    this.opMode = OperationMode.RUN;
                }


                // file options
                if (cl.hasOption("f")) {
                    this.inputFile = new File(cl.getOptionValue("f"));
                }
            }
        }
        catch (ParseException e) {
            System.err.println("Failed to read supplied arguments (" + e.getMessage() + ")");
            help.printHelp("goci-association-file-validator", options, true);
            parseArgs += 1;
        }
        return parseArgs;
    }

    private enum OperationMode {
        RUN
    }
}