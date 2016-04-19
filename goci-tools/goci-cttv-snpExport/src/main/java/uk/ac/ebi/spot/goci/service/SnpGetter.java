package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by catherineleroy on 16/02/2016.
 */

@Service
public class SnpGetter {

    private SingleNucleotidePolymorphismService singleNucleotidePolymorphismService;

    @Autowired
    public SnpGetter(SingleNucleotidePolymorphismService singleNucleotidePolymorphismService){

        this.singleNucleotidePolymorphismService = singleNucleotidePolymorphismService;

    }

    public SingleNucleotidePolymorphismService getSingleNucleotidePolymorphismService() {
        return singleNucleotidePolymorphismService;
    }

   public Collection<String> getSnpRsIdList(){
       Collection<String> rsIds = new ArrayList<>();
       SingleNucleotidePolymorphismService snpService = getSingleNucleotidePolymorphismService();
       List<SingleNucleotidePolymorphism> snps = snpService.findAll();
       for(SingleNucleotidePolymorphism snp : snps){
           rsIds.add(snp.getRsId());
       }
       return rsIds;

   }

}
