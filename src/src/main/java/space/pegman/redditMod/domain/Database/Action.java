package space.pegman.redditMod.domain.Database;

import lombok.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
/*
    Database domain class for Action
 */
public class Action {

    Long id;
    String name;
    Integer type;
    String message;

    public static class Types {
        public static final int APPROVE = 0;
        public static final int DELETE = 1;
        public static final int BANUSER = 2;
        public static final int REPLY = 3;
    }

}
