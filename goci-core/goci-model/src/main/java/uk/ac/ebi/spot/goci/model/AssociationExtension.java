package uk.ac.ebi.spot.goci.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
public class AssociationExtension {
    @Id
    @GeneratedValue
    Long id;

    @OneToOne
    Association association;
    String effectAllele;
    String otherAllele;
}
