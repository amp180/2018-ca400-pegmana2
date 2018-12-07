package space.pegman.redditMod.mappers;

import org.apache.ibatis.annotations.*;
import space.pegman.redditMod.domain.Database.Comment;
import space.pegman.redditMod.domain.Database.CommentSampleSet;


@Mapper
public interface CommentsMapper {
    @Select("SELECT id as id, reddit_id as redditId,  sample_set as sampleSet, user as user, num_words as numWords FROM Comments WHERE id=#{id};")
    Comment getComment(@Param("id") Long id);

    @Select("SELECT id as id, reddit_id as redditId, sample_set as sampleSet, user as user, num_words as numWords FROM Comments WHERE sample_set=#{commentSet.id};")
    Comment[] getCommentsBySampleSet(@Param("commentSet") CommentSampleSet commentSampleSet);

    @Insert("INSERT INTO Comments (user, reddit_id, sample_set, num_words) VALUES (#{comment.user}, #{comment.redditId}, #{comment.sampleSet}, #{comment.numWords});")
    @SelectKey(resultType = Long.class, before=false, keyProperty = "comment.id", keyColumn = "id", statement = "SELECT last_insert_rowid() as id;")
    Long insertComment(@Param("comment") Comment comment);

    @Update("UPDATE Comments SET user = #{comment.user}, reddit_id=#{comment.redditId},  sample_set=#{comment.sampleSet}, num_words=#{comment.numWords} WHERE id=#{comment.id};")
    @SelectKey(resultType = Long.class, before=false, keyProperty = "comment.id", keyColumn = "id", statement = "SELECT last_insert_rowid() as id;")
    Long updateComment(@Param("comment")  Comment comment);

    @Delete("DELETE FROM Comments WHERE sample_set=#{sampleSet.id};")
    Long deleteCommentsBySampleSet(@Param("sampleSet") CommentSampleSet sampleSet);

    @Delete("DELETE FROM Comments WHERE id=#{comment.id};")
    Long deleteComment(@Param("comment")  Comment comment);
}
