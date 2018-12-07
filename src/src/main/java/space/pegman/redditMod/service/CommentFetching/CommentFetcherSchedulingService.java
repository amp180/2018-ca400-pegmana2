package space.pegman.redditMod.service.CommentFetching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.pegman.redditMod.domain.Database.CommentSampleSet;
import space.pegman.redditMod.domain.RedditCommentAccumulator;
import space.pegman.redditMod.service.DbInteractions.CommentSetService;

import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CommentFetcherSchedulingService {

    private static final int fetchSize = 1000;
    private final CommentFetcherService commentFetcherService;
    private final CommentSetService commentSetService;
    private final ConcurrentHashMap<String, Thread> threads;

    @Autowired
    CommentFetcherSchedulingService(CommentFetcherService commentFetcherService, CommentSetService commentSetService){
        this.commentFetcherService = commentFetcherService;
        this.commentSetService = commentSetService;
        this.threads = new ConcurrentHashMap<>();
    }

    public void saveCommentSet(final String name, final String url, final int approxMaxSize) throws InterruptedException {
        CommentSampleSet set = commentSetService.createCommentSet(name);
        RedditCommentAccumulator acc = new RedditCommentAccumulator();
        try {
            //collect in batches to minimise memory use
            for (int i = 0, fetched = 0; i < (approxMaxSize + fetchSize) && acc.isEmpty(); i += fetched) {
                final int toFetch = Math.min(fetchSize, approxMaxSize - i);
                log.trace(name + ": collecting comments " + i + " to " + (i + toFetch));
                acc = commentFetcherService.fetchCommentsStopAfterN(url, toFetch, acc);
                fetched = acc.getComments().size();
                log.trace(name + ": writing comments " + i + " to " + (i + fetched));
                if (fetched == 0) {
                    log.trace("No more comments fetched for {}/.", name);
                    break;
                }
                commentSetService.addCommentsToSet(acc.getComments(), set);
                acc.setComments(new HashSet<>());
                checkInterrupt();
            }

            commentSetService.finalizeCommentSampleSet(set);
        } catch (Exception e) {
            log.error("Comment set insertion failed, reverting. {}", e);
            commentSetService.deleteSampleSet(set);
        }
    }

    public void spawnFetchThread(final String name, final String url, final int approxMaxSize){
        Runnable r = () -> {
            try{
                log.info("Started fetch thread for "+url);
                    saveCommentSet(name, url, approxMaxSize);

            } catch (InterruptedException e) {
                log.warn(name+": collection interrupted early.");
            } finally {
                threads.remove(name);
            }
        };
        Thread t = new Thread(r);
        threads.put(name, t);
        t.start();
    }


    public boolean interruptCollection(String name){
        if (threads.containsKey(name)) {
            Thread t = this.threads.get(name);
            if (t != null) {
                t.interrupt();
                return true;
            }
        }
        return false;
    }

    @PreDestroy
    private void interruptCollection(){
        for (Thread t : threads.values()) {
            t.interrupt();
        }
    }

    public void spawnDeleteSetThread(final CommentSampleSet sampleSet){
        CommentFetcherSchedulingService _this = this;
        Runnable r = () -> {
            try{
                log.info("Started delete thread for "+sampleSet.getName());
                if(_this.threads.containsKey(sampleSet.getName())) {
                    interruptCollection(sampleSet.getName());
                } else {
                    commentSetService.deleteSampleSet(sampleSet);
                }
            } catch (Exception e) {
                log.warn(sampleSet.getName()+": deletion interrupted early.", e);
            } finally {
                _this.threads.remove(sampleSet.getName()+"-delete");
            }
        };
        Thread t = new Thread(r);
        threads.put(sampleSet.getName()+"-delete", t);
        t.start();
    }

    private void checkInterrupt() throws InterruptedException {
        if(Thread.currentThread().isInterrupted()){
            throw new InterruptedException();
        }
    }

}
