package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositionProvenance {
//    @JsonDeserialize(using= DateTimeDeserializer.class)
//    @JsonSerialize(using = DateTimeSerializer.class)
    private DateTime timestamp;
    private DepositionUser user;}
