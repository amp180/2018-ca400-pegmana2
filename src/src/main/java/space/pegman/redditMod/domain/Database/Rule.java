package space.pegman.redditMod.domain.Database;

import lombok.*;


@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
/*
    Database domain class for Rule
 */
public class Rule {
    Long id;
    String name;
    Long trigger;
    Long action;
    String sub;

}
