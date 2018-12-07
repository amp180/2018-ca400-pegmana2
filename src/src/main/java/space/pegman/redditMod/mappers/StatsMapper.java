package space.pegman.redditMod.mappers;

import org.apache.ibatis.annotations.*;
import space.pegman.redditMod.domain.Database.CommentSampleSet;
import space.pegman.redditMod.domain.Database.Stats;

import java.util.HashMap;
import java.util.Map;

@Mapper
public interface StatsMapper {

    @Select("SELECT word as word, sample_set as sampleSet, count as count, mean_count as meanCount, median_count as medianCount, std_dev as stdDev, mad_count as MADCount "
            + "FROM Stats "
            + "WHERE word=#{word} "
            + "AND sample_set = #{commentSample};"
    )
    Stats getStats(@Param("word") String word, @Param("commentSample") Long commentSampleId);

    @Select("SELECT word as word, sample_set as sampleSet, count as count, mean_count as meanCount, median_count as medianCount, std_dev as stdDev, mad_count as MADCount "
            + "FROM Stats "
            + "WHERE word=#{word};"
    )
    Stats[] getStatsByWord(@Param("word") String word);

    @Select("SELECT word as word, sample_set as sampleSet, count as count, mean_count as meanCount, median_count as medianCount, std_dev as stdDev, mad_count as MADCount "
            + "FROM Stats "
            + "WHERE sample_set = #{commentSample};"
    )
    Stats[] getStatsBySampleSet(@Param("commentSample") Long commentSample);

    @Select("SELECT word as word, sample_set as sampleSet, count as count, mean_count as meanCount, median_count as medianCount, std_dev as stdDev, mad_count as MADCount "
            +" FROM Stats "
            + "WHERE sample_set = #{commentSample};"
    )
    @MapKey("word")
    Map<String, Stats> getWordCountsMap(@Param("commentSample") Long commentSampleId);

    @Insert({"INSERT INTO Stats(word, sample_set, count, mean_count, median_count, std_dev, mad_count) " +
            " VALUES (#{stats.word}, #{stats.sampleSet}, #{stats.count}, #{stats.meanCount}, #{stats.medianCount}, #{stats.stdDev}, #{stats.MADCount} );"})
    @SelectKey(resultType = String.class, before=false, keyProperty = "stats.word", keyColumn = "word", statement = "SELECT word as word FROM Stats WHERE rowid=last_insert_rowid();")
    Long insertStats(@Param("stats") Stats stats);

    @Delete("DELETE FROM Stats where sample_set=#{sampleSet.id};")
    Long deleteStatsBySampleSet(@Param("sampleSet") CommentSampleSet sampleSet);

}
