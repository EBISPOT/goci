package uk.ac.ebi.spot.goci.model;

import lombok.Data;

@Data
public class Submission {

    String id;
    String pubMedID;
    String title;
    String author;
    String status;
    String curator;
    String created;
    String publicationStatus;
}
