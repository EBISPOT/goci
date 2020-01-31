package uk.ac.ebi.spot.goci.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class AssociationExtension {
    @Id
    @GeneratedValue
    Long id;

    @OneToOne
    @JoinColumn(name = "association_id", unique = true)
    Association association;
    String effectAllele;
    String otherAllele;
}
