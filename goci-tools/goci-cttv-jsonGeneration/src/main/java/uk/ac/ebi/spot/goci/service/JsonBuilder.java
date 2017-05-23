package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.goci.service.model.SnpInfo;


import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by catherineleroy on 17/02/2016.
 */
@Service
public class JsonBuilder {


    private AssociationService associationService;

    private AssociationRepository associationRepository;

    @Autowired
    public JsonBuilder(AssociationService associationService, AssociationRepository associationRepository) {

        this.associationService = associationService;
        this.associationRepository = associationRepository;
    }

    public JsonBuilder(){

    }

    public AssociationService getAssociationService() {
        return associationService;
    }

    public AssociationRepository getAssociationRepository() {
        return associationRepository;
    }


    public Collection<String> getJsons(String snp2geneMappingFilePath) throws IOException {

        Collection<String> jsons = new ArrayList<>();

        SnpToGeneMapper snpToGeneMapper = new SnpToGeneMapper(snp2geneMappingFilePath);




        Sort sort = new Sort(new Sort.Order("id"));
        int setNumber = 0;
        Pageable pager = new PageRequest(setNumber, 200, sort);
        Page<Association> associationPage = associationService.findPublishedAssociations(pager);

        Iterator<Association> assoIterator = associationPage.iterator();
        while (assoIterator.hasNext()) {
            jsons.addAll(processAssociation(assoIterator.next(), snpToGeneMapper));
        }
        while (associationPage.hasNext()) {

            pager = associationPage.nextPageable();
            associationPage = associationService.findPublishedAssociations(pager);

            assoIterator = associationPage.iterator();
            while (assoIterator.hasNext()) {

                jsons.addAll(processAssociation(assoIterator.next(), snpToGeneMapper));

            }
        }





////        18193044, gwas_sample_size
//        Collection<Association> assos = associationService.findPublishedAssociationsByStudyId(Long.parseLong("5320"));
//        for(Association asso : assos){
//            jsons.addAll(processAssociation(asso, snpToGeneMapper));
//        }


        return jsons;
    }


    private Collection<String> processAssociation(Association association, SnpToGeneMapper snpToGeneMapper) {
        Collection<String> jsons = new ArrayList<>();
//        associationService.loadAssociatedData(association);
        Collection<EfoTrait> efoTraits = association.getEfoTraits();
        Collection<Locus> loci = association.getLoci();

        Collection<Ancestry> ancestries = association.getStudy().getAncestries();
        int sampleSize = 0;

        System.out.println("\n\nancestry size = " + ancestries.size());
        for (Ancestry ancestry : ancestries) {
//            if("initial".equals(ancestry.getType())) {
                int ancestryCount = 0;
                if (ancestry.getNumberOfIndividuals() != null) {
                    ancestryCount = ancestry.getNumberOfIndividuals();
                }
                System.out.println("ancestryCount = " + ancestryCount);
                sampleSize = sampleSize + ancestryCount;
                System.out.println("sampleSize = " + sampleSize);
//            }
        }

        if(sampleSize > 0) {

            for (Locus locus : loci) {
                Collection<RiskAllele> riskAlleles = locus.getStrongestRiskAlleles();
                for (RiskAllele riskAllele : riskAlleles) {

                    for (EfoTrait efoTrait : efoTraits) {

                        SnpInfo snpInfo = snpToGeneMapper.get(riskAllele.getSnp().getRsId());
                        if (snpInfo != null) {

                            List<String> ensemblIds = snpInfo.getEnsemblIds();

                            //If the study is in gwas then it means, the author looked at at least 100 000 snps.
                            //If the number exact of snp is not specified then we put 100000.
                            long snpCount = 100000;
                            if (association.getStudy().getSnpCount() != null) {
                                snpCount = association.getStudy().getSnpCount();
                            }
                            if (association.getPvalueMantissa() != null && association.getPvalueExponent()<0) {

                                for (String ensemblId : ensemblIds) {
                                    jsons.add(buildJson(association.getPvalueMantissa() + "e" + association.getPvalueExponent(),
                                                    efoTrait.getUri(),
                                                    riskAllele.getSnp().getRsId(),
                                                    association.getStudy().getPubmedId(),
                                                    sampleSize,
                                                    snpCount,
                                                    ensemblId)
                                    );
                                }
                            }
                        }

                    }

                }
            }
        }
        return jsons;


    }

    public String buildJson(String pvalue, String efoTrait, String rsId, String pubmedId, int sampleSize, long gwasPanelResolution, String ensemblId) {

        String dbVersion = getDate();
        String gwasDbId = "http://identifiers.org/gwascatalog";
        String jsonSchemaVersion = "1.2.2";
        String soTerm = "http://purl.obolibrary.org/obo/SO_0001627";

        JsonObject target = Json.createObjectBuilder()
                .add("activity", "http://identifiers.org/cttv.activity/predicted_damaging")
                .add("id", Json.createArrayBuilder().add("http://identifiers.org/ensembl/" + ensemblId))
                .add("target_type", "http://identifiers.org/cttv.target/gene_evidence").build();

        JsonObject variant = Json.createObjectBuilder()
                .add("type", "snp single")
                .add("id", Json.createArrayBuilder().add("http://identifiers.org/dbsnp/" + rsId))
                .build();

        JsonObject disease = Json.createObjectBuilder()
                .add("id", Json.createArrayBuilder().add(efoTrait))
                .build();

        JsonObject uniqueAssociationFields = Json.createObjectBuilder()
                .add("sample_size", Integer.toString(sampleSize))
                .add("gwas_panel_resolution", Long.toString(gwasPanelResolution))
                .add("pubmed_refs", "http://europepmc.org/abstract/MED/" + pubmedId)
                .add("target", "http://identifiers.org/ensembl/" + ensemblId)
                .add("object", efoTrait)
                .add("variant", "http://identifiers.org/dbsnp/" + rsId)
                .add("study_name", "cttv009_gwas_catalog")
                .add("pvalue", pvalue)
                .build();


        JsonObject litId = Json.createObjectBuilder().add("lit_id", "http://europepmc.org/abstract/MED/" + pubmedId).build();
        JsonArray references = Json.createArrayBuilder().add(litId).build();
        JsonObject literature = Json.createObjectBuilder().add("references", references).build();


        JsonArray litIdArray = Json.createArrayBuilder().add(litId).build();
        JsonObject literatureHigher = Json.createObjectBuilder().add("references", litIdArray)
                .build();

        JsonObject expert = Json.createObjectBuilder()
                .add("status", true)
                .add("statement", "Primary submitter of data").build();

        JsonObject dbXref = Json.createObjectBuilder()
                .add("version", dbVersion)
                .add("id", gwasDbId)
                .build();

        JsonObject database = Json.createObjectBuilder()
                .add("version", dbVersion)
                .add("id", "GWAS Catalog")
                .add("dbxref", dbXref)
                .build();

        JsonObject variant2diseaseProvenanceType = Json.createObjectBuilder()
                .add("literature", literature)
                .add("expert", expert)
                .add("database", database)
                .build();

        JsonObject method = Json.createObjectBuilder()
                .add("description", "pvalue for the snp to disease association.")
                .build();



        JsonObject resourceScore = Json.createObjectBuilder()
                .add("type", "pvalue")
                .add("method", method)
                .add("value", pvalue)
                .build();

        JsonArray evidenceCodes = Json.createArrayBuilder()
                .add("http://identifiers.org/eco/GWAS")
                .add("http://purl.obolibrary.org/obo/ECO_0000205")
                .build();

        JsonObject variant2disease = Json.createObjectBuilder()
                .add("gwas_sample_size", sampleSize)
                .add("unique_experiment_reference", "http://europepmc.org/abstract/MED/" + pubmedId)
                .add("gwas_panel_resolution", gwasPanelResolution)
                .add("provenance_type", variant2diseaseProvenanceType)
                .add("is_associated", true)
                .add("resource_score", resourceScore)
                .add("evidence_codes", evidenceCodes)
                .add("date_asserted", dbVersion)
                .build();

        JsonObject gene2Variantexpert = Json.createObjectBuilder()
                .add("status", true)
                .add("statement", "Primary submitter of data")
                .build();


        JsonObject gene2variantProvenanceType = Json.createObjectBuilder()
                .add("expert", gene2Variantexpert)
                .add("database", database)
                .build();

        JsonArray gene2VariantEvidenceCodes = Json.createArrayBuilder()
                .add("http://purl.obolibrary.org/obo/ECO_0000205")
                .add("http://identifiers.org/eco/cttv_mapping_pipeline")
                .build();

        JsonObject gene2variant = Json.createObjectBuilder()
                .add("provenance_type", gene2variantProvenanceType)
                .add("is_associated", true)
                .add("date_asserted", dbVersion)
                .add("evidence_codes", gene2VariantEvidenceCodes)
                .add("functional_consequence", soTerm)
                .build();

        JsonObject evidence = Json.createObjectBuilder()
//                .add("provenance_type", provenanceType)
                .add("variant2disease", variant2disease)
                .add("gene2variant", gene2variant)
                .build();


        JsonObject json = Json.createObjectBuilder()
                .add("target", target)
                .add("access_level", "public")
                .add("sourceID", "gwascatalog")
                .add("variant", variant)
                .add("disease", disease)
                .add("unique_association_fields", uniqueAssociationFields)
                .add("evidence", evidence)
                .add("validated_against_schema_version", jsonSchemaVersion)
                .add("type", "genetic_association")
                .add("literature", literatureHigher)
                .build();


        String jsonToReturn = json.toString();
//        Pattern pattern = Pattern.compile(",\"value\":(\".E.\")");
//        Matcher matcher = pattern.matcher(jsonToReturn);
//
//        boolean found = false;
//        while (matcher.find()) {
//            System.out.println("I found the text" +
//                            matcher.group() + " starting at " +
//                            "index " + matcher.start() + " and ending at index " +  matcher.end() );
//            found = true;
//        }


        jsonToReturn = removeQuoteAroundPvalue(jsonToReturn);
        System.out.println("\n"+ jsonToReturn);
        return jsonToReturn;

    }

//    public static void main(String[] args) {
//        Double monDouble = Double.valueOf("4e-8");
//        System.out.println(monDouble);
//        getDate();
//    }


    public static String getDate() {
        String date = "";
        Calendar c = new GregorianCalendar();

        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        String dayOfMonthString = "";
        if (dayOfMonth < 10) {
            dayOfMonthString = "0" + dayOfMonth;
        } else {
            dayOfMonthString = "" + dayOfMonth;

        }

        int monthNumber = c.get(Calendar.MONTH);
        String monthNumberString;
        if (monthNumber < 10) {
            monthNumberString = "0" + monthNumber;
        } else {
            monthNumberString = "" + monthNumber;

        }

        int year = c.get(Calendar.YEAR);


        int hour = c.get(Calendar.HOUR);
        String hourString;
        if (hour < 10) {
            hourString = "0" + hour;
        } else {
            hourString = "" + hour;

        }


        int minute = c.get(Calendar.MINUTE);
        String minuteString;
        if (minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = "" + minute;
        }

        int seconds = c.get(Calendar.SECOND);
        String secondsString;
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }


        date = year + "-" + monthNumberString + "-" + dayOfMonthString + "T" + hourString + ":" + minuteString + ":" + secondsString + "+00:00";


        return date;
    }


    public static String  removeQuoteAroundPvalue(String json){
//        String jsonToReturn = "{\"target\":{\"activity\":\"http://identifiers.org/cttv.activity/predicted_damaging\",\"id\":[\"http://identifiers.org/ensembl/ENSG00000133048\"],\"target_type\":\"http://identifiers.org/cttv.target/gene_evidence\"},\"access_level\":\"public\",\"sourceID\":\"gwascatalog\",\"variant\":{\"type\":\"snp single\",\"id\":[\"http://identifiers.org/dbsnp/rs4950928\"]},\"disease\":{\"id\":[\"http://www.ebi.ac.uk/efo/EFO_0004869\"]},\"unique_association_fields\":{\"sample_size\":\"1772\",\"gwas_panel_resolution\":\"290325\",\"pubmed_refs\":\"http://europepmc.org/abstract/MED/18403759\",\"target\":\"http://identifiers.org/ensembl/ENSG00000133048\",\"object\":\"http://www.ebi.ac.uk/efo/EFO_0004869\",\"variant\":\"http://identifiers.org/dbsnp/rs4950928\",\"study_name\":\"cttv009_gwas_catalog\",\"pvalue\":\"1.0E-13\"},\"evidence\":{\"variant2disease\":{\"gwas_sample_size\":1772,\"unique_experiment_reference\":\"http://europepmc.org/abstract/MED/18403759\",\"gwas_panel_resolution\":290325,\"provenance_type\":{\"literature\":{\"references\":[{\"lit_id\":\"http://europepmc.org/abstract/MED/18403759\"}]},\"expert\":{\"status\":true,\"statement\":\"Primary submitter of data\"},\"database\":{\"version\":\"2016-01-24T09:42:05+00:00\",\"id\":\"GWAS Catalog\",\"dbxref\":{\"version\":\"2016-01-24T09:42:05+00:00\",\"id\":\"http://identifiers.org/gwascatalog\"}}},\"is_associated\":true,\"resource_score\":{\"type\":\"pvalue\",\"method\":{\"description\":\"pvalue for the snp to disease association.\"},\"value\":\"1.0E-13\"},\"evidence_codes\":[\"http://identifiers.org/eco/GWAS\",\"http://purl.obolibrary.org/obo/ECO_0000205\"],\"date_asserted\":\"2016-01-24T09:42:05+00:00\"},\"gene2variant\":{\"provenance_type\":{\"expert\":{\"status\":true,\"statement\":\"Primary submitter of data\"},\"database\":{\"version\":\"2016-01-24T09:42:05+00:00\",\"id\":\"GWAS Catalog\",\"dbxref\":{\"version\":\"2016-01-24T09:42:05+00:00\",\"id\":\"http://identifiers.org/gwascatalog\"}}},\"is_associated\":true,\"date_asserted\":\"2016-01-24T09:42:05+00:00\",\"evidence_codes\":[\"http://purl.obolibrary.org/obo/ECO_0000205\",\"http://identifiers.org/eco/cttv_mapping_pipeline\"],\"functional_consequence\":\"http://purl.obolibrary.org/obo/SO_0001627\"}},\"validated_against_schema_version\":\"1.2.2\",\"type\":\"genetic_association\",\"literature\":{\"references\":[{\"lit_id\":\"http://europepmc.org/abstract/MED/18403759\"}]}}";
        Pattern pattern = Pattern.compile("\"value\":\"(.*e-\\d{1,3})\"},");
        Matcher matcher = pattern.matcher(json);

        boolean found = false;
        int count = 0;
        while (matcher.find()) {
            count++;
            System.out.println("I found the text " +
                    matcher.group(1) + " starting at " +
                    "index " + matcher.start(1) + " and ending at index " +  matcher.end(1) );
            found = true;




            json = json.replace(",\"value\":\"" + matcher.group(1) + "\"", ",\"value\":" + matcher.group(1));


        }
        return json;
    }

//    public static void main(String[] args) {
//        String jsonToReturn = "{\"target\":{\"activity\":\"http://identifiers.org/cttv.activity/predicted_damaging\",\"id\":[\"http://identifiers.org/ensembl/ENSG00000133048\"],\"target_type\":\"http://identifiers.org/cttv.target/gene_evidence\"},\"access_level\":\"public\",\"sourceID\":\"gwascatalog\",\"variant\":{\"type\":\"snp single\",\"id\":[\"http://identifiers.org/dbsnp/rs4950928\"]},\"disease\":{\"id\":[\"http://www.ebi.ac.uk/efo/EFO_0004869\"]},\"unique_association_fields\":{\"sample_size\":\"1772\",\"gwas_panel_resolution\":\"290325\",\"pubmed_refs\":\"http://europepmc.org/abstract/MED/18403759\",\"target\":\"http://identifiers.org/ensembl/ENSG00000133048\",\"object\":\"http://www.ebi.ac.uk/efo/EFO_0004869\",\"variant\":\"http://identifiers.org/dbsnp/rs4950928\",\"study_name\":\"cttv009_gwas_catalog\",\"pvalue\":\"1.0E-13\"},\"evidence\":{\"variant2disease\":{\"gwas_sample_size\":1772,\"unique_experiment_reference\":\"http://europepmc.org/abstract/MED/18403759\",\"gwas_panel_resolution\":290325,\"provenance_type\":{\"literature\":{\"references\":[{\"lit_id\":\"http://europepmc.org/abstract/MED/18403759\"}]},\"expert\":{\"status\":true,\"statement\":\"Primary submitter of data\"},\"database\":{\"version\":\"2016-01-24T09:42:05+00:00\",\"id\":\"GWAS Catalog\",\"dbxref\":{\"version\":\"2016-01-24T09:42:05+00:00\",\"id\":\"http://identifiers.org/gwascatalog\"}}},\"is_associated\":true,\"resource_score\":{\"type\":\"pvalue\",\"method\":{\"description\":\"pvalue for the snp to disease association.\"},\"value\":\"1.0E-13\"},\"evidence_codes\":[\"http://identifiers.org/eco/GWAS\",\"http://purl.obolibrary.org/obo/ECO_0000205\"],\"date_asserted\":\"2016-01-24T09:42:05+00:00\"},\"gene2variant\":{\"provenance_type\":{\"expert\":{\"status\":true,\"statement\":\"Primary submitter of data\"},\"database\":{\"version\":\"2016-01-24T09:42:05+00:00\",\"id\":\"GWAS Catalog\",\"dbxref\":{\"version\":\"2016-01-24T09:42:05+00:00\",\"id\":\"http://identifiers.org/gwascatalog\"}}},\"is_associated\":true,\"date_asserted\":\"2016-01-24T09:42:05+00:00\",\"evidence_codes\":[\"http://purl.obolibrary.org/obo/ECO_0000205\",\"http://identifiers.org/eco/cttv_mapping_pipeline\"],\"functional_consequence\":\"http://purl.obolibrary.org/obo/SO_0001627\"}},\"validated_against_schema_version\":\"1.2.2\",\"type\":\"genetic_association\",\"literature\":{\"references\":[{\"lit_id\":\"http://europepmc.org/abstract/MED/18403759\"}]}}";
//        jsonToReturn = removeQuoteAroundPvalue(jsonToReturn);
//
//        System.out.println(jsonToReturn);
//
//
//
//
//
//        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{2}:\\d{2}");
//        Matcher matcher = pattern.matcher("2016-01-24T09:42:05+00:00");
//
//        boolean found = false;
//        int count = 0;
//        while (matcher.find()) {
//            count++;
//            System.out.println("I found the text " +
//                    matcher.group() + " starting at " +
//                    "index " + matcher.start() + " and ending at index " +  matcher.end() );
//            found = true;
//
//
//
//
//
//
//        }


//    }


}
