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
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.model.ValidationSummary;
import uk.ac.ebi.spot.goci.service.AssociationFileUploadService;
import uk.ac.ebi.spot.goci.service.ValidationLogService;

import java.io.File;
import java.io.FileNotFoundException;

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

    @Autowired
    private ValidationLogService validationLogService;

    private File inputFile;

    private File outputFile;

    private OperationMode opMode;

    private String validationLevel;

    private static int exitCode;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public static void main(String... args) {
        System.out.println("Starting association validator service...");
        SpringApplication app = new SpringApplication(ValidatorApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
        System.exit(exitCode);
    }

    @Bean CommandLineRunner run() {
        return strings -> {
            bindOptions();
            exitCode = parseArguments(strings);

            if (exitCode == 0) {
                switch (opMode) {
                    case RUN:
                        try {
                            getLog().info("Beginning validation");
                            runUpload(inputFile, validationLevel);
                        }
                        catch (Exception e) {
                            System.err.println("Validation failed (" + e.getMessage() + ")");
                            getLog().error("Validation failed", e);
                            exitCode += 2;
                        }
                        break;
                    default:
                        System.err.println("No operation mode supplied");
                        exitCode += 1;
                }
            }
        };
    }

    private void runUpload(File file, String validationLevel) throws FileNotFoundException {

        ValidationSummary validationSummary =
                associationFileUploadService.processAndValidateAssociationFile(file, validationLevel);
        validationLogService.processErrors(inputFile, validationSummary);
    }

    private Options bindOptions() {
        Options options = new Options();

        // help
        Option helpOption = new Option("h",
                                       "help",
                                       false,
                                       "Use -a run author submission validation of your file, use -c to run curation level validation");
        options.addOption(helpOption);

        // options are...
        OptionGroup modeGroup = new OptionGroup();
        modeGroup.setRequired(true);

        OptionGroup validationGroup = new OptionGroup();
        modeGroup.setRequired(true);
        Option lightOption = new Option(
                "a",
                "author checking",
                false,
                "Runs light checking over the uploaded association file, used mainly for author submission spreadsheets");

        lightOption.setRequired(false);
        validationGroup.addOption(lightOption);
        options.addOptionGroup(validationGroup);

        Option fullOption = new Option(
                "c",
                "curator checking",
                false,
                "Runs full checking over the uploaded association file, used mainly for curator submission spreadsheets");

        fullOption.setRequired(false);
        validationGroup.addOption(fullOption);
        options.addOptionGroup(validationGroup);

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

                // file options
                if (cl.hasOption("f")) {
                    this.inputFile = new File(cl.getOptionValue("f"));
                }

                // set validation level
                if (cl.hasOption("a")) {
                    this.validationLevel = "author";
                    this.opMode = OperationMode.RUN;
                }

                if (cl.hasOption("c")) {
                    this.validationLevel = "full";
                    this.opMode = OperationMode.RUN;
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