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
import uk.ac.ebi.spot.goci.repository.CatalogExportRepository;
import uk.ac.ebi.spot.goci.repository.CatalogImportRepository;
import uk.ac.ebi.spot.goci.repository.CatalogMetaDataRepository;
import uk.ac.ebi.spot.goci.service.SpreadsheetProcessor;

import java.io.File;
import java.io.IOException;

/**
 * Created by emma on 17/02/15.
 *
 * @author emma
 *         <p>
 *         Application to create a text file that can be sent to NCBI pipeline. Takes a file name as an arguement
 */
@SpringBootApplication
public class ImportExportApplication {
    @Autowired
    private CatalogExportRepository catalogExportRepository;
    @Autowired
    private CatalogImportRepository catalogImportRepository;
    @Autowired
    private CatalogMetaDataRepository catalogMetaDataRepository;
    @Autowired
    private SpreadsheetProcessor spreadsheetProcessor;

    private OperationMode opMode;
    private File outputFile;
    private File inputFile;

    private static int exitCode;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public static void main(String... args) {
        System.out.println("Starting catalog I/O service...");
        ApplicationContext ctx = SpringApplication.run(ImportExportApplication.class, args);
        SpringApplication.exit(ctx, () -> exitCode);
    }

    @Bean CommandLineRunner run() {
        return strings -> {
            bindOptions();
            exitCode = parseArguments(strings);

            if (exitCode == 0) {
                switch (opMode) {
                    case NCBI:
                        try {
                            doNCBIExport(outputFile);
                        }
                        catch (Exception e) {
                            System.err.println("NCBI export failed (" + e.getMessage() + ")");
                            getLog().error("NCBI export failed", e);
                            exitCode += 2;
                        }
                        break;
                    case DOWNLOAD:
                        try {
                            doDownloadExport(outputFile);
                        }
                        catch (Exception e) {
                            System.err.println("Download export failed (" + e.getMessage() + ")");
                            getLog().error("Download export failed", e);
                            exitCode += 3;
                        }
                        break;
                    case DOWNLOAD_ALTERNATIVE:
                        try {
                            doAlternativeDownloadExport(outputFile);
                        }
                        catch (Exception e) {
                            System.err.println("Alternative download export failed (" + e.getMessage() + ")");
                            getLog().error("Alternative download export failed", e);
                            exitCode += 5;
                        }
                        break;
                    case STATS:
                        try {
                            doStatsExport(inputFile);
                        }
                        catch (Exception e) {
                            System.err.println("Stats export failed (" + e.getMessage() + ")");
                            getLog().error("Stats export failed", e);
                            exitCode += 6;
                        }
                        break;
                    case LOAD:
                        try {
                            doLoad(inputFile);
                        }
                        catch (Exception e) {
                            System.err.println("Loading NCBI data failed (" + e.getMessage() + ")");
                            getLog().error("Loading NCBI data failed", e);
                            exitCode += 4;
                        }
                        break;
                    default:
                        System.err.println("No operation mode specified");
                        exitCode += 1;
                }
            }
        };
    }

    void doNCBIExport(File outFile) throws IOException {
        String[][] data = catalogExportRepository.getNCBISpreadsheet();
        spreadsheetProcessor.writeToFile(data, outFile);
    }

    void doDownloadExport(File outFile) throws IOException {
//        String[][] allData = catalogExportRepository.getDownloadSpreadsheet("d");
//
//        String[][] data = new String[allData.length][allData[0].length-2];
//
//        for(int i =0; i< allData.length; i++){
//            for(int j =0; j< allData[0].length-2; j++){
//                data[i][j] = allData[i][j];
//            }
//        }
        String[][] data = catalogExportRepository.getDownloadSpreadsheet("d");
        spreadsheetProcessor.writeToFile(data, outFile);
    }

    void doAlternativeDownloadExport(File outFile) throws IOException {
        String[][] data = catalogExportRepository.getDownloadSpreadsheet("a");
        spreadsheetProcessor.writeToFile(data, outFile);
    }

    void doStatsExport(File statsFile) throws IOException{
         catalogMetaDataRepository.getMetaData(statsFile);
    }


    void doLoad(File inFile) throws IOException {
        String[][] data = spreadsheetProcessor.readFromFile(inFile);
        catalogImportRepository.loadNCBIMappedData(data);
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
                help.printHelp("gwas-catalog-io", options, true);
                parseArgs += 1;
            }
            else {
                System.out.println("Running catalog I/O with the following options...");
                for (Option opt : cl.getOptions()) {
                    System.out.println("\t" + opt.getLongOpt() +
                                               (opt.hasArg() ? ": " + opt.getValue() : "") +
                                               " (" + opt.getArgName() + ")");
                }

                // options are...
                // -n --ncbi        (write out NCBI export file)
                // -d --download    (write out downloads file)
                // -l --load        (load in NCBI mapped file)
                // -f --file        (file to load in)
                // -o --out         (file to write out to)

                // required options
                if (cl.hasOption("n")) {
                    this.opMode = OperationMode.NCBI;
                }
                if (cl.hasOption("d")) {
                    this.opMode = OperationMode.DOWNLOAD;
                }
                if (cl.hasOption("a")) {
                    this.opMode = OperationMode.DOWNLOAD_ALTERNATIVE;
                }
                if (cl.hasOption("l")) {
                    this.opMode = OperationMode.LOAD;
                }
                if (cl.hasOption("s")) {
                    this.opMode = OperationMode.STATS;
                }

                // file options
                if (cl.hasOption("f")) {
                    this.inputFile = new File(cl.getOptionValue("f"));
                }
                if (cl.hasOption("o")) {
                    this.outputFile = new File(cl.getOptionValue("o"));
                }
            }
        }
        catch (ParseException e) {
            System.err.println("Failed to read supplied arguments (" + e.getMessage() + ")");
            help.printHelp("zooma", options, true);
            parseArgs += 1;
        }
        return parseArgs;
    }

    private Options bindOptions() {
        Options options = new Options();

        // help
        Option helpOption = new Option("h", "help", false, "Print the help");
        options.addOption(helpOption);

        // options are...
        // -n --ncbi        (write out NCBI export file)
        // -d --download    (write out downloads file)
        // -l --load        (load in NCBI mapped file)
        // -f --file        (file to load in)
        // -o --out         (file to write out to)
        // -a --download_alt  (write out alternative downloads file)
        // -s --stats       (write out catalog meta data)

        // add input options
        OptionGroup modeGroup = new OptionGroup();
        modeGroup.setRequired(true);

        Option ncbiOption = new Option(
                "n",
                "ncbi",
                false,
                "NCBI - generate an export of the GWAS catalog to send to the NCBI for mapping");
        ncbiOption.setRequired(false);
        modeGroup.addOption(ncbiOption);

        Option downloadOption = new Option(
                "d",
                "download",
                false,
                "Download - generate an export of the GWAS catalog suitable for download");
        downloadOption.setRequired(false);
        modeGroup.addOption(downloadOption);

        Option downloadAltOption = new Option(
                "a",
                "download_alt",
                false,
                "Download alternative - generate an export of the GWAS catalog, including ontology mappings, suitable for download");
        downloadAltOption.setRequired(false);
        modeGroup.addOption(downloadAltOption);

        Option loadOption = new Option(
                "l",
                "load",
                false,
                "Load - take a spreadsheet file mapped by the NCBI and load into the database");
        loadOption.setRequired(false);
        modeGroup.addOption(loadOption);

        Option statsOption = new Option(
                "s",
                "stats",
                false,
                "Stats - generate the meta data for the GWAS Catalog published content");
        statsOption.setRequired(false);
        modeGroup.addOption(statsOption);

        options.addOptionGroup(modeGroup);

        // add input file arguments
        OptionGroup fileGroup = new OptionGroup();
        fileGroup.setRequired(true);

        Option inOption = new Option(
                "f",
                "file",
                true,
                "Input file - file where the NCBI mapped data can be found");
        inOption.setArgName("file");
        inOption.setRequired(false);
        fileGroup.addOption(inOption);

        // add output file arguments
        Option outOption = new Option(
                "o",
                "out",
                true,
                "Output file - file to write the chosen data export to");
        outOption.setArgName("file");
        outOption.setRequired(false);
        fileGroup.addOption(outOption);

        options.addOptionGroup(fileGroup);

        return options;
    }

    private enum OperationMode {
        NCBI,
        DOWNLOAD,
        DOWNLOAD_ALTERNATIVE,
        STATS,
        LOAD
    }
}