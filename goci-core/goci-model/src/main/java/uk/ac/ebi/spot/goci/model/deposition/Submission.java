package uk.ac.ebi.spot.goci.model.deposition;

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
