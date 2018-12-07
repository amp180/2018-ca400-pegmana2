package space.pegman.redditMod.mappers;

import org.apache.ibatis.annotations.*;
import space.pegman.redditMod.domain.Database.Rule;

@Mapper
public interface RulesMapper {

    @Select("SELECT id AS id, name AS name, trigger as trigger, action as action, subreddit as sub " +
            " FROM Rules WHERE subreddit=#{sub} " +
            " ORDER BY (SELECT type FROM Triggers WHERE id = trigger) DESC;")
    Rule[] getRulesBySubreddit(@Param("sub") String subreddit);

    @Select("SELECT id AS id, name AS name, trigger as trigger, action as action, subreddit as sub " +
            " FROM Rules" +
            " ORDER BY (SELECT type FROM Triggers WHERE id = trigger);")
    Rule[] getRules();

    @Select("SELECT id AS id, name AS name, trigger as trigger, action as action, subreddit as sub " +
            " FROM Rules WHERE id=#{id};")
    Rule getRuleById(@Param("id") Long id);

    @Insert("INSERT INTO Rules (name, trigger, action, subreddit) VALUES (#{rule.name}, #{rule.trigger}, #{rule.action}, #{rule.sub});")
    @SelectKey(resultType=Long.class, before=false, keyProperty = "rule.id", keyColumn = "id", statement = "SELECT last_insert_rowid() as id;")
    Long insertRule(@Param("rule") Rule rule);

    @Update("UPDATE Rules SET trigger=#{rule.trigger}, action=#{rule.action}, subreddit=#{rule.sub} WHERE id=#{rule.id};")
    Long updateRule(@Param("rule") Rule rule);

    @Delete("DELETE FROM Rules WHERE (trigger=#{rule.trigger} AND action=#{rule.action} AND subreddit=#{rule.sub}) OR id=#{rule.id};")
    Long deleteRule(@Param("rule") Rule rule);

}
