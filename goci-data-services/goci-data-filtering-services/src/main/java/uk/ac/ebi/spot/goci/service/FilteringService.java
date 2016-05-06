package uk.ac.ebi.spot.goci.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.FilterAssociation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by dwelter on 05/04/16.
 */
@Service
public class FilteringService {


    public Map<String, List<FilterAssociation>> groupByChromosomeName(List<FilterAssociation> associations){

        Map<String, List<FilterAssociation>> byChrom =
                associations.stream()
                            .collect(Collectors.groupingBy(FilterAssociation::getChromosomeName));

        return byChrom;
    }

    public Map<String, List<FilterAssociation>> sortByBPLocation(Map<String, List<FilterAssociation>> byChrom){
        Map<String, List<FilterAssociation>> byBPLocation = new HashMap<String, List<FilterAssociation>>();
        byChrom.forEach((k, v) -> {
            List<FilterAssociation> byChromBP = v.stream()
                    .sorted((v1, v2) -> Integer.compare(v1.getChromosomePosition(), v2.getChromosomePosition()))
                    .collect(Collectors.toList());

            byBPLocation.put(k, byChromBP);
        })  ;


        return byBPLocation;
    }

    public List<FilterAssociation> filterTopAssociations(Map<String, List<FilterAssociation>> byBPLocation){

        List<FilterAssociation> filtered = new ArrayList<>();
        byBPLocation.forEach((chromName, associations) -> {

            if(associations.size() == 1 && associations.get(0).getPvalueExponent() < -5){
                associations.get(0).setIsTopAssociation(true);
                filtered.add(associations.get(0));
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
                    filtered.add(current);

                }

            }
        });

        return filtered;

    }


}
