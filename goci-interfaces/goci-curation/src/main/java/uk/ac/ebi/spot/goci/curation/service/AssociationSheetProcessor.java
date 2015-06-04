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

    private Collection<Association> newAssociations = new ArrayList<>();

    // Services
    private AssociationCalculationService associationCalculationService;
    private LociAttributesService lociAttributesService;

    // Repository
    private EfoTraitRepository efoTraitRepository;

    // Logging
    private Logger log = LoggerFactory.getLogger(getClass());
    private String logMessage;
    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationSheetProcessor(AssociationCalculationService associationCalculationService,
                                     LociAttributesService lociAttributesService,
                                     EfoTraitRepository efoTraitRepository) {
        this.associationCalculationService = associationCalculationService;
        this.lociAttributesService = lociAttributesService;
        this.efoTraitRepository = efoTraitRepository;
    }


    // Read and parse uploaded spreadsheet
    public void readSnpAssociations(XSSFSheet sheet) {
        boolean done = false;
        int rowNum = 1;

        while (!done) {
            XSSFRow row = sheet.getRow(rowNum);

            if (row == null) {
                done = true;
                getLog().debug("Last row read");
                logMessage = "All spreadsheet data processed successfully";
            }
            else {

                // Get gene values
                String authorReportedGene = null;
                if (row.getCell(0, row.RETURN_BLANK_AS_NULL) != null) {
                    authorReportedGene = row.getCell(0).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Gene' in row " + rowNum + 1 + "\n";

                }
                else {
                    getLog().debug("Gene is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Gene' in row " + rowNum + 1 + "\n";
                }

                // Get Strongest SNP-Risk Allele
                String strongestAllele = null;
                if (row.getCell(1, row.RETURN_BLANK_AS_NULL) != null) {
                    strongestAllele = row.getCell(1).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Risk allele' in row " + rowNum + 1 + "\n";

                }
                else {
                    getLog().debug("Risk allele is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Risk allele' in row " + rowNum + 1 + "\n";
                }


                // Get SNP
                String snp = null;
                if (row.getCell(2, row.RETURN_BLANK_AS_NULL) != null) {
                    snp = row.getCell(2).getRichStringCellValue().getString();
                    logMessage = "Error in field 'SNP' in row " + rowNum + 1 + "\n";

                }
                else {
                    getLog().debug("SNP is null in row " + row.getRowNum());
                    logMessage = "Error in field 'SNP' in row " + rowNum + 1 + "\n";

                }

                // Get Proxy SNP
                String proxy = null;
                if (row.getCell(3, row.RETURN_BLANK_AS_NULL) != null) {
                    proxy = row.getCell(3).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Proxy SNP' in row " + rowNum + 1 + "\n";

                }
                else {
                    getLog().debug("SNP is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Proxy SNP' in row " + rowNum + 1 + "\n";

                }

                // Get Risk Allele Frequency, will contain multiple values for haplotype or interaction
                String riskFrequency = null;
                if (row.getCell(4, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell risk = row.getCell(4);
                    switch (risk.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            riskFrequency = risk.getRichStringCellValue().getString();
                            logMessage = "Error in field 'Risk Frequency' in row " + rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            riskFrequency = Double.toString(risk.getNumericCellValue());
                            logMessage =
                                    "Error in field 'Risk Allele Frequency in Controls' in row " + rowNum + 1 + "\n";

                            break;
                    }
                }
                else {
                    getLog().debug("RF is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Risk Allele Frequency in Controls' in row " + rowNum + 1 + "\n";
                }

                // Will be a single value that applies to association
                String associationRiskFrequency = null;
                if (row.getCell(5, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell risk = row.getCell(5);
                    switch (risk.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            associationRiskFrequency = risk.getRichStringCellValue().getString();
                            logMessage = "Error in field 'Association Risk Frequency' in row " + rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            associationRiskFrequency = Double.toString(risk.getNumericCellValue());
                            logMessage = "Error in field 'Association Risk Frequency' in row " + rowNum + 1 + "\n";

                            break;
                    }
                }
                else {
                    getLog().debug("RF is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Risk Frequency' in row " + rowNum + 1 + "\n";
                }

                // Get P-value mantissa	and P-value exponent
                Integer pvalueMantissa = null;
                Integer pvalueExponent = null;

                if (row.getCell(6, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell mant = row.getCell(6);
                    switch (mant.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            pvalueMantissa = null;
                            logMessage = "Error in field 'pvalue mantissa' in row " + rowNum + 1 + "\n";

                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalueMantissa = (int) mant.getNumericCellValue();
                            logMessage = "Error in field 'pvalue mantissa' in row " + rowNum + 1 + "\n";

                            break;
                    }
                }
                else {
                    pvalueMantissa = null;
                    getLog().debug("pvalue mantissa is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvalue mantissa' in row " + rowNum + 1 + "\n";

                }

                if (row.getCell(7, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell expo = row.getCell(7);
                    switch (expo.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            pvalueExponent = null;
                            logMessage = "Error in field 'pvalue exponent' in row " + rowNum + 1 + "\n";

                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalueExponent = (int) expo.getNumericCellValue();
                            logMessage = "Error in field 'pvalue exponent' in row " + rowNum + 1 + "\n";

                            break;
                    }
                }
                else {
                    pvalueExponent = null;
                    getLog().debug("pvalue exponent is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvalue exponent' in row " + rowNum + 1 + "\n";
                }

                // Get P-value (Text)
                String pvalueText;
                if (row.getCell(8, row.RETURN_BLANK_AS_NULL) != null) {
                    pvalueText = row.getCell(8).getRichStringCellValue().getString();
                    logMessage = "Error in field 'pvaluetxt' in row " + rowNum + 1 + "\n";

                }
                else {
                    pvalueText = null;
                    getLog().debug("pvalue text is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvaluetxt' in row " + rowNum + 1 + "\n";
                }

                // Get OR per copy or beta (Num)
                Float orPerCopyNum = null;
                if (row.getCell(9, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell or = row.getCell(9);
                    switch (or.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyNum = null;
                            logMessage = "Error in field 'OR' in row " + rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyNum = (float) or.getNumericCellValue();
                            logMessage = "Error in field 'OR' in row " + rowNum + 1 + "\n";
                            break;
                    }
                }
                else {
                    orPerCopyNum = null;
                    getLog().debug("OR is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR' in row " + rowNum + 1 + "\n";
                }

                // Get OR entered (reciprocal)
                Float orPerCopyRecip = null;
                if (row.getCell(10, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell recip = row.getCell(10);
                    switch (recip.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyRecip = null;
                            logMessage = "Error in field 'OR recip' in row " + rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyRecip = (float) recip.getNumericCellValue();
                            logMessage = "Error in field 'OR recip' in row " + rowNum + 1 + "\n";
                            break;
                    }
                }
                else {
                    orPerCopyRecip = null;
                    getLog().debug("OR recip is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR recip' in row " + rowNum + 1 + "\n";

                }


                String orType;
                if (row.getCell(11, row.RETURN_BLANK_AS_NULL) != null) {
                    orType = row.getCell(11).getRichStringCellValue().getString();
                    logMessage = "Error in field 'OR type' in row " + rowNum + 1 + "\n";
                }
                else {
                    orType = null;
                    getLog().debug("OR type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR type' in row " + rowNum + 1 + "\n";
                }

                // Get Multi-SNP Haplotype value
                String multiSnpHaplotype;
                if (row.getCell(12, row.RETURN_BLANK_AS_NULL) != null) {
                    multiSnpHaplotype = row.getCell(12).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Multi-SNP Haplotype' in row " + rowNum + 1 + "\n";
                }
                else {
                    multiSnpHaplotype = null;
                    getLog().debug("OR type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Multi-SNP Haplotype' in row " + rowNum + 1 + "\n";
                }

                // Get SNP interaction value
                String snpInteraction;
                if (row.getCell(13, row.RETURN_BLANK_AS_NULL) != null) {
                    snpInteraction = row.getCell(13).getRichStringCellValue().getString();
                    logMessage = "Error in field 'SNP:SNP interaction' in row " + rowNum + 1 + "\n";
                }
                else {
                    snpInteraction = null;
                    getLog().debug("OR type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'SNP:SNP interaction' in row " + rowNum + 1 + "\n";
                }

                // Get Confidence Interval/Range
                String orPerCopyRange;
                if (row.getCell(14, row.RETURN_BLANK_AS_NULL) != null) {
                    orPerCopyRange = row.getCell(14).getRichStringCellValue().getString();
                    logMessage = "Error in field 'CI' in row " + rowNum + 1 + "\n";
                }
                else {
                    orPerCopyRange = null;
                    getLog().debug("CI is null in row " + row.getRowNum());
                    logMessage = "Error in field 'CI' in row " + rowNum + 1 + "\n";
                }

                String orPerCopyRecipRange;
                if (row.getCell(15, row.RETURN_BLANK_AS_NULL) != null) {
                    orPerCopyRecipRange = row.getCell(15).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Reciprocal CI' in row " + rowNum + 1 + "\n";
                }
                else {
                    orPerCopyRecipRange = null;
                    getLog().debug("CI is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Reciprocal CI' in row " + rowNum + 1 + "\n";
                }

                // Get Beta unit and direction/description
                String orPerCopyUnitDescr;
                if (row.getCell(16) != null) {
                    orPerCopyUnitDescr = row.getCell(16).getRichStringCellValue().getString();
                    logMessage = "Error in field 'OR direction' in row " + rowNum + 1 + "\n";
                }
                else {
                    orPerCopyUnitDescr = null;
                    getLog().debug("OR direction is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR direction' in row " + rowNum + 1 + "\n";
                }

                // Get standard error
                Float orPerCopyStdError = null;
                if (row.getCell(17, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell std = row.getCell(17);
                    switch (std.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyStdError = null;
                            logMessage = "Error in field 'Standard Error' in row " + rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyStdError = (float) std.getNumericCellValue();
                            logMessage = "Error in field 'Standard Error' in row " + rowNum + 1 + "\n";
                            break;
                    }
                }
                else {
                    orPerCopyStdError = null;
                    getLog().debug("SE is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Standard Error' in row " + rowNum + 1 + "\n";
                }

                // Get SNP type (novel / known)
                String snpType;
                if (row.getCell(18, row.RETURN_BLANK_AS_NULL) != null) {
                    snpType = row.getCell(18).getRichStringCellValue().getString().toLowerCase();
                    logMessage = "Error in field 'SNP type' in row " + rowNum + 1 + "\n";
                }
                else {
                    snpType = null;
                    getLog().debug("SNP type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'SNP type' in row " + rowNum + 1 + "\n";
                }

                String efoTrait;
                if (row.getCell(19, row.RETURN_BLANK_AS_NULL) != null) {
                    efoTrait = row.getCell(19).getRichStringCellValue().getString();
                    logMessage = "Error in field 'EFO traits' in row " + rowNum + 1 + "\n";
                }
                else {
                    efoTrait = null;
                    getLog().debug("EFO trait is null in row " + row.getRowNum());
                    logMessage = "Error in field 'EFO trait' in row " + rowNum + 1 + "\n";
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
                            uri.trim();
                            efoUris.add(uri);
                        }

                        Collection<EfoTrait> efoTraits = getEfoTraitsFromRepository(efoUris);

                        newAssociation.setEfoTraits(efoTraits);
                    }

                    // Set values common to all association types
                    newAssociation.setRiskFrequency(associationRiskFrequency);
                    newAssociation.setPvalueMantissa(pvalueMantissa);
                    newAssociation.setPvalueExponent(pvalueExponent);
                    newAssociation.setPvalueText(pvalueText);
                    newAssociation.setOrPerCopyRecip(orPerCopyRecip);
                    newAssociation.setOrPerCopyStdError(orPerCopyStdError);
                    newAssociation.setOrPerCopyRecipRange(orPerCopyRecipRange);
                    newAssociation.setOrPerCopyUnitDescr(orPerCopyUnitDescr);
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
                        orPerCopyRange = associationCalculationService.reverseCI(orPerCopyRecipRange);
                        newAssociation.setOrPerCopyRange(orPerCopyRange);
                    }
                    else if ((orPerCopyRange == null) && (orPerCopyStdError != null)) {
                        orPerCopyRange = associationCalculationService.setRange(orPerCopyStdError, orPerCopyNum);
                        newAssociation.setOrPerCopyRange(orPerCopyRange);
                    }
                    else {
                        newAssociation.setOrPerCopyRange(orPerCopyRange);
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

                    String delimiter = "";
                    Collection<Locus> loci = new ArrayList<>();

                    if (newAssociation.getSnpInteraction()) {
                        delimiter = "x";

                        // For SNP interaction studies we need to create a locus per risk allele
                        // Handle curator entered risk allele
                        Collection<RiskAllele> locusRiskAlleles =
                                createLocusRiskAlleles(strongestAllele, snp, proxy, riskFrequency, delimiter);

                        for (RiskAllele riskAllele : locusRiskAlleles) {
                            Locus locus = new Locus();
                            Collection<RiskAllele> currentLocusRiskAlleles = new ArrayList<>();
                            currentLocusRiskAlleles.add(riskAllele);

                            locus.setDescription("SNP x SNP interaction");
                            loci.add(locus);
                        }

                        // Add genes to relevant loci, split by 'x' delimiter first
                        Collection<Locus> lociWithAddedGenes = new ArrayList<>();
                        String[] genes = authorReportedGene.split(delimiter);
                        for (Locus locus : loci) {
                            for (String gene : genes) {
                                Collection<Gene> locusGenes = createLocusGenes(gene, ",");
                                locus.setAuthorReportedGenes(locusGenes);
                            }
                            lociWithAddedGenes.add(locus);
                        }

                        loci = lociWithAddedGenes;
                    }

                    // Handle multi-snp and standard snp
                    else {
                        delimiter = ",";

                        // For multi-snp and standard snps we assume their is only one locus
                        Locus locus = new Locus();

                        // Handle curator entered genes
                        Collection<Gene> locusGenes = createLocusGenes(authorReportedGene, delimiter);
                        locus.setAuthorReportedGenes(locusGenes);

                        // Handle curator entered risk allele
                        Collection<RiskAllele> locusRiskAlleles =
                                createLocusRiskAlleles(strongestAllele, snp, proxy, riskFrequency, delimiter);
                        locus.setStrongestRiskAlleles(locusRiskAlleles);

                        // Set locus attributes
                        Integer haplotypeCount = locusRiskAlleles.size();
                        if (haplotypeCount > 1) {
                            locus.setHaplotypeSnpCount(haplotypeCount);
                            locus.setDescription(String.valueOf(haplotypeCount) + "-SNP haplotype");
                        }

                        else {
                            locus.setDescription("Single variant");
                        }
                        loci.add(locus);
                    }

                    newAssociation.setLoci(loci);

                    // Add all newly created associations to collection
                    newAssociations.add(newAssociation);
                }
            }
            rowNum++;
        }
    }

    private Collection<RiskAllele> createLocusRiskAlleles(String strongestAllele,
                                                          String snp,
                                                          String proxy, String riskFrequency,
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

        List<String> riskFrequencies = new ArrayList<>();
        String[] separatedRiskFrequencies = riskFrequency.split(delimiter);
        for (String separatedRiskFrequency : separatedRiskFrequencies) {
            riskFrequencies.add(separatedRiskFrequency.trim());
        }

        Iterator<String> riskAlleleIterator = riskAlleles.iterator();
        Iterator<String> snpIterator = snps.iterator();
        Iterator<String> proxyIterator = proxies.iterator();
        Iterator<String> riskFrequencyIterator = riskFrequencies.iterator();

        // Loop through our risk alleles
        if (riskAlleles.size() == snps.size()) {

            while (riskAlleleIterator.hasNext()) {

                String snpValue = snpIterator.next().trim();
                String riskAlleleValue = riskAlleleIterator.next().trim();
                String proxyValue = proxyIterator.next().trim();
                String riskFrequencyValue = riskFrequencyIterator.next().trim();

                SingleNucleotidePolymorphism newSnp = lociAttributesService.createSnp(snpValue);

                // Create a new risk allele and assign newly created snp
                RiskAllele newRiskAllele = lociAttributesService.createRiskAllele(riskAlleleValue, newSnp);

                // Check for a proxy and if we have one create a proxy snp
                SingleNucleotidePolymorphism proxySnp = lociAttributesService.createSnp(proxyValue);
                newRiskAllele.setProxySnp(proxySnp);

                // If there is no curator entered value don't save
                if (!riskFrequencyValue.equalsIgnoreCase("NR")) {
                    newRiskAllele.setRiskFrequency(riskFrequencyValue);
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
            gene.trim();

            if (gene.contains(",")) {

            }

            genesToCreate.add(gene);
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

    public Collection<Association> getAllAssociations() {
        return newAssociations;
    }

    public String getLogMessage() {
        return logMessage;
    }


}

