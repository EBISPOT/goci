package uk.ac.ebi.spot.goci.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
public class AncestryExtension {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Ancestry ancestry;

    private Long numberCases;

    private Long numberControls;

    private String sampleDescription;

    private String ancestryDescriptor;

    private String isolatedPopulation;
}
