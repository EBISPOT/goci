package uk.ac.ebi.spot.goci.curation.service.deposition;

public class DepositionTransform {

    public static String transformGenotypingTechnology(String input) {
        if (input.equalsIgnoreCase("Whole genome sequencing")) {
            return "Genome-wide sequencing";
        }
        return input;
    }

}
