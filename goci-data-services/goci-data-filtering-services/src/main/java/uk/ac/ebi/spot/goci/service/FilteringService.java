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
        Map<String, List<FilterAssociation>> byBPLocation = new HashMap<>();
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
//                            filtered.add(current);
                        }
                        else if (distToPrev == null && distToNext != null && distToNext > 100000) {
                            current.setIsTopAssociation(true);
//                            filtered.add(current);
                        }
                        else if (distToPrev != null && distToNext == null && distToPrev > 100000) {
                            current.setIsTopAssociation(true);
//                            filtered.add(current);
                        }
                        else if (distToPrev != null && distToPrev < 100000 && !(associations.get(i-1).getPvalueExponent() < -5)
                                && ((distToNext != null && distToNext > 100000) || distToNext == null)){
                            current.setIsTopAssociation(true);
                        }
                        else if (distToNext != null && distToNext < 100000) {
                            int j = i;
                            boolean end = false;

                            List<FilterAssociation> ldBlock = new ArrayList<>();
                            ldBlock.add(current);

                            while (!end){
                                FilterAssociation a = associations.get(j);
                                FilterAssociation b = associations.get(j+1);

                                Integer dist = b.getChromosomePosition() - a.getChromosomePosition();

                                if(dist < 100000){
                                    ldBlock.add(b);
                                    j++;
                                }
                                else {
                                    end = true;
                                }

                                if(j == associations.size()-1){
                                    end = true;
                                }
                            }


                            FilterAssociation mostSignificant;
                            if(ldBlock.size() > 1) {
                                List<FilterAssociation> byPval = ldBlock.stream()
                                        .sorted((fa1, fa2) -> Integer.compare(fa1.getPvalueExponent(),
                                                                              fa2.getPvalueExponent()))
                                        .collect(Collectors.toList());


                                mostSignificant = byPval.get(0);

                                for (int k = 1; k < byPval.size(); k++) {
                                    FilterAssociation fa = byPval.get(k);
                                    if (fa.getPvalueExponent() == mostSignificant.getPvalueExponent()) {
                                        if (fa.getPvalueMantissa() < mostSignificant.getPvalueMantissa()) {
                                            mostSignificant = fa;
                                        }
                                    }
                                    else {
                                        break;
                                    }
                                }
                            }
                            else {
                                mostSignificant = ldBlock.get(0);
                            }
                            if(mostSignificant.getPvalueExponent() < -5) {
                                mostSignificant.setIsTopAssociation(true);
                            }

                            i = j;
                        }
                    }
//                    else {
//                        System.out.println(current.getStrongestAllele() + "\t" + current.getPvalueExponent());
//                    }
                    i++;
                }
                filtered.addAll(associations);
            }
        });

        return filtered;

    }


}
