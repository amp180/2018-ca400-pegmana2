package space.pegman.redditMod.mappers;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import space.pegman.redditMod.domain.Database.Word;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TestWordsMapper {

    @Autowired
    SqlSession sessionTemplate;

    @Test
    public void insertSampleSet(){
        WordsMapper wordsMapper = sessionTemplate.getMapper(WordsMapper.class);
        Word word = new Word("hey");
        wordsMapper.insertWord(word);
        Assert.assertEquals(wordsMapper.deleteWord(word).longValue(), 1);
    }

}
