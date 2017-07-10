package uk.ac.ebi.spot.goci.curation.component;
import org.springframework.data.jpa.domain.Specification;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Study;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by cinzia on 05/07/2017.
 */
public class StudySpecifications implements Specification<Study> {

    private final StudySearchFilter filters;

    public StudySpecifications(StudySearchFilter filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<Study> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        Class clazz = cq.getResultType();
        if (clazz.equals(Long.class) || clazz.equals(long.class)){
            return null;

        }

        //building the desired query
        root.fetch("diseaseTrait", JoinType.LEFT);
        root.fetch("housekeeping", JoinType.LEFT);
        //root.fetch("curator",JoinType.LEFT);
        cq.distinct(true);


        if (filters.getPubmedId() != null) {
            System.out.println("pubmedId Predicate");
            predicates.add(cb.like(cb.lower(root.get("pubmedId")), filters.getPubmedId().toLowerCase() + "%"));
        }

        if (filters.getAuthor() != null) {
            System.out.println("Author Predicate");
            predicates.add(cb.like(cb.lower(root.get("author")), filters.getAuthor().toLowerCase() + "%"));
        }


        if (filters.getStudyType() != null) {
            System.out.println("studyType Predicate");
            String studyColumn = convertStudyTypeToColumn(filters.getStudyType());
            if (studyColumn != null) {
                predicates.add(cb.equal(root.get(studyColumn), 1));
            }
        }


        if (filters.getCuratorSearchFilterId() != null) {
            System.out.println("Curator predicate");
            predicates.add(cb.equal(root.get("housekeeping").get("curator").get("id"), filters.getCuratorSearchFilterId()));
        }

        if (filters.getStatusSearchFilterId() != null) {
            //predicates.add(cb.like(cb.lower(root.get(Study_.title)), example.getTitle().toLowerCase() + "%"));
            //predicates.add(cb.equals(cb.lower(root.get("housekeeping")), filters.getCuratorSearchFilterId() + "%"));
            //Path<Long> path = root.join("housekeeoping").<Long>get("id");
            //predicates.add(cb.equal(root.get("housekeeping").get("curator").get("id"), filters.getCuratorSearchFilterId()));
            predicates.add(cb.equal(root.get("housekeeping").get("curationStatus").get("id"), filters.getStatusSearchFilterId()));

            System.out.println("Status predicate");
        }

        /*
        if (example.getAuthor() != null) {
            //predicates.add(cb.like(cb.lower(root.get(Study_.title)), example.getTitle().toLowerCase() + "%"));
            predicates.add(cb.like(cb.lower(root.get("author")), example.getAuthor().toLowerCase() + "%"));
            System.out.println("predicato");
        }

        if (example.getDiseaseTrait().getTrait() != null) {
            //predicates.add(cb.like(cb.lower(root.get(Study_.title)), example.getTitle().toLowerCase() + "%"));
            predicates.add(cb.like(cb.lower(root.get("diseaseTrait").get("trait")), example.getDiseaseTrait().getTrait().toLowerCase() + "%"));
            System.out.println("predicato");
        }
*/
        return andTogether(predicates, cb);
    }

    private Predicate andTogether(List<Predicate> predicates, CriteriaBuilder cb) {
        return cb.and(predicates.toArray(new Predicate[0]));
    }


    private String convertStudyTypeToColumn(String studyType) {
        String column = null;
        switch (studyType) {
            case "GXE":
                column = "gxe";
                break;
            case "GXG":
                column = "gxg";
                break;
            case "CNV":
                column = "cnv";
                break;
            case "Genome-wide genotyping array studies":
                //column ="";
                break;
            case "Targeted genotyping array studies":
                //column ="";
                break;
            case "Exome genotyping array studies":
                //column ="";
                break;
            case "Genome-wide sequencing studies":
                //column ="";
                break;
            case "Exome-wide sequencing studies":
                //column ="";
                break;
            case "Studies in curation queue":
                //column ="";
                break;
            case "Multi-SNP haplotype studies":
                //column ="";
                break;
            case "SNP Interaction studies":
                //column ="";
                break;
            case "p-Value Set":
                column ="fullPvalueSet";
                break;
            case "User Requested":
                column ="userRequested";
                break;
            default: column = null;
                break;
        }
        return column;
    }
}
