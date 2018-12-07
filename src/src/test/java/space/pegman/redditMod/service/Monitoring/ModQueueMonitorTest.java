package space.pegman.redditMod.service.Monitoring;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import space.pegman.redditMod.dao.RedditCommentDao;
import space.pegman.redditMod.domain.Database.Subreddit;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThing;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditComment;
import space.pegman.redditMod.domain.RedditCommentAccumulator;
import space.pegman.redditMod.mappers.SubredditMapper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ModQueueMonitorTest {

    @Autowired
    ModQueueMonitor modQueueMonitor;

    @Test
    public void getModQueueForSubReddit() throws InterruptedException {
        Set<RedditComment> comments = modQueueMonitor.getModQueueForSubReddit("/r/sample_cat_subreddit");
        assert comments != null;
    }

}
