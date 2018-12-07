package space.pegman.redditMod.mappers;

import org.apache.ibatis.annotations.*;
import space.pegman.redditMod.domain.Database.CommentSampleSet;

import java.util.HashMap;


@Mapper
public interface CommentSampleSetMapper {

    @Select("SELECT id AS id, name AS name, done as done, " +
            "(SELECT COUNT(*) FROM Comments WHERE sample_set=CommentSampleSets.id) AS numComments " +
            "FROM CommentSampleSets;")
    CommentSampleSet[] getCommentSampleSets();

    @Select("SELECT id AS id, name AS name, done as done, " +
            "(SELECT COUNT(*) FROM Comments WHERE sample_set=CommentSampleSets.id) AS numComments " +
            "FROM CommentSampleSets WHERE id=#{id};")
    CommentSampleSet getCommentSampleSetById(Long id);

    @Select("SELECT id AS id, name AS name, done as done, " +
            "(SELECT COUNT(*) FROM Comments WHERE sample_set=CommentSampleSets.id) AS numComments " +
            "FROM CommentSampleSets WHERE name=#{name};")
    CommentSampleSet getCommentSampleSetByName(String name);

    @Insert("INSERT INTO CommentSampleSets (name, done) VALUES (#{sampleSet.name}, #{sampleSet.done});")
    @SelectKey(resultType = Long.class, before=false, keyProperty = "sampleSet.id", keyColumn = "id", statement = "SELECT last_insert_rowid() as id;")
    Long insertSampleSet(@Param("sampleSet") CommentSampleSet sampleSet);

    @Update("UPDATE CommentSampleSets SET name=#{sampleSet.name}, done=#{sampleSet.done} WHERE id=#{sampleSet.id};")
    Long updateSampleSet(@Param("sampleSet") CommentSampleSet sampleSet);

    @Delete("DELETE FROM CommentSampleSets WHERE id=#{sampleSet.id};")
    Long deleteSampleSet(@Param("sampleSet") CommentSampleSet sampleSet);
}
