package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by dwelter on 05/04/16.
 */
public class FilteringService {

    @Autowired
    private List<Object> associations;



    @Autowired
    FilteringService(List<Object> associations){
        this.associations = associations;
    }


    public void findSignificantAssociations(){
//        for(FilterAssociation a : associations){
//
//        }
    }

}
