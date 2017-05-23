package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

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

   public Map<String, Map<String, String>> getSnpRsIdList(){
       Map<String, Map<String, String>> snpInfo = new HashMap<>();

       SingleNucleotidePolymorphismService snpService = getSingleNucleotidePolymorphismService();
       List<SingleNucleotidePolymorphism> snps = snpService.deepFindAll();
       for(SingleNucleotidePolymorphism snp : snps){

           if(!snp.getRsId().startsWith("rs")){
               continue;
           }

           Map<String, String> info = new LinkedHashMap<>();

           /*
                Location
            */
           Collection<Location> locations = snp.getLocations();

           if(locations.size() == 0){
               info.put("chrName", "NA");
               info.put("from", "NA");
               info.put("to", "NA");
//               info.put("region", "-1");
           }
           for(Location location : locations){
               info.put("chrName",location.getChromosomeName());
               Integer chromPos = location.getChromosomePosition();
//               if(chromPos.contains(":")){
//                   String from = chromPos.split(":")[0];
//                   String to = chromPos.split(":")[1];
//                   info.put("from", from);
//                   info.put("to", to);
//               } else {
//
//               }
               info.put("from", chromPos.toString());
               info.put("to", chromPos.toString());
//               String regionName = location.getRegion().getName();
//               if(regionName == null){
//                   info.put("region", "-1");
//               } else {
//                   info.put("region", regionName);
//               }
           }

           /*
                Allele
            */
           info.put("ref", "NA");
           Collection<RiskAllele> riskAlleles = snp.getRiskAlleles();
           if (riskAlleles.isEmpty()){
               info.put("allele", "NA");
           } else {
               for(RiskAllele riskAllele : riskAlleles){
                   String name = riskAllele.getRiskAlleleName();
                   if(name.contains("-")){
                       String[] alleleString = name.split("-");
                       String allele = alleleString[alleleString.length -1];
                       if(allele.equals("?")){
                           info.put("allele", "NA");
                       } else {
                           info.put("allele", allele);
                       }
                   } else {
                       info.put("allele", "NA");
                   }
               }
           }

           info.put("strand", "NA");
           info.put("type", "NA");
           info.put("rsid", snp.getRsId());

//           /*
//                NCBI Gene
//            */
//           Collection<GenomicContext> contexts = snp.getGenomicContexts();
//           StringJoiner ncbiGenes = new StringJoiner(",");
//           for(GenomicContext context : contexts){
//               if(context.getSource().toLowerCase().equals("ncbi")){
//                   ncbiGenes.add(context.getId().toString());
//               }
//           }
//           info.put("ncbi", ncbiGenes.toString());
//           if(contexts.size() == 0){
//               info.put("ncbi", "NA");
//           }

           /*
           Functional Class
            */
           String fclass = snp.getFunctionalClass();
           if (fclass != null){
               info.put("fclass", snp.getFunctionalClass());
           } else {
               info.put("fclass", "NA");
           }

           snpInfo.put(snp.getRsId(), info);
       }
       return snpInfo;

   }

}
