package uk.ac.ebi.fgpt.goci;

import org.apache.commons.cli.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.goci.lang.FilterProperties;
import uk.ac.ebi.fgpt.goci.checker.DataProcessor;
import uk.ac.ebi.fgpt.goci.checker.Mapping;

import java.io.*;
import java.util.Collection;


public class GOCIMappingsExtractorDriver
 {
     private static OutputStream _out;

     public static void main( String[] args )
      {
          try {
              int parseArgs = parseArguments(args);
              if(parseArgs == 0){
                  System.out.println("Arguments parsed, data loading in progress");
                  GOCIMappingsExtractorDriver driver = new GOCIMappingsExtractorDriver();
                  driver.generateMappingsFile();

                  if (_out != System.out) {
                      _out.close();
                  }
                  System.out.println("Process complete!");
              }
              else {
                  // could not parse arguments, exit with exit code >1 (depending on parsing problem)
                  System.err.println("Failed to parse supplied arguments");
                  System.exit(1 + parseArgs);
              }

          }
          catch (Exception e) {
              System.err.println("An unexpected error occurred\n\t(" + e.getMessage() + ")");
              e.printStackTrace();
              System.exit(1);
          }
//              catch (IOException e) {
//                  System.err.println("Failed to close output stream - report may not have written correctly");
//                  System.exit(-1);
//              }
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
                 help.printHelp("extract", options, true);
                 parseArgs += 1;
             }
             else {
                 // find -o option (for asserted output file)
                 if (cl.hasOption("o")) {
                     String outOpt = cl.getOptionValue("o");
                     _out = new BufferedOutputStream(new FileOutputStream(new File(outOpt)));


                     if(cl.hasOption("p")){
                         String pvalueFilter = cl.getOptionValue("p");
                         FilterProperties.setPvalueFilter(pvalueFilter);
                     }

                     if(cl.hasOption("d")) {
                         String dateFilter = cl.getOptionValue("d");
                         FilterProperties.setDateFilter(dateFilter);
                     }
                 }
                 else {
                     System.err.println("-o (ontology output file) argument is required");
                     help.printHelp("publish", options, true);
                     parseArgs += 2;
                 }
             }
         }
         catch (ParseException e) {
             System.err.println("Failed to read supplied arguments (" + e.getMessage() + ")");
             help.printHelp("evaluate", options, true);
             parseArgs += 4;
         } catch (FileNotFoundException e) {
             System.err.println("Failed to read supplied arguments - file not found (" + e.getMessage() + ")");
             help.printHelp("evaluate", options, true);
             parseArgs += 5;
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
                 "The output file to write the published ontology to");
         outputFileOption.setArgName("file");
         outputFileOption.setRequired(true);
         options.addOption(outputFileOption);

         Option pvalueFilterOption = new Option("p", "pvalue", true, "The minimum p-value on which to filter the knowledge base, in format nE-x, e.g. 5E-8");
         options.addOption(pvalueFilterOption);

         Option dateFilterOption = new Option("d", "date", true, "The date on which to filter the knowledge base, in format YYYY-MM-DD");
         options.addOption(dateFilterOption);


         return options;
     }


     private DataProcessor processor;

     public GOCIMappingsExtractorDriver() {
         ApplicationContext ctx = new ClassPathXmlApplicationContext("goci-mappingsextractor.xml");
         processor = ctx.getBean("processor", DataProcessor.class);
     }


     public void generateMappingsFile(){

         Collection<Mapping> allMappings = processor.processData();
         System.out.println("Writing data to file");
         printToFile(allMappings);

     }

     public void printToFile(Collection<Mapping> allMappings){
         PrintWriter writer = new PrintWriter(_out);

         StringBuilder header = new StringBuilder();
         header.append("DISEASETRAIT \t");
         header.append("EFOTRAIT \t");
         header.append("EFOURI \t");
         header.append("PARENT \t");
         header.append("PUBMEDID \t");
         header.append("AUTHOR \t");
         header.append("PUBDATE \t");
         header.append("JOURNAL");

         writer.println(header.toString());

         for(Mapping mapping : allMappings){
             StringBuilder line = new StringBuilder();

             line.append(mapping.getDiseasetrait());
             line.append("\t");
             line.append(mapping.getEfotrait());
             line.append("\t");
             line.append(mapping.getEfouri());
             line.append("\t");
             line.append(mapping.getParent());
             line.append("\t");
             line.append(mapping.getPmid());
             line.append("\t");
             line.append(mapping.getAuthor());
             line.append("\t");
             line.append(mapping.getDate());
             line.append("\t");
             line.append(mapping.getJournal());

             writer.println(line.toString());
         }

         writer.flush();
     }


 }
