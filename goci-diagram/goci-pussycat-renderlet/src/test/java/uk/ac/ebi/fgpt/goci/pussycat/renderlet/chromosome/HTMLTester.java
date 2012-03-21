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
        String filename = "/home/dwelter/karyotype.svg";

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

                String one = "<?xml version=\"1.0\" standalone=\"no\"?>\n" +
                        "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \n" +
                        "  \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
                        "<svg \n" +
                        "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">";
                 writer.write(one);

          /*      String two = "<body> \n";
                writer.write(two);          */
            }

            catch(IOException ex){
                ex.printStackTrace();
            }
        }

        public void closeFile(){
            try{
               String one = "</svg> \n";
                writer.write(one);

      /*          String two = "</html> \n";
                writer.write(two);        */

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
                
                String line = chrom.render(null, null, null);
   //             String line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrTwo();

                line = chrom.render(null, null, null);
   //             line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrThree();
                line = chrom.render(null, null, null);

   //             line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrFour();
                line = chrom.render(null, null, null);

   //             line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrFive();
                line = chrom.render(null, null, null);

   //             line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrSix();
                line = chrom.render(null, null, null);

   //             line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrSeven();
                line = chrom.render(null, null, null);

    //            line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrEight();
                line = chrom.render(null, null, null);

   //             line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrNine();
                line = chrom.render(null, null, null);

 //               line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrTen();
                line = chrom.render(null, null, null);

  //              line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrEleven();
                line = chrom.render(null, null, null);
 //              line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrTwelve();
                line = chrom.render(null, null, null);
 //              line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

      /*          line = "<br> \n";
                writer.write(line);    */

                chrom = new ChrThirteen();
                line = chrom.render(null, null, null);
//               line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrFourteen();
                line = chrom.render(null, null, null);
 //              line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrFifteen();
                line = chrom.render(null, null, null);
 //              line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrSixteen();
                line = chrom.render(null, null, null);
 //              line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrSeventeen();
                line = chrom.render(null, null, null);
 //             line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrEighteen();
                line = chrom.render(null, null, null);
  //            line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrNineteen();
                line = chrom.render(null, null, null);
 //              line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrTwenty();
                line = chrom.render(null, null, null);
 //             line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrTwentyone();
                line = chrom.render(null, null, null);
//               line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrTwentytwo();
                line = chrom.render(null, null, null);
 //               line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrX();
                line = chrom.render(null, null, null);
  //              line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);

                chrom = new ChrY();
                line = chrom.render(null, null, null);
  //              line = "<embed src=\"" + chrom.getSVGFile() + "\" type=\"image/svg+xml\"> \n";
                writer.write(line);
            }

            catch(IOException ex){
                ex.printStackTrace();
            }

        }
    }



}
