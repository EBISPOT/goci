package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.FilterAssociation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Dani on 17/05/16.
 */
@Service
public class FilterDataProcessingService {

    private Integer rs_id;
    private Integer bp_location;
    private Integer chromosome;
    private Integer pvalue;
    private Integer ld_block = null;
    private List<Integer> other;
    private String[] headers;


    @Autowired
    private FilteringService filteringService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public String[][] filterInputData(String[][] data, Boolean pruneOutput){
        headers = data[0];

        processHeaders();
        List<FilterAssociation> associations = processInputData(data);

        getLog().info("Starting sorting by chromosome");
        Map<String, List<FilterAssociation>> byChrom = filteringService.groupByChromosomeName(associations);

        getLog().info("Sorting by chromosome done");

        getLog().info("Starting sorting by bp location");

        Map<String, List<FilterAssociation>> byLoc = filteringService.sortByBPLocation(byChrom);

        getLog().info("Sorting by bp location done");


        getLog().info("Starting filtering process");

        List<FilterAssociation> filtered = filteringService.filterTopAssociations(byLoc);
        getLog().info("Filtering process complete");

        String[][] output = transformAssociations(filtered, pruneOutput);
        return output;
    }

    public void processHeaders(){
        other = new ArrayList();
        for(int h=0; h < headers.length; h++){
            switch (headers[h].toLowerCase()){
                case "rs_id" :
                    rs_id = h;
                    break;
                case "pvalue":
                    pvalue = h;
                    break;
                case "chromosome":
                    chromosome = h;
                    break;
                case "bp_location":
                    bp_location = h;
                    break;
                case "ld_block":
                    ld_block = h;
                    break;
                default:
                    other.add(h);
                    break;
            }
        }
    }


    public List<FilterAssociation> processInputData(String[][] data) {
        List<FilterAssociation> associations = new ArrayList<>();

        int entries = data.length-1;
        System.out.println("About to process " + entries + " entries");

        for(int i = 1; i < data.length; i++){

            if((i*100)%data.length == 0) {
                System.out.println((i*100)/data.length + " % done");
            }

            String strongestAllele = data[i][rs_id];
            String chromosomeName = data[i][chromosome];
            String chromosomePosition  = data[i][bp_location];

            String pval = data[i][pvalue].toLowerCase();

            Double pvalueFull = Double.parseDouble(pval);

            Double pvalueMantissa;
            Integer pvalueExponent;

            if(!pval.contains("e")){
                double m = pvalueFull;
                int e = 0;

                while(m < 1){
                    e--;
                    m = m*10;
                }
                pvalueMantissa = m;
                pvalueExponent = e;
            }
            else {
                String[] p = pval.split("e");
                pvalueMantissa = Double.parseDouble(p[0]);
                pvalueExponent = Integer.parseInt(p[1]);
            }

            FilterAssociation fa = new FilterAssociation(strongestAllele, pvalueMantissa, pvalueExponent, chromosomeName, chromosomePosition);

            if(pvalueExponent < -323){
                fa.setPrecisionConcern(true);
            }

            if(ld_block != null){
                fa.setLdBlock(data[i][ld_block]);
            }

            fa.setPvalue(pvalueFull);

            List<String> otherVals = new ArrayList<>();

            for(int o : other){
                otherVals.add(data[i][o]);
            }
            
            fa.setOtherInformation(otherVals);

            associations.add(fa);
        }
        return associations;
    }


    public String[][] transformAssociations(List<FilterAssociation> filtered, Boolean pruneOutput) {
        List<String[]> lines = new ArrayList<>();

        String[] newHeaders = new String[headers.length+1];

        for(int i=0; i< headers.length; i++){
            newHeaders[i] = headers[i];
        }
        newHeaders[headers.length] = "isTopAssociation";
        lines.add(newHeaders);

        System.out.println("About to prepare " + filtered.size() + " entries for export");


        for(FilterAssociation f : filtered){
            if((filtered.indexOf(f)*100)%filtered.size() == 0.0) {
                System.out.println((filtered.indexOf(f)*100)/filtered.size() + " % done");
            }

            if(!pruneOutput || (pruneOutput && f.getPvalueExponent() < -5)) {
                String[] line = new String[headers.length + 1];

                line[rs_id] = f.getStrongestAllele();
                line[chromosome] = f.getChromosomeName();
                line[bp_location] = f.getChromosomePosition().toString();

                if(f.getIsAmbigious() && f.getIsTopAssociation()) {
                    line[headers.length] = "REQUIRES REVIEW";
                }
                else {
                    line[headers.length] = f.getIsTopAssociation().toString();
                }

                if (f.getPrecisionConcern()) {
                    String m = f.getPvalueMantissa().toString();
                    String e = f.getPvalueExponent().toString();
                    line[pvalue] = m + "e" + e;
                }
                else {
                    line[pvalue] = String.valueOf(f.getPvalue());
                }

                for (int o : other) {
                    line[o] = f.getOtherInformation().get(other.indexOf(o));
                }

                if (ld_block != null) {
                    line[ld_block] = f.getLdBlock();
                }

                lines.add(line);
            }
        }
        return lines.toArray(new String[lines.size()][]);
    }
}
