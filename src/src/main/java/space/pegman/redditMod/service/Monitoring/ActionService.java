package space.pegman.redditMod.service.Monitoring;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.pegman.redditMod.dao.RedditCommentDao;
import space.pegman.redditMod.domain.Database.Action;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditComment;

@Slf4j
@Service
public class ActionService {

    @Autowired
    RedditCommentDao redditCommentDao;

    /*
        Applies an action to a reddit comment.
     */
    public boolean takeAction(Action action, RedditComment comment){
        switch(action.getType()){
            case Action.Types.APPROVE:
                this.approveComment(comment);
                return true;
            case Action.Types.DELETE:
                return this.deleteComment(comment);
            case Action.Types.BANUSER:
                this.banUser(comment);
                return this.deleteComment(comment);
            case Action.Types.REPLY:
                this.approveComment(comment);
                return this.replyToComment(action, comment);
            default:
                log.warn("Unknown action type: {}", action.toString());
                return false;
        }
    }

    private boolean deleteComment(RedditComment comment) {
        try {
            return redditCommentDao.removeComment(comment.getName());
        } catch (Exception e) {
            log.warn("Deleting comment {} failed.", comment, e);
            return false;
        }
    }

    private void banUser(RedditComment comment){
        try {
            redditCommentDao.banUserForComment(comment.getAuthor(), comment.getSubredditId());
        } catch (Exception e) {
            log.warn("Banning user {} failed.", comment.getAuthor(), e);
        }
    }

    private boolean replyToComment(Action action, RedditComment comment){
        try {
            return redditCommentDao.postReply(comment.getName(), action.getMessage());
        } catch (Exception e) {
            log.warn("Posting reply to comment {} failed.", comment, e);
            return false;
        }
    }

    private void approveComment(RedditComment comment){
        try {
            redditCommentDao.approveComment(comment.getName());
        } catch (Exception e) {
            log.warn("Approving comment {} failed.", comment, e);
        }
    }

}
