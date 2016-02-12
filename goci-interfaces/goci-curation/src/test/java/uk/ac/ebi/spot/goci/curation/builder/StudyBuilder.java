package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;
import java.util.Date;

public class StudyBuilder {

    private Study study = new Study();

    public StudyBuilder setId(Long id) {
        study.setId(id);
        return this;
    }

    public StudyBuilder setAuthor(String author) {
        study.setAuthor(author);
        return this;
    }

    public StudyBuilder setPublicationDate(Date publicationDate) {
        study.setPublicationDate(publicationDate);
        return this;
    }

    public StudyBuilder setPublication(String publication) {
        study.setPublication(publication);
        return this;
    }

    public StudyBuilder setTitle(String title) {
        study.setTitle(title);
        return this;
    }

    public StudyBuilder setInitialSampleSize(String initialSampleSize) {
        study.setInitialSampleSize(initialSampleSize);
        return this;
    }

    public StudyBuilder setReplicateSampleSize(String replicateSampleSize) {
        study.setReplicateSampleSize(replicateSampleSize);
        return this;
    }

    public StudyBuilder setPlatform(String platform) {
        study.setPlatform(platform);
        return this;
    }

    public StudyBuilder setPubmedId(String pubmedId) {
        study.setPubmedId(pubmedId);
        return this;
    }

    public StudyBuilder setCnv(Boolean cnv) {
        study.setCnv(cnv);
        return this;
    }

    public StudyBuilder setGxe(Boolean gxe) {
        study.setGxe(gxe);
        return this;
    }

    public StudyBuilder setGxg(Boolean gxg) {
        study.setGxg(gxg);
        return this;
    }

    public StudyBuilder setDiseaseTrait(DiseaseTrait diseaseTrait) {
        study.setDiseaseTrait(diseaseTrait);
        return this;
    }

    public StudyBuilder setEfoTraits(Collection<EfoTrait> efoTraits) {
        study.setEfoTraits(efoTraits);
        return this;
    }

    public StudyBuilder setSingleNucleotidePolymorphisms(Collection<SingleNucleotidePolymorphism> singleNucleotidePolymorphisms) {
       study.setSingleNucleotidePolymorphisms(singleNucleotidePolymorphisms);
        return this;
    }

    public StudyBuilder setEthnicities(Collection<Ethnicity> ethnicities) {
        study.setEthnicities(ethnicities);
        return this;
    }

    public StudyBuilder setHousekeeping(Housekeeping housekeeping) {
        study.setHousekeeping(housekeeping);
        return this;
    }

    public Study build() {
        return study;
    }
}