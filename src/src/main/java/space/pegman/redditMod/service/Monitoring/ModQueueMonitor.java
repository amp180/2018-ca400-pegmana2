package space.pegman.redditMod.service.Monitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

@Slf4j
@Service
public class ModQueueMonitor {

    @Autowired
    RedditCommentDao redditCommentDao;

    @Autowired
    SubredditMapper subredditMapper;

    @Autowired
    RuleService ruleService;

    static private Thread thread = null;
    final static private String urlPrefix = "https://oauth.reddit.com";
    final static private String urlPostfix = "/about/modqueue.json?raw_json=true";

    /*
        Starts a thread that monitors the modqueue pf a subreddit.
     */
    @PostConstruct
    public synchronized void spawnMonitorThread(){
        if (thread!=null){
            stopMonitorThread();
        }

        final ModQueueMonitor _this = this;

        Runnable monitorRunner = () -> {
            try {
                log.info("ModQueue monitor thread started.");
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        _this.applyAllRules();
                    } catch (InterruptedException ie){
                        throw ie; //re-throw InterruptedException
                    } catch (Exception e){
                        log.error("Comment queue monitor error", e);
                    }
                    Thread.sleep(10000);
                }
            } catch (InterruptedException e) {
            log.info("ModQueue monitor thread stopped. Printing stacktrace.", e);
        }
        };

        thread = new Thread(monitorRunner);
        thread.start();
    }


    /*
        Stops the thread on shutdown.
     */
    @PreDestroy
    public void stopMonitorThread(){
        if (thread!=null) {
            thread.interrupt();
            thread = null;
        }
    }


    /*
        Gets all monitored subs and applies rules.
     */
    private void applyAllRules() throws InterruptedException {
        final Subreddit[] subs = subredditMapper.getSubreddits();
        for(Subreddit sub : subs) {
            applyRulesToSubreddit(sub.getSub());
        }
    }


    /*
        Applies rules given a subreddit name.
     */
    private void applyRulesToSubreddit(String sub) throws InterruptedException {
        Set<RedditComment> toModerate = getModQueueForSubReddit(sub);
        ruleService.applyRulesBySubreddit(sub, toModerate);
    }


    /*
        Gets the modqueue of a subreddit.
     */
    public Set<RedditComment> getModQueueForSubReddit(String sub) throws InterruptedException {
        final RedditThing[] things;
        try {
            final String url = urlPrefix + sub + urlPostfix;
            things = redditCommentDao.getThings(url);
        } catch (HttpClientErrorException e) {
            log.warn("{}", e);
            return new HashSet<>();
        }
        RedditCommentAccumulator accumulator = new RedditCommentAccumulator(false);

        for(RedditThing thing: things){
            thing.collectComments(accumulator, null);
        }

        return accumulator.getComments();
    }

}
