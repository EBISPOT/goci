package uk.ac.ebi.fgpt.goci.pussycat.model;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 26/09/12
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
public class TraitSummary {

    private ArrayList<SNPSummary> summary;
    private String chromBand;

    public TraitSummary(){
         this.chromBand = null;
        summary = new ArrayList<SNPSummary>();
    }


    public void setChromBand(String chromBand){
        this.chromBand = chromBand;
    }

    public String getChromBand(){
        return chromBand;
    }


    public ArrayList<SNPSummary> getSNPSummaries(){
        return summary;
    }

    public void addSNP(String study, String author, String date, String snp, String pval, String gwastrait, String efotrait, String efouri){
        SNPSummary snpSummary = new SNPSummary(study, author, date, snp,pval,gwastrait, efotrait, efouri);
        summary.add(snpSummary);
    }

    public class SNPSummary{
        private String study, author, date, snp, pval, gwastrait, efotrait, efouri;

        public SNPSummary(String study, String author, String date, String snp, String pval, String gwastrait, String efotrait, String efouri){
            this.study = study;
            this.author = author;
            this.date = date;
            this.snp = snp;
            this.pval = pval;
            this.gwastrait = gwastrait;
            this.efotrait = efotrait;
            this.efouri = efouri;
        }

        public String getStudy(){
            return study;
        }

        public String getSNP(){
            return snp;
        }

        public String getPval(){
            return pval;
        }

        public String getGwastrait(){
            return gwastrait;
        }

        public String getAuthor(){
            return author;
        }

        public String getDate(){
            return date;
        }


        public String getEfoTrait(){
            return efotrait;
        }


        public String getEfoUri(){
            return efouri;
        }
    }
}
