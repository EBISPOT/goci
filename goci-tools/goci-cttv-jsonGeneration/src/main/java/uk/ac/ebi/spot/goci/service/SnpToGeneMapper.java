package uk.ac.ebi.spot.goci.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.service.model.ols.SearchQuery;
import uk.ac.ebi.spot.goci.service.model.SnpInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by catherineleroy on 17/02/2016.
 */
@Service
public class SnpToGeneMapper {

    private Map<String, List<SnpInfo>> snp2snpInfo = new HashMap<>();

    public SnpToGeneMapper(){

    }

    private RestTemplate restTemplate;

    public SnpToGeneMapper(String snp2geneFilePath) throws IOException {

        this.restTemplate = new RestTemplate();

        Map<String, String> soTermToSoTerm = new HashMap<>();
        soTermToSoTerm.put("3_prime_UTR_variant","http://purl.obolibrary.org/obo/SO_0001624");
        soTermToSoTerm.put("5_prime_UTR_variant","http://purl.obolibrary.org/obo/SO_0001623");
        soTermToSoTerm.put("coding_sequence_variant","http://purl.obolibrary.org/obo/SO_0001580");
        soTermToSoTerm.put("downstream_gene_variant","http://purl.obolibrary.org/obo/SO_0001632");
        soTermToSoTerm.put("frameshift_variant","http://purl.obolibrary.org/obo/SO_0001589");
        soTermToSoTerm.put("intron_variant","http://purl.obolibrary.org/obo/SO_0001627");
        soTermToSoTerm.put("missense_variant","http://purl.obolibrary.org/obo/SO_0001583");
        soTermToSoTerm.put("nearest_gene_five_prime_end","http://targetvalidation.org/sequence/nearest_gene_five_prime_end");
        soTermToSoTerm.put("nearest_gene_five_prime_end_reg","http://targetvalidation.org/sequence/regulatory_nearest_gene_five_prime_end");
        soTermToSoTerm.put("splice_acceptor_variant","http://purl.obolibrary.org/obo/SO_0001574");
        soTermToSoTerm.put("splice_donor_variant","http://purl.obolibrary.org/obo/SO_0001575");
        soTermToSoTerm.put("splice_region_variant","http://purl.obolibrary.org/obo/SO_0001630");
        soTermToSoTerm.put("stop_gained","http://purl.obolibrary.org/obo/SO_0001587");
        soTermToSoTerm.put("stop_lost","http://purl.obolibrary.org/obo/SO_0001578");
        soTermToSoTerm.put("synonymous_variant","http://purl.obolibrary.org/obo/SO_0001819");
        soTermToSoTerm.put("upstream_gene_variant","http://purl.obolibrary.org/obo/SO_0001631");
        soTermToSoTerm.put("start_lost","http://purl.obolibrary.org/obo/SO_0002012");
        soTermToSoTerm.put("inframe_deletion","http://purl.obolibrary.org/obo/SO_0001822");
        soTermToSoTerm.put("inframe_insertion","http://purl.obolibrary.org/obo/SO_0001821");


        String line;
        BufferedReader br = new BufferedReader(new FileReader(snp2geneFilePath));

        while ((line = br.readLine()) != null) {

            String[] array = line.split("\t");
            if(!"".equals(array[2]) && array[2] != null) {

                SnpInfo snpInfo = new SnpInfo();

                snpInfo.setRsId(array[0]);

                snpInfo.setIsInEnsmbl(array[1]);

                if(soTermToSoTerm.get(array[4]) == null ){
                    soTermToSoTerm.put(array[4], findNewSoTerm(array[4]));
                }

                snpInfo.setSoTerm(soTermToSoTerm.get(array[4]));

                if (array[2].contains(",")) {
                    String[] ensemblIds = array[2].split(",");
                    List<String> ids = new ArrayList<>();

                    for (String ensemblId : ensemblIds) {
                        ids.add(ensemblId);
                    }
                    snpInfo.setEnsemblIds(ids);
                } else {
                    List<String> ids = new ArrayList<>();
                    ids.add(array[2].toString());

                    snpInfo.setEnsemblIds(ids);
                }

                if (array[3].contains(",")) {
                    String[] ensemblLabels = array[3].split(",");
                    List<String> labels = new ArrayList<>();

                    for (String label : ensemblLabels) {
                        labels.add(label);
                    }
                    snpInfo.setEnsemblName(labels);
                } else {
                    List<String> labels = new ArrayList<>();
                    labels.add(array[3].toString());
                    snpInfo.setEnsemblName(labels);
                }

                snpInfo.setDistance(array[5]);

                snp2snpInfo.computeIfAbsent(snpInfo.getRsId(), k -> new ArrayList<>());

                List<SnpInfo> list = snp2snpInfo.get(snpInfo.getRsId());
                list.add(snpInfo);
            }
        }
    }

    public List<SnpInfo> get(String rsId){
        return snp2snpInfo.get(rsId);
    }


    private String findNewSoTerm(String soTerm) {
        URI uri = null;
        try {
            uri = new URI("https", "www.ebi.ac.uk", "/" + "ols/api/search", "queryFields=label&fieldList=iri&exact=true&ontology=so&q=" + soTerm, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not query OLS: " + e);
        }

        SearchQuery response = this.restTemplate.getForObject(uri, SearchQuery.class);
        if (response.getResponse().getNumFound() != 1){
            throw new RuntimeException("Could not retrive SO Term from OLS: " + soTerm);
        }
        return response.getResponse().getSearchResults()[0].getIri();
    }


}
