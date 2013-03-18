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
    private String efoTrait, efoUri, chromBand;

    public TraitSummary(){
        this.efoTrait = null;
        this.efoUri = null;
        this.chromBand = null;
        summary = new ArrayList<SNPSummary>();
    }

    public void setEfoTrait(String efoTrait){
        this.efoTrait = efoTrait;
    }

    public String getEfoTrait(){
        return efoTrait;
    }

    public void setEfoUri(String efoUri){
        this.efoUri = efoUri;
    }

    public String getEfoUri(){
        return efoUri;
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

    public void addSNP(String study, String author, String date, String snp, String pval, String gwastrait){
        SNPSummary snpSummary = new SNPSummary(study, author, date, snp,pval,gwastrait);
        summary.add(snpSummary);
    }

    public class SNPSummary{
        private String study, author, date, snp, pval, gwastrait;

        public SNPSummary(String study, String author, String date, String snp, String pval, String gwastrait){
            this.study = study;
            this.author = author;
            this.date = date;
            this.snp = snp;
            this.pval = pval;
            this.gwastrait = gwastrait;
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

//        public String getEfouri(){
//            return efouri;
//        }

        public String getAuthor(){
            return author;
        }

        public String getDate(){
            return date;
        }
    }
}
