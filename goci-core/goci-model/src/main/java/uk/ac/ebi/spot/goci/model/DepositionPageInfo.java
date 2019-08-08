package uk.ac.ebi.spot.goci.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositionPageInfo {
    private int size;
    private int totalElements;
    private int totalPages;
    private int number;
}
