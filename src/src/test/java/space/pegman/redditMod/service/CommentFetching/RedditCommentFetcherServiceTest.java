package space.pegman.redditMod.service.CommentFetching;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import space.pegman.redditMod.domain.RedditCommentAccumulator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedditCommentFetcherServiceTest {

    @Autowired
    CommentFetcherService commentFetcherService;

    @Test
    public void testCommentFetcher() throws InterruptedException {
        RedditCommentAccumulator a = new RedditCommentAccumulator();
        commentFetcherService.fetchComments("https://oauth.reddit.com/r/worldnews/comments/8h0xyd/landowner_aims_to_bring_wolves_back_to_scotland/comments.json", a);
        assert !a.isEmpty();
    }

}
