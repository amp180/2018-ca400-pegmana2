package space.pegman.redditMod.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import space.pegman.redditMod.dao.RedditCommentDao;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditComment;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditMore;

import java.util.HashSet;

@Data
@AllArgsConstructor
/*
    Class to be passed to the redditData collectComments method to track it's state.
    Instances act like a visitor,
    keeping track of comments and ids of more data that can be fetched,
    as well as the comments already fetched.
    The CollectsReplies flag determines if comment replies should be traversed.
 */
public class RedditCommentAccumulator {
    private HashSet<RedditComment> comments;
    private HashSet<String> visitedCommentIds;
    private HashSet<RedditMore> mores;
    private HashSet<String> visitedMores;
    private HashSet<String> afterIds;
    private HashSet<String> visitedAfterIds;
    private HashSet<String> threads;
    private HashSet<String> visitedThreads;
    private boolean collectsReplies;

    public RedditCommentAccumulator(boolean collectsReplies) {
        this();
        this.collectsReplies = collectsReplies;
    }

    public RedditCommentAccumulator(){
        this.comments = new HashSet<>();
        this.visitedCommentIds = new HashSet<>();
        this.mores = new HashSet<>();
        this.visitedMores = new HashSet<>();
        this.afterIds = new HashSet<>();
        this.visitedAfterIds = new HashSet<>();
        this.threads = new HashSet<>();
        this.visitedThreads = new HashSet<>();
        collectsReplies = true;
    }

    public void addComment(RedditComment comment){
        final String id = comment.getId();
        if (!visitedCommentIds.contains(id)) {
            comments.add(comment);
            visitedCommentIds.add(id);
        }
    }

    public void addMore(RedditMore more){
        if(!visitedMores.contains(more.getId())) {
            mores.add(more);
            visitedMores.add(more.getId());
        }
    }

    public void addThread(String threadLink){
        final String fullLink;
        if(threadLink.startsWith("http")){
            fullLink = threadLink+".json";
        } else {
            fullLink = RedditCommentDao.baseUrl + threadLink + ".json";
        }
        if(!visitedThreads.contains(fullLink)) {
            threads.add(fullLink);
            visitedThreads.add(fullLink);
        }
    }

    public void addAfter(String id){
        if (!visitedAfterIds.contains(id)) {
            afterIds.add(id);
            visitedAfterIds.add(id);
        }
    }

    public boolean getCollectsReplies(){
        return collectsReplies;
    }

    /*
        Returns whether there are comments or more can be fetched.
     */
    public boolean isEmpty(){
        return !hasMore() && (this.comments.size()) <= 0;
    }

    /*
        Returns whether more comments can be fetched.
     */
    public boolean hasMore(){
        return this.mores.size()+this.threads.size()+this.afterIds.size()>0;
    }
}
