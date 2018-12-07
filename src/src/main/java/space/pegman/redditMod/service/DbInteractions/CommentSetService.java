package space.pegman.redditMod.service.DbInteractions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.pegman.redditMod.domain.Database.Comment;
import space.pegman.redditMod.domain.Database.CommentSampleSet;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditComment;
import space.pegman.redditMod.mappers.*;

import java.util.HashSet;

@Service
@Slf4j
public class CommentSetService {

    private final CommentSampleSetMapper commentSampleSetMapper;
    private final CommentsMapper commentsMapper;
    private final WordsService wordsService;
    private final StatsService statsService;

    @Autowired
    public CommentSetService(
            CommentSampleSetMapper commentSampleSetMapper,
            CommentsMapper commentsMapper,
            WordsService wordsService,
            StatsService statsService
        ){
        this.commentSampleSetMapper=commentSampleSetMapper;
        this.commentsMapper=commentsMapper;
        this.wordsService = wordsService;
        this.statsService = statsService;
    }

    public CommentSampleSet createCommentSet(String name){
        CommentSampleSet sampleSet = new CommentSampleSet(null, name, false, null);
        commentSampleSetMapper.insertSampleSet(sampleSet);
        return sampleSet;
    }

    public void addCommentsToSet(HashSet<RedditComment> comments, CommentSampleSet set){
        log.info("Adding {} comments to set {}.", comments.size(), set.getName());
        for(RedditComment redditComment : comments){
            String[] words = wordsService.splitRedditComment(redditComment);
            Comment dbComment = new Comment();
            dbComment.setRedditId(redditComment.getId());
            dbComment.setNumWords((long)(words.length));
            dbComment.setUser(redditComment.getAuthor());
            dbComment.setSampleSet(set.getId());
            commentsMapper.insertComment(dbComment);
            wordsService.addWords(words, dbComment);
        }
    }

    public void finalizeCommentSampleSet(CommentSampleSet set){
        log.info("Calculating stats for commentSampleSet: {}.", set.getName());
        statsService.calculateStatsForCommentSet(set);
        log.info("Done calculating stats for: {}", set.getName());
        set.setDone(true);
        commentSampleSetMapper.updateSampleSet(set);
        log.info("Done: {}", set.getName());

    }

    public void deleteSampleSet(CommentSampleSet sampleSet){
        log.info("Attempting to delete commentSampleSet: {}.", sampleSet.getName());
        statsService.deleteStatsForCommentSet(sampleSet);
        wordsService.deleteWordCountsbyCommentSample(sampleSet);
        commentsMapper.deleteCommentsBySampleSet(sampleSet);
        commentSampleSetMapper.deleteSampleSet(sampleSet);
        log.info("commentSampleSet: {} deleted.", sampleSet.getName());
    }


}
