package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Platform;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 12/02/2016.
 *
 * @author emma
 *         <p>
 *         Study builder used in testing
 */
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

    public StudyBuilder setGenomewideArray(Boolean genomewideArray) {
        study.setGenomewideArray(genomewideArray);
        return this;
    }

    public StudyBuilder setTargetedArray(Boolean targetedArray) {
        study.setTargetedArray(targetedArray);
        return this;
    }

    public StudyBuilder setFullPvalueSet(Boolean fullPvalueSet) {
        study.setFullPvalueSet(fullPvalueSet);
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

    public StudyBuilder setAncestries(Collection<Ancestry> ancestries) {
        study.setAncestries(ancestries);
        return this;
    }

    public StudyBuilder setHousekeeping(Housekeeping housekeeping) {
        study.setHousekeeping(housekeeping);
        return this;
    }

    public StudyBuilder setAssociations(Collection<Association> associations) {
        study.setAssociations(associations);
        return this;
    }

    public StudyBuilder setPooled(Boolean pooled) {
        study.setPooled(pooled);
        return this;
    }

    public StudyBuilder setSnpCount(Integer snpCount) {
        study.setSnpCount(snpCount);
        return this;
    }

    public StudyBuilder setQualifer(String qualifer) {
        study.setQualifier(qualifer);
        return this;
    }

    public StudyBuilder setImputed(Boolean imputed) {
        study.setImputed(imputed);
        return this;
    }

    public StudyBuilder setStudyDesignComment(String studyDesignComment) {
        study.setStudyDesignComment(studyDesignComment);
        return this;
    }

    public StudyBuilder setPlatforms(Collection<Platform> platforms) {
        study.setPlatforms(platforms);
        return this;
    }

    public StudyBuilder setEvents(Collection<Event> events) {
        study.setEvents(events);
        return this;
    }

    public Study build() {
        return study;
    }
}