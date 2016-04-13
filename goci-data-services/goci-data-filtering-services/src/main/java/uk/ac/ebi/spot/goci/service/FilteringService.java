package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.goci.model.FilterAssociation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by dwelter on 05/04/16.
 */
public class FilteringService {

    @Autowired
    private List<FilterAssociation> associations;

    Map<String, List<FilterAssociation>> byChrom;



    @Autowired
    FilteringService(List<FilterAssociation> associations){
        this.associations = associations;
    }


    public void groupByChromosomeName(){

        byChrom =
                associations.stream()
                            .collect(Collectors.groupingBy(FilterAssociation::getChromosomeName));


        for(String k : byChrom.keySet()){
            System.out.print(k + "-");

            for(FilterAssociation a : byChrom.get(k)){
                System.out.print("\t" + a.getRowNumber());
            }
            System.out.print("\n");

        }

    }

    public void sortByBPLocation(){
        byChrom.forEach((k, v) -> {
            List<FilterAssociation> byChromBP = v.stream()
                    .sorted((v1, v2) -> Integer.compare(v1.getChromosomePosition(), v2.getChromosomePosition()))
                    .collect(Collectors.toList());

            byChrom.put(k, byChromBP);
        })  ;

        for(String k : byChrom.keySet()){
            System.out.print(k + "-");

            for(FilterAssociation a : byChrom.get(k)){
                System.out.print("\t" + a.getRowNumber());
            }
            System.out.print("\n");

        }

    }

    public void filterTopAssociations(){

        byChrom.forEach((chromName, associations) -> {

            if(associations.size() == 1 && associations.get(0).getPvalueExponent() < -5){
                associations.get(0).setIsTopAssociation(true);
            }
            else {
                int i =0;

                while(i < associations.size()) {

                    FilterAssociation current = associations.get(i);

                    if(current.getPvalueExponent() < -5) {

                        Integer distToPrev = null;
                        if (i > 0) {
                            distToPrev = current.getChromosomePosition() - associations.get(i - 1).getChromosomePosition();
                        }

                        Integer distToNext = null;
                        if (i < associations.size() - 1) {
                            distToNext = associations.get(i + 1).getChromosomePosition() - current.getChromosomePosition();
                        }


                        if (distToPrev != null && distToNext != null && distToPrev > 100000 && distToNext > 100000) {
                            current.setIsTopAssociation(true);
                        }
                        else if (distToPrev == null && distToNext != null && distToNext > 100000) {
                            current.setIsTopAssociation(true);
                        }
                        else if (distToPrev != null && distToNext == null && distToPrev > 100000) {
                            current.setIsTopAssociation(true);
                        }
                        else if (distToNext != null && distToNext < 100000) {
                            FilterAssociation next = associations.get(i + 1);
                            Integer cpe = current.getPvalueExponent();
                            Integer npe = next.getPvalueExponent();
                            //TO DO: what if two associations in LD have the same p-value???
                            if (cpe == npe) {
                                Integer cpm = current.getPvalueMantissa();
                                Integer npm = next.getPvalueMantissa();

                                if (cpm < npm) {
                                    current.setIsTopAssociation(true);
                                }
                                else {
                                    next.setIsTopAssociation(true);
                                    current.setIsTopAssociation(false);
                                }
                            }
                            else if (cpe < npe) {
                                current.setIsTopAssociation(true);
                            }
                            else {
                                next.setIsTopAssociation(true);
                                current.setIsTopAssociation(false);
                            }
                        }
                    }
                    i++;

                }

            }
        });

        for(String k : byChrom.keySet()){

            System.out.print(k + " top -");

            for(FilterAssociation a : byChrom.get(k)){
                if(a.getIsTopAssociation()) {
                    System.out.print("\t" + a.getRowNumber());
                }
            }
            System.out.print("\n");

            System.out.print(k + " in LD -");

            for(FilterAssociation a : byChrom.get(k)){
                if(!a.getIsTopAssociation()) {
                    System.out.print("\t" + a.getRowNumber());
                }
            }
            System.out.print("\n");

        }


    }
}
