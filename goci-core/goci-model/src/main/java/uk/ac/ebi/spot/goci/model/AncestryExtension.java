package uk.ac.ebi.spot.goci.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class AncestryExtension {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "ancestry_id", unique = true)
    private Ancestry ancestry;

    private Long numberCases;

    private Long numberControls;

    private String sampleDescription;

    private String ancestryDescriptor;

    private String isolatedPopulation;
}
