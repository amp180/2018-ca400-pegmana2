package space.pegman.redditMod.domain.Database;

import lombok.*;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
/*
    Database domain class for WordCount
 */
public class WordCount {
    Long id;
    Long comment;
    String word;
    Long count;
}
