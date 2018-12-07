package space.pegman.redditMod.mappers;

import org.apache.ibatis.annotations.*;
import space.pegman.redditMod.domain.Database.Trigger;

@Mapper
public interface TriggersMapper {

    @Select("SELECT id AS id, name AS name, type AS type, regex AS regex, sample_set AS sampleSet, bad_sample_set as badSampleSet " +
            "FROM Triggers;")
    Trigger[] getTriggers();

    @Select("SELECT id AS id, name AS name, type AS type, regex AS regex, sample_set AS sampleSet, bad_sample_set as badSampleSet " +
            "FROM Triggers where id=#{id};")
    Trigger getTriggerById(@Param("id") Long id);

    @Insert({"INSERT INTO Triggers (name, type, regex, sample_set, bad_sample_set) " +
            "VALUES (#{trigger.name}, #{trigger.type}, #{trigger.regex}, #{trigger.sampleSet}, #{trigger.badSampleSet});"})
    @SelectKey(resultType = Long.class, before=false, keyProperty = "trigger.id", keyColumn = "id", statement = "SELECT last_insert_rowid() as id;")
    Long insertTrigger(@Param("trigger") Trigger trigger);

    @Delete("DELETE FROM Triggers WHERE id=#{triggerId};")
    Long deleteTrigger(@Param("triggerId") long triggerId);
}
