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
import javax.json.JsonObjectBuilder;
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
        Page<Association> associationPage = associationService.findAll(pager);
        int count = 0;
        Iterator<Association> assoIterator = associationPage.iterator();
        while (assoIterator.hasNext()) {
            jsons.addAll(processAssociation(assoIterator.next(), snpToGeneMapper));
            count++;
        }
        System.out.println("Count: " + count);
        while (associationPage.hasNext()) {

            pager = associationPage.nextPageable();
            associationPage = associationService.findAll(pager);

            assoIterator = associationPage.iterator();
            while (assoIterator.hasNext()) {

                jsons.addAll(processAssociation(assoIterator.next(), snpToGeneMapper));
                count++;
            }
            System.out.println("Count: " + count);
        }

        return jsons;

    }


    private Collection<String> processAssociation(Association association, SnpToGeneMapper snpToGeneMapper) {
        Collection<String> jsons = new ArrayList<>();
//        associationService.loadAssociatedData(association);
        Collection<EfoTrait> efoTraits = association.getEfoTraits();
        Collection<Locus> loci = association.getLoci();

        Float oddRatio = association.getOrPerCopyNum();
        String range = association.getRange();
        if (range == null){
            range = "NR";
            System.out.println(association.getStudy().getId());
        } else {
            range = range.replace("[", "").replace("]", "");
        }

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

                        List<SnpInfo> snpInfoList = snpToGeneMapper.get(riskAllele.getSnp().getRsId());
                        if (snpInfoList != null) {
                            for (SnpInfo snpInfo : snpInfoList) {
                                List<String> ensemblIds = snpInfo.getEnsemblIds();
                                String soTerm = snpInfo.getSoTerm();
                                //If the study is in gwas then it means, the author looked at at least 100 000 snps.
                                //If the number exact of snp is not specified then we put 100000.
                                long snpCount = 100000;
                                if (association.getStudy().getSnpCount() != null) {
                                    snpCount = association.getStudy().getSnpCount();
                                }
                                // Stet pvalue to minimum system value
                                double pvalue = 2.2E-308;
                                if (association.getPvalue() != 0.0) {
                                    pvalue = association.getPvalue();
                                }
                                if (association.getPvalueMantissa() != null && association.getPvalueExponent() < 0) {
                                    for (String ensemblId : ensemblIds) {
                                        jsons.add(buildJson(association.getPvalueMantissa(),
                                                association.getPvalueExponent(),
                                                association.getPvalueDescription(),
                                                pvalue,
                                                efoTrait.getUri(),
                                                riskAllele.getSnp().getRsId(),
                                                association.getStudy().getPublicationId().getPubmedId(),
                                                association.getStudy().getAccessionId(),
                                                sampleSize,
                                                snpCount,
                                                ensemblId,
                                                soTerm,
                                                oddRatio,
                                                range)
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return jsons;
    }

    public String buildJson(int pvalueMantissa, int pvalueExponent, String pvalueText, double pvalue, String efoTrait, String rsId, String pubmedId, String study_acc, int sampleSize, long gwasPanelResolution, String ensemblId, String soTerm,
                            Float oddRatio, String range) {

        String dbVersion = getDate();
        String gwasDbId = "http://identifiers.org/gwas_catalog";
        String jsonSchemaVersion = "1.6.2";

        JsonObject target = Json.createObjectBuilder()
                .add("activity", "http://identifiers.org/cttv.activity/predicted_damaging")
                .add("id", "http://identifiers.org/ensembl/" + ensemblId)
                .add("target_type", "http://identifiers.org/cttv.target/gene_evidence").build();

        JsonObject variant = Json.createObjectBuilder()
                .add("type", "snp single")
                .add("id", "http://identifiers.org/dbsnp/" + rsId)
                .build();

        JsonObject disease = Json.createObjectBuilder()
                .add("id", efoTrait)
                .build();

        JsonObjectBuilder uniqueAssociationBuilder = Json.createObjectBuilder()
                .add("pubmed_refs", "http://europepmc.org/abstract/MED/" + pubmedId)
                .add("target", "http://identifiers.org/ensembl/" + ensemblId)
                .add("disease_id", efoTrait)
                .add("variant", "http://identifiers.org/dbsnp/" + rsId);

        if(study_acc == null){
            uniqueAssociationBuilder.add("study_name", "");
        } else {
            uniqueAssociationBuilder.add("study_name", study_acc);
        }

        if(pvalueText == null){
            uniqueAssociationBuilder.add("pvalue_annotation", "");
        } else {
            uniqueAssociationBuilder.add("pvalue_annotation", pvalueText);
        }

        JsonObject uniqueAssociationFields = uniqueAssociationBuilder.build();



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
                .add("mantissa", pvalueMantissa)
                .add("exponent", pvalueExponent)
                .build();

        JsonArray evidenceCodes = Json.createArrayBuilder()
                .add("http://identifiers.org/eco/GWAS")
                .add("http://purl.obolibrary.org/obo/ECO_0000205")
                .build();

        JsonObjectBuilder variant2diseaseBuilder = Json.createObjectBuilder()
                .add("gwas_sample_size", sampleSize)
                .add("unique_experiment_reference", "http://europepmc.org/abstract/MED/" + pubmedId)
                .add("gwas_panel_resolution", gwasPanelResolution)
                .add("provenance_type", variant2diseaseProvenanceType)
                .add("is_associated", true)
                .add("resource_score", resourceScore)
                .add("evidence_codes", evidenceCodes)
                .add("date_asserted", dbVersion)
                .add("confidence_interval", range);

        if(oddRatio == null){
            variant2diseaseBuilder.add("odds_ratio", "");
        } else {
            variant2diseaseBuilder.add("odds_ratio", Float.toString(oddRatio));
        }
        JsonObject variant2diseaseFields = variant2diseaseBuilder.build();

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
                .add("variant2disease", variant2diseaseFields)
                .add("gene2variant", gene2variant)
                .build();


        JsonObject json = Json.createObjectBuilder()
                .add("target", target)
                .add("access_level", "public")
                .add("sourceID", "gwas_catalog")
                .add("variant", variant)
                .add("disease", disease)
                .add("unique_association_fields", uniqueAssociationFields)
                .add("evidence", evidence)
                .add("validated_against_schema_version", jsonSchemaVersion)
                .add("type", "genetic_association")
                .add("literature", literatureHigher)
                .build();


        String jsonToReturn = json.toString();

        System.out.println("\n"+ jsonToReturn);
        return jsonToReturn;

    }



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

}
