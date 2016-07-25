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
            System.out.println("Processing chromosome " + chromName + " with " + associations.size() + " associations");

            if(associations.size() == 1 && associations.get(0).getPvalueExponent() < -5){
                associations.get(0).setIsTopAssociation(true);
            }
            else {
                int i =0;

                while(i < associations.size()) {

                    FilterAssociation current = associations.get(i);

                    if(current.getPvalueExponent() < -5) {
                        List<FilterAssociation> ldBlock = new ArrayList<>();

                        if(current.getLdBlock() != null){
                            boolean end = false;
                            ldBlock.add(current);

                            while (!end) {
                                if (i == associations.size() - 1) {
                                    end = true;
                                }
                                else {
                                    FilterAssociation b = associations.get(i + 1);

                                    if (current.getLdBlock().equals(b.getLdBlock())) {
                                        ldBlock.add(b);
                                        i++;
                                    }
                                    else {
                                        end = true;
                                    }
                                }
                            }

                        }

                        else {
                            Integer distToPrev = null;
                            if (i > 0) {
                                distToPrev = current.getChromosomePosition() -
                                        associations.get(i - 1).getChromosomePosition();
                            }

                            Integer distToNext = null;
                            if (i < associations.size() - 1) {
                                distToNext = associations.get(i + 1).getChromosomePosition() -
                                        current.getChromosomePosition();
                            }


                            if (distToPrev != null && distToNext != null && distToPrev > 100000 &&
                                    distToNext > 100000) {
                                current.setIsTopAssociation(true);
                            }
                            else if (distToPrev == null && distToNext != null && distToNext > 100000) {
                                current.setIsTopAssociation(true);
                            }
                            else if (distToPrev != null && distToNext == null && distToPrev > 100000) {
                                current.setIsTopAssociation(true);
                            }
                            else if (distToPrev != null && distToPrev < 100000 &&
                                    !(associations.get(i - 1).getPvalueExponent() < -5)
                                    && ((distToNext != null && distToNext > 100000) || distToNext == null)) {
                                current.setIsTopAssociation(true);
                            }
                            else if (distToNext != null && distToNext < 100000) {
                                int j = i;
                                boolean end = false;

                                ldBlock.add(current);

                                while (!end) {
                                    FilterAssociation a = associations.get(j);
                                    FilterAssociation b = associations.get(j + 1);


                                    Integer dist = b.getChromosomePosition() - a.getChromosomePosition();

                                    if (dist < 100000) {
                                        ldBlock.add(b);
                                        j++;
                                    }
                                    else {
                                        end = true;
                                    }

                                    if (j == associations.size() - 1) {
                                        end = true;
                                    }

                                }
                                i = j;
                            }
                        }

                        if(ldBlock.size() != 0) {

                            int min = ldBlock.get(0).getChromosomePosition();
                            int maxDist = ldBlock.get(ldBlock.size()-1).getChromosomePosition() - min;

                            if(maxDist > 100000){
                                setSecondaryBlocks(ldBlock);
                            }
                            else {
                                setMostSignificant(ldBlock);
                            }
                        }
                    }
                    i++;
                }
            }
            filtered.addAll(associations);
        });
        return filtered;
    }

    public void setMostSignificant(List<FilterAssociation> ldBlock){
        FilterAssociation mostSignificant;
        List<FilterAssociation> secondary = new ArrayList<>();
        if (ldBlock.size() > 1) {
            List<FilterAssociation> byPval = ldBlock.stream()
                    .sorted((fa1, fa2) -> Double.compare(fa1.getPvalue(),
                                                         fa2.getPvalue()))
                    .collect(Collectors.toList());

            mostSignificant = byPval.get(0);

            if (mostSignificant.getPrecisionConcern()) {
                for (int k = 1; k < byPval.size(); k++) {
                    FilterAssociation fa = byPval.get(k);
                    if (fa.getPvalueExponent() == mostSignificant.getPvalueExponent()) {
                        if (fa.getPvalueMantissa() < mostSignificant.getPvalueMantissa()) {
                            mostSignificant = fa;
                        }
                        else if (fa.getPvalueMantissa() ==
                                mostSignificant.getPvalueMantissa()) {
                            secondary.add(fa);
                        }
                    }
                    else {
                        break;
                    }
                }
            }
            else {
                boolean done = false;
                int p = 0;
                while(!done && p < byPval.size()-1) {
                    if (byPval.get(p).getPvalue() == byPval.get(p+1).getPvalue()) {
                        secondary.add(byPval.get(p+1));
                        p++;
                    }
                    else{
                        done = true;
                    }
                }
            }
        }
        else {
            mostSignificant = ldBlock.get(0);
        }
        if (mostSignificant.getPvalueExponent() < -5) {
            mostSignificant.setIsTopAssociation(true);
        }
        //account for the case where multiple p-values within the same LD block are identical
        if (secondary.size() != 0){
            mostSignificant.setIsAmbigious(true);
            for(FilterAssociation s : secondary){
                if(s.getPvalue() == mostSignificant.getPvalue()){
                    s.setIsTopAssociation(true);
                    s.setIsAmbigious(true);
                }
            }
        }
    }


    public void setSecondaryBlocks(List<FilterAssociation> ldBlock){

        List<List<FilterAssociation>> secondaryBlocks = new ArrayList<>();

        List<FilterAssociation> mostSign = new ArrayList<>();

        Integer index = 0;
        boolean done = false;

        while(!done){
            int min = ldBlock.get(index).getChromosomePosition();
            ArrayList<FilterAssociation> block = new ArrayList<FilterAssociation>();

            block.add(ldBlock.get(index));
            boolean next = false;
            int q = index+1;
            while(!next && q < ldBlock.size()) {

                int max = ldBlock.get(q).getChromosomePosition();

                if (max - min < 100000) {
                    block.add(ldBlock.get(q));
                    q++;
                }
                else {
                    next = true;
                }
            }

            setMostSignificant(block);

            FilterAssociation msib = findMostSignificantInBlock(block);

            int i = ldBlock.indexOf(msib);
            mostSign.add(msib);

            secondaryBlocks.add(block);

            if(i == index){
                index = i + block.size();
            }
            else {
                index = i;
            }

            if(q == ldBlock.size()){
                done = true;
            }
        }

        int p = 0;

        while (p < mostSign.size()-1){
            if(mostSign.get(p+1).getChromosomePosition() - mostSign.get(p).getChromosomePosition() < 100000){
                if(!mostSign.get(p).equals(mostSign.get(p+1))) {
                    if (mostSign.get(p).getPvalue() > mostSign.get(p + 1).getPvalue() &&
                            !mostSign.get(p).getIsAmbigious()) {
                        mostSign.get(p).setIsTopAssociation(false);
                        p++;
                    }
                    else if (mostSign.get(p).getPvalue() > mostSign.get(p + 1).getPvalue() &&
                            mostSign.get(p).getIsAmbigious()) {
                        FilterAssociation notSign = mostSign.get(p);
                        notSign.setIsTopAssociation(false);
                        notSign.setIsAmbigious(false);

                        for(List<FilterAssociation> block : secondaryBlocks){
                            if(block.contains(notSign)){
                                for(FilterAssociation s : block){
                                    s.setIsTopAssociation(false);
                                    s.setIsAmbigious(false);
                                }
                            }
                        }
                        p++;

                    }
                    else if (mostSign.get(p).getPvalue() < mostSign.get(p + 1).getPvalue() &&
                            !mostSign.get(p + 1).getIsAmbigious()) {
                        mostSign.get(p + 1).setIsTopAssociation(false);
                        p = p+2;
                    }
                    else {
                        FilterAssociation notSign = mostSign.get(p+1);
                        notSign.setIsTopAssociation(false);
                        notSign.setIsAmbigious(false);

                        for(List<FilterAssociation> block : secondaryBlocks){
                            if(block.contains(notSign)){
                                for(FilterAssociation s : block){
                                    s.setIsTopAssociation(false);
                                    s.setIsAmbigious(false);
                                }
                            }
                        }
                        p = p+2;
                    }
                }
                else {
                    p++;
                }
            }
            else {
                p++;
            }
        }


    }

    public FilterAssociation findMostSignificantInBlock(ArrayList<FilterAssociation> block){

        List<FilterAssociation> byPval = block.stream()
                .sorted((fa1, fa2) -> Double.compare(fa1.getPvalue(),
                                                     fa2.getPvalue()))
                .collect(Collectors.toList());

        FilterAssociation ms = null;
        for(FilterAssociation fa : byPval){
            if(fa.getIsTopAssociation()){
                ms  = fa;
                break;
            }
        }

        return ms;
    }
}