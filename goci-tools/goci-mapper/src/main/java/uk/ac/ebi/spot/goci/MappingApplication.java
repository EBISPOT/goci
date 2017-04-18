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
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.service.MapCatalogService;

/**
 * Created by emma on 13/08/2015.
 *
 * @author emma
 *         <p>
 *         Application to map all associations in the GWAS database. Mapping pipeline will map SNPs in database and also
 *         validate the author reported gene linked to that SNP via the associations.
 */
@SpringBootApplication
public class MappingApplication {

    @Autowired
    private MapCatalogService mapCatalogService;

    private String performer;

    private OperationMode opMode;

    private int job;

    private int length;

    private static int exitCode;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public static void main(String... args) {
        String filename = "gwas_mapper_lsf.";
        if ((args != null) && (args.length > 2)) {
            filename = filename.concat(args[2].toString());
        }
        System.setProperty("logfilename", filename);
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
                    case LSF:
                        try {
                            doMappingLSF();
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
        getLog().info("Starting mapping of all associations with performer: " + this.performer);
        try {
            mapCatalogService.mapCatalogContents(this.performer);
            getLog().info("Finished mapping by performer:  " + this.performer);
        }
        catch (EnsemblMappingException e) {
            getLog().error("Mapping failed due to Ensembl API communication issue");
        }

    }

    private void doMappingLSF() {
        getLog().info("Starting mapping of all associations with performer: " + this.performer);
        try {
            mapCatalogService.mapCatalogContentsLSF(this.performer,this.job,this.length);
            getLog().info("Finished mapping by performer:  " + this.performer);
        }
        catch (EnsemblMappingException e) {
            getLog().error("Mapping failed due to Ensembl API communication issue");
        }

    }

    private Options bindOptions() {
        Options options = new Options();

        // help
        Option helpOption = new Option("h", "help", false, "Use -m option to run mapping on contents of GWAS catalog");
        options.addOption(helpOption);

        // options are...
        // -m do mapping
        OptionGroup modeGroup = new OptionGroup();
        modeGroup.setRequired(false);

        Option mappingOption = new Option(
                "m",
                "mapping",
                false,
                "Maps all associations in the GWAS database. Mapping pipeline will map SNPs " +
                        "in database and also validate the author reported gene linked to that SNP via the associations");
        mappingOption.setArgName("performer");
        mappingOption.setRequired(true);
        modeGroup.addOption(mappingOption);
        options.addOptionGroup(modeGroup);


        Option mappingOptionLSF = new Option(
                "l",
                "LSF",
                false,
                "Maps all associations in the GWAS database. Mapping pipeline will map SNPs " +
                        "in database and also validate the author reported gene linked to that SNP via the associations");
        mappingOptionLSF.setArgName("performer");
        mappingOptionLSF.setRequired(true);
        modeGroup.addOption(mappingOptionLSF);
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



                // options: -m do mapping
                if (cl.hasOption("m")) {
                    this.opMode = OperationMode.MAPPING;
                    this.performer = cl.getArgList().get(0).toString();
                    System.out.println("-m automatic_mapping_process");
                }

                if(cl.hasOption("l")){
                    this.opMode = OperationMode.LSF;
                    this.performer = cl.getArgList().get(0).toString();
                    this.job = Integer.valueOf(cl.getArgList().get(1).toString());
                    this.length = Integer.valueOf(cl.getArgList().get(2).toString());


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
        MAPPING,
        LSF
    }

}
