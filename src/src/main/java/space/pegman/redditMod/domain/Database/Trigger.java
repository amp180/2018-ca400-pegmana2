package space.pegman.redditMod.domain.Database;

import lombok.*;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
/*
    Database domain class for Trigger
 */
public class Trigger {

    Long id;
    String name;
    Integer type;
    String regex;
    Long sampleSet;
    Long badSampleSet;

    public static class Types {
        public final static int REGEX = 0;
        public final static int BAYSIAN = 1;
        public final static int SIMILARITY = 2;
        public final static int ANOMALY = 3;
        public final static int ALL = 4;
    }

}
