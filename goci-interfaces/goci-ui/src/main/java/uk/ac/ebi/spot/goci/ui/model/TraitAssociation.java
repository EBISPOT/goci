//package uk.ac.ebi.spot.goci.model;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotNull;
//
///**
//* Javadocs go here!
//*
//* @author Tony Burdett
//* @date 13/11/14
//*/
//@Entity
//@Table(name = "GWASASSOCIATIONS")
//public class TraitAssociation {
//    @Id
//    @GeneratedValue
//    @NotNull
//    @Column(name = "ID")
//    private Long id;
//
//    @Column(name = "STUDYID")
//    private Long studyID;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "GWASSNPXREF",
//            joinColumns = {@JoinColumn(name = "ASSOCIATIONID", referencedColumnName = "ID")},
//            inverseJoinColumns = {@JoinColumn(name = "SNPID", referencedColumnName = "ID")}
//    )
//    private Snp snps;
//
//    @ManyToOne(optional = false)
//    private Study study;
////    @ManyToOne(optional = false)
////    private Snp snp;
//
//    TraitAssociation() {
//    }
//
//    TraitAssociation(Study study, Snp snps) {
////        this.trait = trait;
//        this.study = study;
//        this.snps = snps;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
////    public String getTrait() {
////        return trait;
////    }
//
////    public String getStudyID() {
////        return studyID;
////    }
//
//    public Study getStudy(){
//        return study;
//    }
//
//    public Snp getSnps() {
//        return snps;
//    }
//
//    @Override public String toString() {
//        return "TraitAssociation{" +
//                "id=" + id +
////                ", trait=" + trait +
//                ", study=" + study +
//                ", snps=" + snps +
//                '}';
//    }
//}
