package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by emma on 17/02/15.
 *
 * @author emma
 */
@Entity
public class CatalogSummaryView {

    @Id
    private Long id;

    private Date studyAddedDate;

    private String pubmedId;

    private String author;

    private Date publicationDate;

    private String journal;

    private String link;

    private String study;

    private String diseaseTrait;

    private String efoTrait;

    private String efoUri;

    private String initialSampleDescription;

    private String replicateSampleDescription;

    private String region;

    private String chromosomeName;

    private String chromosomePosition;

    private String reportedGene;

    private String entrezMappedGene;

    private String entrezMappedGeneId;

    private String ensemblMappedGene;

    private String ensemblMappedGeneId;

    private String entrezUpstreamMappedGene;

    private String entrezUpstreamGeneId;

    private Integer entrezUpstreamGeneDist;

    private String ensemblUpstreamMappedGene;

    private String ensemblUpstreamGeneId;

    private Integer ensemblUpstreamGeneDist;

    private String entrezDownstreamMappedGene;

    private String entrezDownstreamGeneId;

    private Integer entrezDownstreamGeneDist;

    private String ensemblDownstreamMappedGene;

    private String ensemblDownstreamGeneId;

    private Integer ensemblDownstreamGeneDist;

    private String strongestSnpRiskAllele;

    private String snpRsid;

    private Boolean merged;

    private Long snpId;

    private String context;

    private Boolean isIntergenicEntrez;

    private Boolean isIntergenicEnsembl;

    private String riskAlleleFrequency;

    private Integer pValueMantissa;

    private Integer pValueExponent;

    private String pValueQualifier;

    private Float orBeta;

    private String ci;

    private String ciQualifier;

    private String platform;

    private Boolean cnv;

    private Long associationId;

    private Long studyId;

    private Date catalogPublishDate;

    private Date catalogUnpublishDate;

    private String curationStatus;

    // JPA no-args constructor
    public CatalogSummaryView() {

    }

    public CatalogSummaryView(Long id,
                              Date studyAddedDate,
                              String pubmedId,
                              String author,
                              Date publicationDate,
                              String journal,
                              String link,
                              String study,
                              String diseaseTrait,
                              String efoTrait,
                              String efoUri,
                              String initialSampleDescription,
                              String replicateSampleDescription,
                              String region,
                              String chromosomeName,
                              String chromosomePosition,
                              String reportedGene,
                              String entrezMappedGene,
                              String entrezMappedGeneId,
                              String ensemblMappedGene,
                              String ensemblMappedGeneId,
                              String entrezUpstreamMappedGene,
                              String entrezUpstreamGeneId,
                              Integer entrezUpstreamGeneDist,
                              String ensemblUpstreamMappedGene,
                              String ensemblUpstreamGeneId,
                              Integer ensemblUpstreamGeneDist,
                              String entrezDownstreamMappedGene,
                              String entrezDownstreamGeneId,
                              Integer entrezDownstreamGeneDist,
                              String ensemblDownstreamMappedGene,
                              String ensemblDownstreamGeneId,
                              Integer ensemblDownstreamGeneDist,
                              String strongestSnpRiskAllele,
                              String snpRsid,
                              Boolean merged,
                              Long snpId,
                              String context,
                              Boolean isIntergenicEntrez,
                              Boolean isIntergenicEnsembl,
                              String riskAlleleFrequency,
                              Integer pValueMantissa,
                              Integer pValueExponent,
                              String pValueQualifier,
                              Float orBeta,
                              String ci,
                              String ciQualifier,
                              String platform,
                              Boolean cnv,
                              Long associationId,
                              Long studyId,
                              Date catalogPublishDate, Date catalogUnpublishDate, String curationStatus) {
        this.id = id;
        this.studyAddedDate = studyAddedDate;
        this.pubmedId = pubmedId;
        this.author = author;
        this.publicationDate = publicationDate;
        this.journal = journal;
        this.link = link;
        this.study = study;
        this.diseaseTrait = diseaseTrait;
        this.efoTrait = efoTrait;
        this.efoUri = efoUri;
        this.initialSampleDescription = initialSampleDescription;
        this.replicateSampleDescription = replicateSampleDescription;
        this.region = region;
        this.chromosomeName = chromosomeName;
        this.chromosomePosition = chromosomePosition;
        this.reportedGene = reportedGene;
        this.entrezMappedGene = entrezMappedGene;
        this.entrezMappedGeneId = entrezMappedGeneId;
        this.ensemblMappedGene = ensemblMappedGene;
        this.ensemblMappedGeneId = ensemblMappedGeneId;
        this.entrezUpstreamMappedGene = entrezUpstreamMappedGene;
        this.entrezUpstreamGeneId = entrezUpstreamGeneId;
        this.entrezUpstreamGeneDist = entrezUpstreamGeneDist;
        this.ensemblUpstreamMappedGene = ensemblUpstreamMappedGene;
        this.ensemblUpstreamGeneId = ensemblUpstreamGeneId;
        this.ensemblUpstreamGeneDist = ensemblUpstreamGeneDist;
        this.entrezDownstreamMappedGene = entrezDownstreamMappedGene;
        this.entrezDownstreamGeneId = entrezDownstreamGeneId;
        this.entrezDownstreamGeneDist = entrezDownstreamGeneDist;
        this.ensemblDownstreamMappedGene = ensemblDownstreamMappedGene;
        this.ensemblDownstreamGeneId = ensemblDownstreamGeneId;
        this.ensemblDownstreamGeneDist = ensemblDownstreamGeneDist;
        this.strongestSnpRiskAllele = strongestSnpRiskAllele;
        this.snpRsid = snpRsid;
        this.merged = merged;
        this.snpId = snpId;
        this.context = context;
        this.isIntergenicEntrez = isIntergenicEntrez;
        this.isIntergenicEnsembl = isIntergenicEnsembl;
        this.riskAlleleFrequency = riskAlleleFrequency;
        this.pValueMantissa = pValueMantissa;
        this.pValueExponent = pValueExponent;
        this.pValueQualifier = pValueQualifier;
        this.orBeta = orBeta;
        this.ci = ci;
        this.ciQualifier = ciQualifier;
        this.platform = platform;
        this.cnv = cnv;
        this.associationId = associationId;
        this.studyId = studyId;
        this.catalogPublishDate = catalogPublishDate;
        this.catalogUnpublishDate = catalogUnpublishDate;
        this.curationStatus = curationStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStudyAddedDate() {
        return studyAddedDate;
    }

    public void setStudyAddedDate(Date studyAddedDate) {
        this.studyAddedDate = studyAddedDate;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getDiseaseTrait() {
        return diseaseTrait;
    }

    public void setDiseaseTrait(String diseaseTrait) {
        this.diseaseTrait = diseaseTrait;
    }

    public String getEfoTrait() {
        return efoTrait;
    }

    public void setEfoTrait(String efoTrait) {
        this.efoTrait = efoTrait;
    }

    public String getEfoUri() {
        return efoUri;
    }

    public void setEfoUri(String efoUri) {
        this.efoUri = efoUri;
    }

    public String getInitialSampleDescription() {
        return initialSampleDescription;
    }

    public void setInitialSampleDescription(String initialSampleDescription) {
        this.initialSampleDescription = initialSampleDescription;
    }

    public String getReplicateSampleDescription() {
        return replicateSampleDescription;
    }

    public void setReplicateSampleDescription(String replicateSampleDescription) {
        this.replicateSampleDescription = replicateSampleDescription;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getChromosomeName() {
        return chromosomeName;
    }

    public void setChromosomeName(String chromosomeName) {
        this.chromosomeName = chromosomeName;
    }

    public String getChromosomePosition() {
        return chromosomePosition;
    }

    public void setChromosomePosition(String chromosomePosition) {
        this.chromosomePosition = chromosomePosition;
    }

    public String getReportedGene() {
        return reportedGene;
    }

    public void setReportedGene(String reportedGene) {
        this.reportedGene = reportedGene;
    }

    public String getEntrezMappedGene() {
        return entrezMappedGene;
    }

    public void setEntrezMappedGene(String entrezMappedGene) {
        this.entrezMappedGene = entrezMappedGene;
    }

    public String getEntrezMappedGeneId() {
        return entrezMappedGeneId;
    }

    public void setEntrezMappedGeneId(String entrezMappedGeneId) {
        this.entrezMappedGeneId = entrezMappedGeneId;
    }

    public String getEnsemblMappedGene() {
        return ensemblMappedGene;
    }

    public void setEnsemblMappedGene(String ensemblMappedGene) {
        this.ensemblMappedGene = ensemblMappedGene;
    }

    public String getEnsemblMappedGeneId() {
        return ensemblMappedGeneId;
    }

    public void setEnsemblMappedGeneId(String ensemblMappedGeneId) {
        this.ensemblMappedGeneId = ensemblMappedGeneId;
    }

    public String getEntrezUpstreamMappedGene() {
        return entrezUpstreamMappedGene;
    }

    public void setEntrezUpstreamMappedGene(String entrezUpstreamMappedGene) {
        this.entrezUpstreamMappedGene = entrezUpstreamMappedGene;
    }

    public String getEntrezUpstreamGeneId() {
        return entrezUpstreamGeneId;
    }

    public void setEntrezUpstreamGeneId(String entrezUpstreamGeneId) {
        this.entrezUpstreamGeneId = entrezUpstreamGeneId;
    }

    public Integer getEntrezUpstreamGeneDist() {
        return entrezUpstreamGeneDist;
    }

    public void setEntrezUpstreamGeneDist(Integer entrezUpstreamGeneDist) {
        this.entrezUpstreamGeneDist = entrezUpstreamGeneDist;
    }

    public String getEnsemblUpstreamMappedGene() {
        return ensemblUpstreamMappedGene;
    }

    public void setEnsemblUpstreamMappedGene(String ensemblUpstreamMappedGene) {
        this.ensemblUpstreamMappedGene = ensemblUpstreamMappedGene;
    }

    public String getEnsemblUpstreamGeneId() {
        return ensemblUpstreamGeneId;
    }

    public void setEnsemblUpstreamGeneId(String ensemblUpstreamGeneId) {
        this.ensemblUpstreamGeneId = ensemblUpstreamGeneId;
    }

    public Integer getEnsemblUpstreamGeneDist() {
        return ensemblUpstreamGeneDist;
    }

    public void setEnsemblUpstreamGeneDist(Integer ensemblUpstreamGeneDist) {
        this.ensemblUpstreamGeneDist = ensemblUpstreamGeneDist;
    }

    public String getEntrezDownstreamMappedGene() {
        return entrezDownstreamMappedGene;
    }

    public void setEntrezDownstreamMappedGene(String entrezDownstreamMappedGene) {
        this.entrezDownstreamMappedGene = entrezDownstreamMappedGene;
    }

    public String getEntrezDownstreamGeneId() {
        return entrezDownstreamGeneId;
    }

    public void setEntrezDownstreamGeneId(String entrezDownstreamGeneId) {
        this.entrezDownstreamGeneId = entrezDownstreamGeneId;
    }

    public Integer getEntrezDownstreamGeneDist() {
        return entrezDownstreamGeneDist;
    }

    public void setEntrezDownstreamGeneDist(Integer entrezDownstreamGeneDist) {
        this.entrezDownstreamGeneDist = entrezDownstreamGeneDist;
    }

    public String getEnsemblDownstreamMappedGene() {
        return ensemblDownstreamMappedGene;
    }

    public void setEnsemblDownstreamMappedGene(String ensemblDownstreamMappedGene) {
        this.ensemblDownstreamMappedGene = ensemblDownstreamMappedGene;
    }

    public String getEnsemblDownstreamGeneId() {
        return ensemblDownstreamGeneId;
    }

    public void setEnsemblDownstreamGeneId(String ensemblDownstreamGeneId) {
        this.ensemblDownstreamGeneId = ensemblDownstreamGeneId;
    }

    public Integer getEnsemblDownstreamGeneDist() {
        return ensemblDownstreamGeneDist;
    }

    public void setEnsemblDownstreamGeneDist(Integer ensemblDownstreamGeneDist) {
        this.ensemblDownstreamGeneDist = ensemblDownstreamGeneDist;
    }

    public String getStrongestSnpRiskAllele() {
        return strongestSnpRiskAllele;
    }

    public void setStrongestSnpRiskAllele(String strongestSnpRiskAllele) {
        this.strongestSnpRiskAllele = strongestSnpRiskAllele;
    }

    public String getSnpRsid() {
        return snpRsid;
    }

    public void setSnpRsid(String snpRsid) {
        this.snpRsid = snpRsid;
    }

    public Boolean isMerged() {
        return merged;
    }

    public void setMerged(Boolean merged) {
        this.merged = merged;
    }

    public Long getSnpId() {
        return snpId;
    }

    public void setSnpId(Long snpId) {
        this.snpId = snpId;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Boolean isIntergenicEntrez() {
        return isIntergenicEntrez;
    }

    public void setIsIntergenicEntrez(Boolean isIntergenicEntrez) {
        this.isIntergenicEntrez = isIntergenicEntrez;
    }

    public Boolean isIntergenicEnsembl() {
        return isIntergenicEnsembl;
    }

    public void setIsIntergenicEnsembl(Boolean isIntergenicEnsembl) {
        this.isIntergenicEnsembl = isIntergenicEnsembl;
    }

    public String getRiskAlleleFrequency() {
        return riskAlleleFrequency;
    }

    public void setRiskAlleleFrequency(String riskAlleleFrequency) {
        this.riskAlleleFrequency = riskAlleleFrequency;
    }

    public Integer getpValueMantissa() {
        return pValueMantissa;
    }

    public void setpValueMantissa(Integer pValueMantissa) {
        this.pValueMantissa = pValueMantissa;
    }

    public Integer getpValueExponent() {
        return pValueExponent;
    }

    public void setpValueExponent(Integer pValueExponent) {
        this.pValueExponent = pValueExponent;
    }

    public String getpValueQualifier() {
        return pValueQualifier;
    }

    public void setpValueQualifier(String pValueQualifier) {
        this.pValueQualifier = pValueQualifier;
    }

    public Float getOrBeta() {
        return orBeta;
    }

    public void setOrBeta(Float orBeta) {
        this.orBeta = orBeta;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getCiQualifier() {
        return ciQualifier;
    }

    public void setCiQualifier(String ciQualifier) {
        this.ciQualifier = ciQualifier;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Boolean getCnv() {
        return cnv;
    }

    public void setCnv(Boolean cnv) {
        this.cnv = cnv;
    }

    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public Date getCatalogPublishDate() {
        return catalogPublishDate;
    }

    public void setCatalogPublishDate(Date catalogPublishDate) {
        this.catalogPublishDate = catalogPublishDate;
    }

    public Date getCatalogUnpublishDate() {
        return catalogUnpublishDate;
    }

    public void setCatalogUnpublishDate(Date catalogUnpublishDate) {
        this.catalogUnpublishDate = catalogUnpublishDate;
    }

    public String getCurationStatus() {
        return curationStatus;
    }

    public void setCurationStatus(String curationStatus) {
        this.curationStatus = curationStatus;
    }
}