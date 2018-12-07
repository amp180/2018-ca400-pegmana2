package space.pegman.redditMod.service.Monitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.pegman.redditMod.domain.Database.CommentSampleSet;
import space.pegman.redditMod.domain.Database.Trigger;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditComment;
import space.pegman.redditMod.mappers.CommentSampleSetMapper;
import space.pegman.redditMod.service.Monitoring.Triggers.AnomalyTriggerService;
import space.pegman.redditMod.service.Monitoring.Triggers.KNearestTriggerService;
import space.pegman.redditMod.service.Monitoring.Triggers.NaiveBayesTriggerService;

@Service
@Slf4j
public class TriggerService {

    @Autowired
    AnomalyTriggerService anomalyTriggerService;

    @Autowired
    KNearestTriggerService kNearestTriggerService;

    @Autowired
    NaiveBayesTriggerService naiveBayesTriggerService;

    @Autowired
    CommentSampleSetMapper commentSampleSetMapper;

    /*
        Returns whether a trigger matches a reddit comment.
     */
    public boolean checkTrigger(Trigger trigger, RedditComment redditComment){
        switch(trigger.getType()){
            case Trigger.Types.REGEX: return checkRegexTrigger(trigger, redditComment);
            case Trigger.Types.BAYSIAN: return checkBaysianTrigger(trigger, redditComment);
            case Trigger.Types.SIMILARITY: return checkKNearestTrigger(trigger, redditComment);
            case Trigger.Types.ANOMALY: return checkAnomalyTrigger(trigger, redditComment);
            case Trigger.Types.ALL: return true;
            default:
                log.warn("Unknown trigger type: {}", trigger.toString());
                return false;
        }
    }


    public boolean checkRegexTrigger(Trigger t, RedditComment redditComment) {
        final String regex = t.getRegex();
        final String commentBody = redditComment.getBody();
        return commentBody.matches(regex);
    }


    public boolean checkBaysianTrigger(Trigger t, RedditComment redditComment){
        final long badSetId = t.getBadSampleSet();
        final long goodSetId = t.getSampleSet();
        final CommentSampleSet commentSampleSet = commentSampleSetMapper.getCommentSampleSetById(goodSetId);
        final CommentSampleSet badCommentSampleSet = commentSampleSetMapper.getCommentSampleSetById(badSetId);

        return naiveBayesTriggerService.isSpam(redditComment, commentSampleSet, badCommentSampleSet);
    }


    public boolean checkKNearestTrigger(Trigger t, RedditComment redditComment){
        final long badSetId = t.getBadSampleSet();
        final long goodSetId = t.getSampleSet();
        final CommentSampleSet commentSampleSet = commentSampleSetMapper.getCommentSampleSetById(goodSetId);
        final CommentSampleSet badCommentSampleSet = commentSampleSetMapper.getCommentSampleSetById(badSetId);

        return kNearestTriggerService.closerToSpam(redditComment, commentSampleSet, badCommentSampleSet);
    }


    public boolean checkAnomalyTrigger(Trigger t, RedditComment redditComment){
        final long goodSetId = t.getSampleSet();
        final CommentSampleSet commentSampleSet = commentSampleSetMapper.getCommentSampleSetById(goodSetId);

        return anomalyTriggerService.isAnomaly(redditComment, commentSampleSet);
    }
}
