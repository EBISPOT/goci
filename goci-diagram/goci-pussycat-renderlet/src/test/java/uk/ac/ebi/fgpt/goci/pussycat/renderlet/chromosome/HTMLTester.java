package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 02/03/12
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class HTMLTester {




    public static void main(String[] args){
        String filename = "/home/dwelter/karyotype2.html";

        new HTMLWriter(filename);

    }

    static class HTMLWriter{

        private FileWriter writer;

        HTMLWriter(String filename) {

            createFile(filename);
            addChromosomes();
            closeFile();
        }


        public void createFile(String filename){
            try{
                writer = new FileWriter(filename);

                System.out.println("Created file " + filename);

                String one = "<html> \n";
                 writer.write(one);

                String two = "<body> \n";
                writer.write(two);
            }

            catch(IOException ex){
                ex.printStackTrace();
            }
        }

        public void closeFile(){
            try{
               String one = "</body> \n";
                writer.write(one);

                String two = "</html> \n";
                writer.write(two);

                writer.close();
                System.out.println("File writer finished");
            }

            catch(IOException ex){
                ex.printStackTrace();
            }
        }

        public void addChromosomes(){

            try{
                ChromosomeRenderlet chrom;

                chrom = new ChrOne();
                
                String line = chrom.render(null, null);
   //             String line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrTwo();

                line = chrom.render(null, null);
   //             line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

     /*           chrom = new ChrThree();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrFour();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrFive();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrSix();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrSeven();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrEight();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrNine();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrTen();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrEleven();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrTwelve();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                line = "<br> \n";
                writer.write(line);

                chrom = new ChrThirteen();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrFourteen();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrFifteen();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrSixteen();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrSeventeen();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrEighteen();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrNineteen();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrTwenty();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrTwentyone();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrTwentytwo();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrX();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrY();
                line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);                                                                  */
            }

            catch(IOException ex){
                ex.printStackTrace();
            }

        }
    }



}
