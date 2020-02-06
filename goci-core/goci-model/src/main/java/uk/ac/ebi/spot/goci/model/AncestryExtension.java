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

    private Integer numberCases;

    private Integer numberControls;

    private String sampleDescription;

    private String isolatedPopulation;

    private String ancestryDescriptor;
}
