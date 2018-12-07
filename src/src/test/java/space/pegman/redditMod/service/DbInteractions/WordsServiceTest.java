package space.pegman.redditMod.service.DbInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditComment;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WordsServiceTest {

    @Autowired
    WordsService wordsService;

    private static final List<String> testWords = Arrays.asList("Test", "Comment", "http", "stop");
    private static final String testString = "Test Comment by for ..,,!@... http:// stop. :)";

    @Test
    public void testWordSplit(){
        RedditComment exampleComment = new RedditComment();
        exampleComment.setBody(testString);
        String[] words = wordsService.splitRedditComment(exampleComment);

        assertEquals(testWords, Arrays.asList(words));
    }

}
