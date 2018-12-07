package space.pegman.redditMod.mappers;

import org.apache.ibatis.annotations.*;
import space.pegman.redditMod.domain.Database.Comment;
import space.pegman.redditMod.domain.Database.CommentSampleSet;
import space.pegman.redditMod.domain.Database.WordCount;

import java.util.HashMap;

@Mapper
public interface WordCountMapper {

    @Select("SELECT id AS id, comment AS comment, word AS word, count AS count FROM WordCounts WHERE id=#{id};")
    WordCount getWordCountById(Long id);

    @Select("SELECT id AS id, comment AS comment, word AS word, count AS count FROM WordCounts WHERE comment=#{comment.id};")
    WordCount[] getWordCountsByComment(@Param("comment") Comment comment);

    @Select("SELECT id AS id, comment AS comment, word AS word, count AS count FROM WordCounts WHERE comment IN (SELECT id FROM Comments WHERE sample_set=#{commentSet.id});")
    WordCount[] getWordCountsByCommentSet(@Param("commentSet") CommentSampleSet commentSet);

    @Select("SELECT word AS word, count AS count FROM WordCounts WHERE comment=#{comment.id};")
    @MapKey("word")
    HashMap<String, WordCount> getWordCountsMapByComment(@Param("comment") Comment comment);

    @Insert("INSERT INTO WordCounts (comment, word, count) VALUES (#{wordcount.comment}, #{wordcount.word}, #{wordcount.count});")
    @SelectKey(resultType = Long.class, before=false, keyProperty = "wordcount.id", keyColumn = "id", statement = "SELECT last_insert_rowid();")
    Long insertWordCount(@Param("wordcount") WordCount wordcount);

    @Delete("DELETE FROM WordCounts WHERE comment IN (SELECT id FROM Comments where sample_set=#{sampleSet.id});")
    Long deleteWordCountsBySampleSet(@Param("sampleSet") CommentSampleSet sampleSet);

    @Delete("DELETE FROM WordCounts WHERE id=#{wordcount.id};")
    Long deleteWordCount(@Param("wordcount") WordCount wordcount);

}