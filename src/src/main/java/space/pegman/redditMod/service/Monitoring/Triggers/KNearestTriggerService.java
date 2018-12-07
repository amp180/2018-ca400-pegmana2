package space.pegman.redditMod.service.Monitoring.Triggers;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.pegman.redditMod.domain.Database.Comment;
import space.pegman.redditMod.domain.Database.CommentSampleSet;
import space.pegman.redditMod.domain.Database.WordCount;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditComment;
import space.pegman.redditMod.mappers.CommentsMapper;
import space.pegman.redditMod.mappers.StatsMapper;
import space.pegman.redditMod.mappers.WordCountMapper;
import space.pegman.redditMod.service.DbInteractions.WordsService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;

@Service
@Slf4j
public class KNearestTriggerService {

    @Autowired
    CommentsMapper commentsMapper;

    @Autowired
    StatsMapper statsMapper;

    @Autowired
    WordCountMapper wordCountMapper;

    @Autowired
    WordsService wordsService;

    private static final long k = 3;

    /*
        Detects spam comments by similarity to other spam.
     */
    public boolean closerToSpam(RedditComment redditComment, CommentSampleSet goodSet, CommentSampleSet spamSet) {
        final String[] commentWords = wordsService.splitRedditComment(redditComment);
        final Map<String,Integer> wordCounts = wordsService.countWords(commentWords);

        final Comment[] goodComments = commentsMapper.getCommentsBySampleSet(goodSet);
        final PriorityQueue<CommentDifference> top = getTopN(rankBySimilarity(wordCounts, goodComments), k);

        final Comment[] spamComments = commentsMapper.getCommentsBySampleSet(spamSet);
        top.addAll(getTopN(rankBySimilarity(wordCounts, spamComments), k));

        int closeSpam = 0;
        for(CommentDifference diff : getTopN(top, k)){
            if(diff.comment.getSampleSet().longValue()==spamSet.getId().longValue()){
                closeSpam++;
            }
        }

        return closeSpam > k/2.0;
    }


    /*
        Returns comments sorted byu similarity.
     */
    private PriorityQueue<CommentDifference> rankBySimilarity(Map<String, Integer> commentAWords, Comment[] comments){
        PriorityQueue<CommentDifference> commentsQueue = new PriorityQueue<>();

        for(Comment comment:comments) {
            final Map<String, WordCount> commentBWordCounts = wordCountMapper.getWordCountsMapByComment(comment);
            HashMap<String, Integer> commentBWords = new HashMap<>();
            for(WordCount count: commentBWordCounts.values()) {
                commentBWords.put(count.getWord(), count.getCount().intValue());
            }
            commentsQueue.add(compareComments(commentAWords, comment, commentBWords));
        }
        return commentsQueue;
    }


    /*
        Gets the distance between two comments.
     */
    private CommentDifference compareComments(
            final Map<String, Integer> commentAWords,
            final Comment commentB,
            final Map<String, Integer> commentBWords
    ) {
        final HashSet<String> allWords = new HashSet<>();
        allWords.addAll(commentAWords.keySet());
        allWords.addAll(commentBWords.keySet());

        int totalDifference = 0;
        for(String word : allWords) {
            final long aCount = commentAWords.getOrDefault(word,0);
            final long bCount = commentBWords.getOrDefault(word,0);
            final long difference = Math.abs(aCount-bCount);
            totalDifference += difference;
        }

        return new CommentDifference(commentB, totalDifference);
    }


    /*
        Returns the top n comments by distance.
     */
    private PriorityQueue<CommentDifference> getTopN(final PriorityQueue<CommentDifference> commentsQueue, long n){
        final PriorityQueue<CommentDifference> topQueue = new PriorityQueue<>();
        n = Math.min(n, commentsQueue.size());

        for(long i = 0; i<n; i++){
            topQueue.add(commentsQueue.poll());
        }

        return topQueue;
    }


    /*
        Class to allow sorting of comments by their difference.
     */
    @Data
    private class CommentDifference implements Comparable<CommentDifference> {
        final Comment comment;
        final int difference;

        public int compareTo(CommentDifference other){
            return Long.compare(this.difference, other.difference);
        }
    }
}
