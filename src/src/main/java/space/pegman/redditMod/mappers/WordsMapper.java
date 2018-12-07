package space.pegman.redditMod.mappers;

import org.apache.ibatis.annotations.*;
import space.pegman.redditMod.domain.Database.Word;

@Mapper
public interface WordsMapper {
    @Insert("INSERT OR IGNORE INTO Words (word) VALUES (#{word.word});")
    Long insertWord(@Param("word") Word word);

    @Delete("DELETE FROM Words WHERE word=#{word.word};")
    Long deleteWord(@Param("word") Word word);
}

