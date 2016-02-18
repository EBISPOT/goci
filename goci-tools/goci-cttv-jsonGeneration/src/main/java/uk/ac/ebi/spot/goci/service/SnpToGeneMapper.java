package uk.ac.ebi.spot.goci.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.service.model.SnpInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by catherineleroy on 17/02/2016.
 */
@Service
public class SnpToGeneMapper {
//    //rs10011926       intron_variant          ENSG00000170522         ELOVL6          0
//    rs10012307       nearest_gene_five_prime_end     ENSG00000189184         PCDH18          -927065
//    rs10012750       nearest_gene_five_prime_end     ENSG00000179059         ZFP42   -301812
//    rs10777332       nearest_gene_five_prime_end     ENSG00000257242,ENSG00000279037,ENSG00000257242,ENSG00000280112,ENSG00000280112,ENSG00000279037         C12orf79,C12orf79,C12orf79,C12orf79,C12orf79,C12o

    private Map<String,SnpInfo> snp2snpInfo = new HashMap<>();

    public SnpToGeneMapper(){

    }

    public SnpToGeneMapper(String snp2geneFilePath) throws IOException {


        String line;

        BufferedReader br = new BufferedReader(new FileReader(snp2geneFilePath));

        while ((line = br.readLine()) != null) {

            String[] array = line.split("\t");

            SnpInfo snpInfo = new SnpInfo();

            snpInfo.setRsId(array[0]);

            snpInfo.setSoTerm(array[1]);

            if(array[2].contains(",")){
                String[] ensemblIds = array[2].split(",");
                List<String> ids = new ArrayList<>();

                for(String ensemblId : ensemblIds){
                    ids.add(ensemblId);
                }
                snpInfo.setEnsemblId(ids);
            }else{
                List<String> ids = new ArrayList<>();
                ids.add(line);
                snpInfo.setEnsemblId(ids);
            }

            if(array[3].contains(",")){
                String[] ensemblLabels = array[3].split(",");
                List<String> labels = new ArrayList<>();

                for(String label : ensemblLabels){
                    labels.add(label);
                }
                snpInfo.setEnsemblName(labels);
            }else{
                List<String> labels = new ArrayList<>();
                labels.add(line);
                snpInfo.setEnsemblName(labels);
            }

            snpInfo.setDistance(array[4]);

            System.out.println(line);
        }
    }

    public SnpInfo get(String rsId){
        return snp2snpInfo.get(rsId);
    }

}
