package space.pegman.redditMod.domain.Database;

import lombok.*;

@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
/*
    Database domain class for Comment
 */
public class Comment {
    Long id;
    String user;
    String redditId;
    Long sampleSet;
    Long numWords;
}
