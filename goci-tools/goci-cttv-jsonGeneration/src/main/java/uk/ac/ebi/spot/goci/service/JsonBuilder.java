package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by catherineleroy on 17/02/2016.
 */
@Service
public class JsonBuilder {


    private AssociationService associationService;

    @Autowired
    public JsonBuilder(AssociationService associationService){

        this.associationService = associationService;

    }

    public AssociationService getAssociationService() {
        return associationService;
    }


    public Collection<String> getJsons(String snp2geneMappingFilePath) throws IOException {

        Collection<String> jsons = new ArrayList<>();

        SnpToGeneMapper snpToGeneMapper = new SnpToGeneMapper(snp2geneMappingFilePath);


        Collection<Association> associations = associationService.findPublishedAssociationsBySnpId(Long.parseLong("26816"));
        System.out.println("\n\n\nasso size = " + associations.size());
        for(Association association : associations){
            System.out.println("\n\n" + association.getPvalue());
            Collection<EfoTrait> efoTraits = association.getEfoTraits();
            for(EfoTrait efoTrait : efoTraits) {
                System.out.println("efo trait uri = " + efoTrait.getUri());
                buildJson(association.getPvalue(), efoTrait.getUri() );
            }

        }
//        buildJson();
//        for(Association association : associations){

//        }

        return jsons;
    }

    private String buildJson(double pvalue, String efoTrait){

        String ensemblId = "ENSG00000000971";
        String rsId = "rs380390";
//        String efoTrait = "EFO_0001365";
        int sampleSize = 146;
        int gwasPanelResolution = 103611;
        String pubmedId = "15761122";
//        String pvalue = "4e-8";//"4e-8";


        String dbVersion = getDate();
        String gwasDbId = "http://identifiers.org/gwascatalog";
        String jsonSchemaVersion = "1.2.1";
        String soTerm = "http://purl.obolibrary.org/obo/SO_0001627";

        JsonObject target = Json.createObjectBuilder()
                .add("activity","http://identifiers.org/cttv.activity/predicted_damaging")
                .add("id", Json.createArrayBuilder().add("http://identifiers.org/ensembl/" + ensemblId))
                        .add("target_type", "http://identifiers.org/cttv.target/gene_evidence").build();

        JsonObject variant = Json.createObjectBuilder()
                .add("type", "snp single")
                .add("id", Json.createArrayBuilder().add("http://identifiers.org/dbsnp/" + rsId))
                .build();

        JsonObject disease = Json.createObjectBuilder()
                .add("id", Json.createArrayBuilder().add( efoTrait))
                .build();

        JsonObject uniqueAssociationFields = Json.createObjectBuilder()
                .add("sample_size", Integer.toString(sampleSize))
                .add("gwas_panel_resolution", Integer.toString(gwasPanelResolution))
                        .add("pubmed_refs", "http://europepmc.org/abstract/MED/" + pubmedId)
                        .add("target", "http://identifiers.org/ensembl/" + ensemblId)
                        .add("object", efoTrait)
                .add("variant", "http://identifiers.org/dbsnp/" + rsId)
                        .add("study_name", "cttv009_gwas_catalog")
                        .add("pvalue", "" + pvalue)
                .build();

//        mail gwas.json
//        "provenance_type": {
//            "literature": {
//                "references": [{
//                    "lit_id": "http://europepmc.org/abstract/MED/15761122"
//                }]
//            }
//        },
        JsonObject litId = Json.createObjectBuilder().add("lit_id", "http://europepmc.org/abstract/MED/" + pubmedId).build();
        JsonArray references = Json.createArrayBuilder().add(litId).build();
        JsonObject literature = Json.createObjectBuilder().add("references", references).build();
        JsonObject provenanceType = Json.createObjectBuilder().add("literature",literature).build();

        JsonObject expert = Json.createObjectBuilder()
                .add("status", true)
                .add("statement","Primary submitter of data").build();

        JsonObject dbXref = Json.createObjectBuilder()
                .add("version", dbVersion)
                .add("id",gwasDbId)
                .build();

        JsonObject database = Json.createObjectBuilder()
                .add("version", dbVersion)
                .add("id","GWAS Catalog")
                .add("dbxref",dbXref)
                .build();

        JsonObject variant2diseaseProvenanceType = Json.createObjectBuilder()
                .add("literature",literature)
                .add("expert",expert)
                .add("database", database)
                .build();

        JsonObject method = Json.createObjectBuilder()
                .add("description","pvalue for the snp to disease association.")
                .build();

        JsonObject resourceScore = Json.createObjectBuilder()
                .add("type", "pvalue")
                .add("method",method)
                .add("value",Double.valueOf(pvalue))
                .build();

        JsonArray evidenceCodes = Json.createArrayBuilder()
                .add("http://identifiers.org/eco/GWAS")
                .add("http://purl.obolibrary.org/obo/ECO_0000205")
                .build();

        JsonObject variant2disease = Json.createObjectBuilder()
                .add("gwas_sample_size", sampleSize)
                .add("unique_experiment_reference","http://europepmc.org/abstract/MED/" + pubmedId)
                .add("gwas_panel_resolution", gwasPanelResolution)
                .add("provenance_type",variant2diseaseProvenanceType)
                .add("is_associated", true)
                .add("resource_score",resourceScore)
                .add("evidence_codes",evidenceCodes)
                .add("date_asserted",dbVersion)
                .build();

        JsonObject gene2Variantexpert = Json.createObjectBuilder()
                .add("status", true)
                .add("statement","Primary submitter of data")
                .build();


        JsonObject gene2variantProvenanceType = Json.createObjectBuilder()
                .add("expert", gene2Variantexpert)
                .add("database",database)
                .build();

        JsonArray gene2VariantEvidenceCodes = Json.createArrayBuilder()
                .add("http://purl.obolibrary.org/obo/ECO_0000205")
                .add("http://identifiers.org/eco/cttv_mapping_pipeline")
                .build();

        JsonObject gene2variant = Json.createObjectBuilder()
                .add("provenance_type", gene2variantProvenanceType)
                .add("is_associated", true)
                .add("date_asserted", dbVersion)
                .add("evidence_codes",gene2VariantEvidenceCodes)
                .add("functional_consequence",soTerm)
                .build();

        JsonObject evidence = Json.createObjectBuilder()
                .add("provenance_type", provenanceType)
                .add("variant2disease", variant2disease)
                .add("gene2variant", gene2variant)
                .build();




        JsonObject json = Json.createObjectBuilder()
                .add("target", target)
                .add("access_level", "public")
                .add("sourceID", "gwascatalog")
                .add("variant", variant)
                .add("disease", disease)
                .add("unique_association_fields",uniqueAssociationFields)
                .add("evidence",evidence)
                .add("validated_against_schema_version",jsonSchemaVersion)
                .add("type", "genetic_association")
                .build();
        System.out.println("\n\n" + json.toString() + "\n\n");

        return json.toString();

    }

//    public static void main(String[] args) {
//        Double monDouble = Double.valueOf("4e-8");
//        System.out.println(monDouble);
//        getDate();
//    }


    public  static String getDate(){
        String date = "";
        Calendar c = new GregorianCalendar();

        int dayOfMonth =  c.get(Calendar.DAY_OF_MONTH);
        String dayOfMonthString = "";
        if(dayOfMonth < 10){
            dayOfMonthString = "0" + dayOfMonth;
        }else{
            dayOfMonthString = "" + dayOfMonth;

        }

        int monthNumber = c.get(Calendar.MONTH);
        String monthNumberString;
        if(monthNumber<10){
            monthNumberString = "0" + monthNumber;
        }else{
            monthNumberString = "" + monthNumber;

        }

        int year = c.get(Calendar.YEAR);


        int hour = c.get(Calendar.HOUR);
        String hourString;
        if(hour<10){
            hourString = "0" + hour;
        }else{
            hourString = "" + hour;

        }


        int minute = c.get(Calendar.MINUTE);
        String minuteString;
        if(minute<10){
            minuteString = "0" + minute;
        }else{
            minuteString = "" + minute;
        }

        int seconds = c.get(Calendar.SECOND);
        String secondsString;
        if(seconds<10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;
        }


        date = year + "-" + monthNumberString + "-" + dayOfMonthString + "T"  + hourString + ":" + minuteString + ":" + secondsString + "+00:00";



        return date;
    }








}
