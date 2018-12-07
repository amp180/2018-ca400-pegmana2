package space.pegman.redditMod.service.CommentFetching;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import space.pegman.redditMod.dao.RedditAuthTokenDao;
import space.pegman.redditMod.dao.RedditCommentDao;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThing;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditComment;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditMore;
import space.pegman.redditMod.domain.RedditCommentAccumulator;

import java.util.HashSet;


@Slf4j
@Service
public class CommentFetcherService {

    private final RedditCommentDao redditCommentDao;

    @Autowired
    public CommentFetcherService(RedditCommentDao redditCommentDao){
        this.redditCommentDao = redditCommentDao;
    }


    public RedditCommentAccumulator fetchCommentsStopAfterN(String redditUrl, int fetchAboutN, RedditCommentAccumulator acc) throws InterruptedException {
        if (acc==null) {
            acc = new RedditCommentAccumulator();
        }
        RedditCommentAccumulator accumulator = fetchComments(redditUrl, acc);

        do {
            HashSet<RedditMore> mores = accumulator.getMores();
            HashSet<String> threadLinks = accumulator.getThreads();
            HashSet<String> afters = accumulator.getAfterIds();

            checkInterrupt();

            if (mores.size() > 0) {
                accumulator.setMores(new HashSet<>());

                for (RedditMore more : mores) {
                    RedditThing[] things = redditCommentDao.getMoreChildren(more);
                    collectCommentsFromThingArray(things, accumulator);
                }

            } else if (threadLinks.size()>0) {
                    accumulator.setThreads(new HashSet<>());

                    for (String link : threadLinks) {
                        fetchComments(link, accumulator);
                    }
            } else if (afters.size()>0){
                accumulator.setAfterIds(new HashSet<>());
                for(String afterId : afters){
                    final String urlWithoutAfter = redditUrl.replaceAll("\\?.*", "");
                    final String urlWithNewAfter = urlWithoutAfter + "?after="+afterId+"&raw=true";
                    fetchComments(urlWithNewAfter, accumulator);
                }
            } else {
                break;
            }
        } while(accumulator.getComments().size()<fetchAboutN && accumulator.hasMore());

        return accumulator;
    }


    public RedditCommentAccumulator fetchComments(String redditUrl, RedditCommentAccumulator accumulator) throws InterruptedException {
        RedditThing[] things = redditCommentDao.getThings(redditUrl);
        collectCommentsFromThingArray(things, accumulator);
        return accumulator;
    }


    private void collectCommentsFromThingArray(RedditThing[] things, RedditCommentAccumulator accumulator){
        for(RedditThing thing: things){
            thing.collectComments(accumulator, null);
        }
    }

    protected void checkInterrupt() throws InterruptedException{
        if(Thread.currentThread().isInterrupted()){
            throw new InterruptedException();
        }
    }

}
