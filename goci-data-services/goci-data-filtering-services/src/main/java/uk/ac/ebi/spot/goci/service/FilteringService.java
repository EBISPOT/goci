package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.goci.model.FilterAssociation;

import java.util.List;

/**
 * Created by dwelter on 05/04/16.
 */
public class FilteringService {

    @Autowired
    private List<FilterAssociation> associations;



    @Autowired
    FilteringService(List<FilterAssociation> associations){
        this.associations = associations;
    }


    public void sortByChromosomeName(){

        associations.stream()
                    .sorted((a1, a2) -> a1.getChromosomeName().compareTo(a2.getChromosomeName()))
                    .forEach(a -> System.out.println(a));

    }

    public void sortByBPLocation(){

    }

    public void filterTopAssociations(){

    }
}
