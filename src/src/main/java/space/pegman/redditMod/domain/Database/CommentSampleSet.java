package space.pegman.redditMod.domain.Database;

import lombok.*;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
/*
    Database domain class for CommentSampleSet
 */
public class CommentSampleSet {
    Long id;
    String name;
    boolean done = false;
    transient Integer numComments;
}
