package space.pegman.redditMod.domain.Database;

import lombok.*;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
/*
    Database domain class for Stats
 */
public class Stats {
    String word;
    Long sampleSet;
    Long count;
    Double meanCount;
    Double medianCount;
    Double stdDev;
    Double MADCount;
}
