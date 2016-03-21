package uk.ac.ebi.spot.goci.curation.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * @author emma
 *         <p>
 *         This class takes an Excel spreadsheet sheet and extracts all the association records For each SNP, an
 *         SnpAssociationForm object is created and passed back to the controller for further processing
 *         <p>
 *         Created from code originally written by Dani/Tony. Adapted to fit with new curation system.
 */
@Service
public class AssociationSheetProcessor {

    // Services
    private AssociationCalculationService associationCalculationService;
    private LociAttributesService lociAttributesService;

    // Repository
    private EfoTraitRepository efoTraitRepository;
    private LocusRepository locusRepository;

    // Logging
    private Logger log = LoggerFactory.getLogger(getClass());
    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationSheetProcessor(AssociationCalculationService associationCalculationService,
                                     LociAttributesService lociAttributesService,
                                     EfoTraitRepository efoTraitRepository,
                                     LocusRepository locusRepository) {

        this.associationCalculationService = associationCalculationService;
        this.lociAttributesService = lociAttributesService;
        this.efoTraitRepository = efoTraitRepository;
        this.locusRepository = locusRepository;
    }

    // Read and parse uploaded spreadsheet
    public Collection<Association> readSnpAssociations(XSSFSheet sheet) {

        // Create collection to store all newly created associations
        Collection<Association> newAssociations = new ArrayList<>();

        boolean done = false;
        int rowNum = 1;

        while (!done) {
            XSSFRow row = sheet.getRow(rowNum);

            if (row == null) {
                done = true;
                getLog().debug("Last row read");
            }
            else {

                // Get gene values
                String authorReportedGene = null;
                if (row.getCell(0, row.RETURN_BLANK_AS_NULL) != null) {
                    authorReportedGene = row.getCell(0).getRichStringCellValue().getString();
                }
                else {
                    getLog().debug("Gene is null in row " + row.getRowNum());
                }

                // Get Strongest SNP-Risk Allele
                String strongestAllele = null;
                if (row.getCell(1, row.RETURN_BLANK_AS_NULL) != null) {
                    strongestAllele = row.getCell(1).getRichStringCellValue().getString();
                }
                else {
                    getLog().debug("Risk allele is null in row " + row.getRowNum());
                }

                // Get SNP
                String snp = null;
                if (row.getCell(2, row.RETURN_BLANK_AS_NULL) != null) {
                    snp = row.getCell(2).getRichStringCellValue().getString();
                }
                else {
                    getLog().debug("SNP is null in row " + row.getRowNum());
                }

                // Get Proxy SNP
                String proxy = null;
                if (row.getCell(3, row.RETURN_BLANK_AS_NULL) != null) {
                    proxy = row.getCell(3).getRichStringCellValue().getString();
                }
                else {
                    getLog().debug("Proxy SNP is null in row " + row.getRowNum());
                }

                // Get Risk Allele Frequency, will contain multiple values for haplotype or interaction
                String riskFrequency = null;
                if (row.getCell(4, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell risk = row.getCell(4);
                    switch (risk.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            riskFrequency = risk.getRichStringCellValue().getString();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            riskFrequency = Double.toString(risk.getNumericCellValue());
                            break;
                    }
                }
                else {
                    getLog().debug("RF is null in row " + row.getRowNum());
                }

                // Will be a single value that applies to association
                String associationRiskFrequency = null;
                if (row.getCell(5, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell risk = row.getCell(5);
                    switch (risk.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            associationRiskFrequency = risk.getRichStringCellValue().getString();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            associationRiskFrequency = Double.toString(risk.getNumericCellValue());
                            break;
                    }
                }
                else {
                    getLog().debug("RF is null in row " + row.getRowNum());
                }

                // Get P-value mantissa	and P-value exponent
                Integer pvalueMantissa = null;
                Integer pvalueExponent = null;

                if (row.getCell(6, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell mant = row.getCell(6);
                    switch (mant.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            pvalueMantissa = null;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalueMantissa = (int) mant.getNumericCellValue();
                            break;
                    }
                }
                else {
                    pvalueMantissa = null;
                    getLog().debug("pvalue mantissa is null in row " + row.getRowNum());
                }

                if (row.getCell(7, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell expo = row.getCell(7);
                    switch (expo.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            pvalueExponent = null;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalueExponent = (int) expo.getNumericCellValue();
                            break;
                    }
                }
                else {
                    pvalueExponent = null;
                    getLog().debug("pvalue exponent is null in row " + row.getRowNum());
                }

                // Get P-value description
                String pvalueDescription;
                if (row.getCell(8, row.RETURN_BLANK_AS_NULL) != null) {
                    pvalueDescription = row.getCell(8).getRichStringCellValue().getString();
                }
                else {
                    pvalueDescription = null;
                    getLog().debug("pvalue text is null in row " + row.getRowNum());
                }

                // Get OR num
                Float orPerCopyNum = null;
                if (row.getCell(9, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell or = row.getCell(9);
                    switch (or.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyNum = null;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyNum = (float) or.getNumericCellValue();
                            break;
                    }
                }
                else {
                    orPerCopyNum = null;
                    getLog().debug("OR is null in row " + row.getRowNum());
                }

                // Get reciprocal OR
                Float orPerCopyRecip = null;
                if (row.getCell(10, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell recip = row.getCell(10);
                    switch (recip.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyRecip = null;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyRecip = (float) recip.getNumericCellValue();
                            break;
                    }
                }
                else {
                    orPerCopyRecip = null;
                    getLog().debug("OR recip is null in row " + row.getRowNum());
                }

                // Get Beta
                Float betaNum = null;
                if (row.getCell(11, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell beta = row.getCell(11);
                    switch (beta.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            betaNum = null;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            betaNum = (float) beta.getNumericCellValue();
                            break;
                    }
                }
                else {
                    orPerCopyRecip = null;
                    getLog().debug("Beta is null in row " + row.getRowNum());
                }

                // Get Beta unit
                String betaUnit;
                if (row.getCell(12, row.RETURN_BLANK_AS_NULL) != null) {
                    betaUnit = row.getCell(12).getRichStringCellValue().getString();
                }
                else {
                    betaUnit = null;
                    getLog().debug("Beta unit is null in row " + row.getRowNum());
                }


                // Get Beta direction
                String betaDirection;
                if (row.getCell(13, row.RETURN_BLANK_AS_NULL) != null) {
                    betaDirection = row.getCell(13).getRichStringCellValue().getString();
                }
                else {
                    betaDirection = null;
                    getLog().debug("Beta direction is null in row " + row.getRowNum());
                }

                // Get range
                String range;
                if (row.getCell(14, row.RETURN_BLANK_AS_NULL) != null) {
                    range = row.getCell(14).getRichStringCellValue().getString();
                }
                else {
                    range = null;
                    getLog().debug("CI is null in row " + row.getRowNum());
                }

                // Get OR recip range
                String orPerCopyRecipRange;
                if (row.getCell(15, row.RETURN_BLANK_AS_NULL) != null) {
                    orPerCopyRecipRange = row.getCell(15).getRichStringCellValue().getString();
                }
                else {
                    orPerCopyRecipRange = null;
                    getLog().debug("CI is null in row " + row.getRowNum());
                }

                // Get standard error
                Float standardError = null;
                if (row.getCell(16, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell std = row.getCell(16);
                    switch (std.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            standardError = null;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            standardError = (float) std.getNumericCellValue();
                            break;
                    }
                }
                else {
                    standardError = null;
                    getLog().debug("SE is null in row " + row.getRowNum());
                }

                // Get Multi-SNP Haplotype value
                String multiSnpHaplotype;
                if (row.getCell(17, row.RETURN_BLANK_AS_NULL) != null) {
                    multiSnpHaplotype = row.getCell(17).getRichStringCellValue().getString();
                }
                else {
                    multiSnpHaplotype = null;
                    getLog().debug("Multi-SNP Haplotype is null in row " + row.getRowNum());
                }

                // Get SNP interaction value
                String snpInteraction;
                if (row.getCell(18, row.RETURN_BLANK_AS_NULL) != null) {
                    snpInteraction = row.getCell(18).getRichStringCellValue().getString();
                }
                else {
                    snpInteraction = null;
                    getLog().debug("SNP interaction is null in row " + row.getRowNum());
                }

                // Get SNP Status
                String snpStatus;
                if (row.getCell(19, row.RETURN_BLANK_AS_NULL) != null) {
                    snpStatus = row.getCell(19).getRichStringCellValue().getString().toLowerCase();
                }
                else {
                    snpStatus = null;
                    getLog().debug("SNP status is null in row " + row.getRowNum());
                }

    /*            // Get Beta unit and direction/description
                String orPerCopyUnitDescr;
                if (row.getCell(16) != null) {
                    orPerCopyUnitDescr = row.getCell(16).getRichStringCellValue().getString();
                }
                else {
                    orPerCopyUnitDescr = null;
                    getLog().debug("OR direction is null in row " + row.getRowNum());
                }*/

                // Get SNP type (novel / known)
                String snpType;
                if (row.getCell(20, row.RETURN_BLANK_AS_NULL) != null) {
                    snpType = row.getCell(20).getRichStringCellValue().getString().toLowerCase();
                }
                else {
                    snpType = null;
                    getLog().debug("SNP type is null in row " + row.getRowNum());
                }

                String efoTrait;
                if (row.getCell(21, row.RETURN_BLANK_AS_NULL) != null) {
                    efoTrait = row.getCell(21).getRichStringCellValue().getString();
                }
                else {
                    efoTrait = null;
                    getLog().debug("EFO trait is null in row " + row.getRowNum());
                }

                // Once we have all the values entered in file process them
                if (authorReportedGene == null && strongestAllele == null && snp == null && proxy == null &&
                        riskFrequency == null) {
                    done = true;
                    getLog().debug("Empty row that wasn't caught via 'row = null'");
                }
                else {

                    Association newAssociation = new Association();

                    // Set EFO traits
                    if (efoTrait != null) {
                        String[] uris = efoTrait.split(",");
                        Collection<String> efoUris = new ArrayList<>();

                        for (String uri : uris) {
                            String trimmedUri = uri.trim();
                            efoUris.add(trimmedUri);
                        }

                        Collection<EfoTrait> efoTraits = getEfoTraitsFromRepository(efoUris);

                        newAssociation.setEfoTraits(efoTraits);
                    }

                    // Set values common to all association types
                    newAssociation.setRiskFrequency(associationRiskFrequency);
                    newAssociation.setPvalueMantissa(pvalueMantissa);
                    newAssociation.setPvalueExponent(pvalueExponent);
                    newAssociation.setPvalueText(pvalueDescription);
                    newAssociation.setOrPerCopyRecip(orPerCopyRecip);
                    newAssociation.setStandardError(standardError);
                    newAssociation.setOrPerCopyRecipRange(orPerCopyRecipRange);
                    newAssociation.setDescription(orPerCopyUnitDescr);
                    newAssociation.setSnpType(snpType);

                    boolean recipReverse = false;
                    // Calculate OR per copy num
                    if ((orPerCopyRecip != null) && (orPerCopyNum == null)) {
                        orPerCopyNum = ((100 / orPerCopyRecip) / 100);
                        newAssociation.setOrPerCopyNum(orPerCopyNum);
                        recipReverse = true;
                    }
                    // Otherwise set to whatever is in upload
                    else {
                        newAssociation.setOrPerCopyNum(orPerCopyNum);
                    }

                    // This logic is retained from Dani's original code
                    if ((orPerCopyRecipRange != null) && recipReverse) {
                        range = associationCalculationService.reverseCI(orPerCopyRecipRange);
                        newAssociation.setRange(range);
                    }
                    else if ((range == null) && (standardError != null)) {
                        range = associationCalculationService.setRange(standardError, orPerCopyNum);
                        newAssociation.setRange(range);
                    }
                    else {
                        newAssociation.setRange(range);
                    }

                    if (orType.equalsIgnoreCase("Y")) {
                        newAssociation.setOrType(true);
                    }
                    else {
                        newAssociation.setOrType(false);
                    }

                    if (multiSnpHaplotype.equalsIgnoreCase("Y")) {
                        newAssociation.setMultiSnpHaplotype(true);
                    }
                    else {
                        newAssociation.setMultiSnpHaplotype(false);
                    }

                    if (snpInteraction.equalsIgnoreCase("Y")) {
                        newAssociation.setSnpInteraction(true);
                    }
                    else {
                        newAssociation.setSnpInteraction(false);
                    }

                    String delimiter;
                    Collection<Locus> loci = new ArrayList<>();

                    if (newAssociation.getSnpInteraction()) {
                        delimiter = "x";

                        // For SNP interaction studies we need to create a locus per risk allele
                        // Handle curator entered risk allele
                        Collection<RiskAllele> locusRiskAlleles =
                                createLocusRiskAlleles(strongestAllele,
                                                       snp,
                                                       proxy,
                                                       riskFrequency,
                                                       snpStatus,
                                                       delimiter);

                        // Add genes to relevant loci, split by 'x' delimiter first
                        Collection<Locus> lociWithAddedGenes = new ArrayList<>();

                        // Deal with genes for each interaction which should be
                        // separated by 'x'
                        String[] separatedGenes = authorReportedGene.split(delimiter);
                        int geneIndex = 0;

                        for (RiskAllele riskAllele : locusRiskAlleles) {
                            Locus locus = new Locus();

                            // Set risk alleles, assume one locus per risk allele
                            Collection<RiskAllele> currentLocusRiskAlleles = new ArrayList<>();
                            currentLocusRiskAlleles.add(riskAllele);
                            locus.setStrongestRiskAlleles(currentLocusRiskAlleles);

                            // Set gene
                            String interactionGene = separatedGenes[geneIndex];
                            Collection<Gene> locusGenes = createLocusGenes(interactionGene, ",");
                            locus.setAuthorReportedGenes(locusGenes);
                            geneIndex++;

                            // Set description
                            locus.setDescription("SNP x SNP interaction");

                            // Save our newly created locus
                            locusRepository.save(locus);
                            loci.add(locus);
                        }
                    }

                    // Handle multi-snp and standard snp
                    else {
                        delimiter = ";";

                        // For multi-snp and standard snps we assume their is only one locus
                        Locus locus = new Locus();

                        // Handle curator entered genes, for haplotype they are separated by a comma
                        Collection<Gene> locusGenes = createLocusGenes(authorReportedGene, ",");
                        locus.setAuthorReportedGenes(locusGenes);

                        // Handle curator entered risk allele
                        Collection<RiskAllele> locusRiskAlleles =
                                createLocusRiskAlleles(strongestAllele,
                                                       snp,
                                                       proxy,
                                                       riskFrequency,
                                                       snpStatus,
                                                       delimiter);


                        // For standard associations set the risk allele frequency to the
                        // same value as the overall association frequency
                        Collection<RiskAllele> locusRiskAllelesWithRiskFrequencyValues = new ArrayList<>();
                        if (!newAssociation.getMultiSnpHaplotype()) {
                            for (RiskAllele riskAllele : locusRiskAlleles) {
                                riskAllele.setRiskFrequency(associationRiskFrequency);
                                locusRiskAllelesWithRiskFrequencyValues.add(riskAllele);
                            }
                            locus.setStrongestRiskAlleles(locusRiskAllelesWithRiskFrequencyValues);
                        }

                        else {
                            locus.setStrongestRiskAlleles(locusRiskAlleles);
                        }

                        // Set locus attributes
                        Integer haplotypeCount = locusRiskAlleles.size();
                        if (haplotypeCount > 1) {
                            locus.setHaplotypeSnpCount(haplotypeCount);
                            locus.setDescription(String.valueOf(haplotypeCount) + "-SNP haplotype");
                        }

                        else {
                            locus.setDescription("Single variant");
                        }

                        // Save our newly created locus
                        locusRepository.save(locus);
                        loci.add(locus);
                    }

                    newAssociation.setLoci(loci);

                    // Add all newly created associations to collection
                    newAssociations.add(newAssociation);
                }
            }
            rowNum++;
        }

        return newAssociations;
    }

    private Collection<RiskAllele> createLocusRiskAlleles(String strongestAllele,
                                                          String snp,
                                                          String proxy,
                                                          String riskFrequency,
                                                          String snpStatus,
                                                          String delimiter) {


        Collection<RiskAllele> locusRiskAlleles = new ArrayList<>();
        // For our list of snps, proxies and risk alleles separate by delimiter
        List<String> snps = new ArrayList<>();
        String[] separatedSnps = snp.split(delimiter);
        for (String separatedSnp : separatedSnps) {
            snps.add(separatedSnp.trim());
        }

        List<String> riskAlleles = new ArrayList<>();
        String[] separatedRiskAlleles = strongestAllele.split(delimiter);
        for (String separatedRiskAllele : separatedRiskAlleles) {
            riskAlleles.add(separatedRiskAllele.trim());
        }

        List<String> proxies = new ArrayList<>();
        String[] separatedProxies = proxy.split(delimiter);
        for (String separatedProxy : separatedProxies) {
            proxies.add(separatedProxy.trim());
        }

        // Value is only recorded for SNP interaction associations
        List<String> riskFrequencies = new ArrayList<>();
        Iterator<String> riskFrequencyIterator = null;
        if (riskFrequency != null) {
            String[] separatedRiskFrequencies = riskFrequency.split(delimiter);
            for (String separatedRiskFrequency : separatedRiskFrequencies) {
                riskFrequencies.add(separatedRiskFrequency.trim());
            }
            riskFrequencyIterator = riskFrequencies.iterator();
        }

        // Snp status
        List<String> snpStatuses = new ArrayList<>();
        Iterator<String> snpStatusIterator = null;
        if (snpStatus != null) {
            String[] separatedSnpStatuses = snpStatus.split(delimiter);
            for (String separatedSnpStatus : separatedSnpStatuses) {
                snpStatuses.add(separatedSnpStatus.trim());
            }
            snpStatusIterator = snpStatuses.iterator();
        }

        Iterator<String> riskAlleleIterator = riskAlleles.iterator();
        Iterator<String> snpIterator = snps.iterator();
        Iterator<String> proxyIterator = proxies.iterator();

        // Loop through our risk alleles
        if (riskAlleles.size() == snps.size()) {

            while (riskAlleleIterator.hasNext()) {

                String snpValue = snpIterator.next().trim();
                String riskAlleleValue = riskAlleleIterator.next().trim();
                String proxyValue = proxyIterator.next().trim();

                SingleNucleotidePolymorphism newSnp = lociAttributesService.createSnp(snpValue);

                // Create a new risk allele and assign newly created snp
                RiskAllele newRiskAllele = lociAttributesService.createRiskAllele(riskAlleleValue, newSnp);

                // Check for proxies and if we have one create a proxy snp
                Collection<SingleNucleotidePolymorphism> newRiskAlleleProxies = new ArrayList<>();
                if (proxyValue.contains(":")) {
                    String[] splitProxyValues = proxyValue.split(":");

                    for (String splitProxyValue : splitProxyValues) {
                        SingleNucleotidePolymorphism proxySnp = lociAttributesService.createSnp(splitProxyValue.trim());
                        newRiskAlleleProxies.add(proxySnp);
                    }
                }

                else if (proxyValue.contains(",")) {
                    String[] splitProxyValues = proxyValue.split(",");

                    for (String splitProxyValue : splitProxyValues) {
                        SingleNucleotidePolymorphism proxySnp = lociAttributesService.createSnp(splitProxyValue.trim());
                        newRiskAlleleProxies.add(proxySnp);
                    }
                }

                else {
                    SingleNucleotidePolymorphism proxySnp = lociAttributesService.createSnp(proxyValue);
                    newRiskAlleleProxies.add(proxySnp);
                }
                newRiskAllele.setProxySnps(newRiskAlleleProxies);

                // If there is no curator entered value for risk allele frequency don't save
                String riskFrequencyValue = null;
                if (riskFrequencyIterator != null) {
                    riskFrequencyValue = riskFrequencyIterator.next().trim();
                }
                if (riskFrequencyValue != null) {
                    newRiskAllele.setRiskFrequency(riskFrequencyValue);
                }

                // Handle snp statuses, these should only apply to SNP interaction associations
                String snpStatusValue = null;
                if (snpStatusIterator != null) {
                    snpStatusValue = snpStatusIterator.next().trim();
                }

                if (snpStatus != null && !snpStatus.equalsIgnoreCase("NR")) {
                    if (snpStatusValue.contains("GW") || snpStatusValue.contains("gw")) {
                        newRiskAllele.setGenomeWide(true);
                    }
                    if (snpStatusValue.contains("LL") || snpStatusValue.contains("ll")) {
                        newRiskAllele.setLimitedList(true);
                    }
                }

                locusRiskAlleles.add(newRiskAllele);
            }
        }
        else {
            getLog().error("Mismatched number of snps and risk alleles");
        }

        return locusRiskAlleles;
    }

    private Collection<Gene> createLocusGenes(String authorReportedGene, String delimiter) {

        String[] genes = authorReportedGene.split(delimiter);
        Collection<String> genesToCreate = new ArrayList<>();

        for (String gene : genes) {
            String trimmedGene = gene.trim();
            genesToCreate.add(trimmedGene);
        }

        return lociAttributesService.createGene(genesToCreate);
    }

    private Collection<EfoTrait> getEfoTraitsFromRepository(Collection<String> efoUris) {
        Collection<EfoTrait> efoTraits = new ArrayList<>();
        for (String uri : efoUris) {
            String fullUri;
            if (uri.contains("EFO")) {
                fullUri = "http://www.ebi.ac.uk/efo/".concat(uri);
            }
            else if (uri.contains("Orphanet")) {
                fullUri = "http://www.orpha.net/ORDO/".concat(uri);
            }
            else {
                fullUri = "http://purl.obolibrary.org/obo/".concat(uri);
            }

            Collection<EfoTrait> traits = efoTraitRepository.findByUri(fullUri);

            for (EfoTrait trait : traits) {
                efoTraits.add(trait);
            }
        }
        return efoTraits;
    }
}