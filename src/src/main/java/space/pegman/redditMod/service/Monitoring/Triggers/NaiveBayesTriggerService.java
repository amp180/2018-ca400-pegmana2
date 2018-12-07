package space.pegman.redditMod.service.Monitoring.Triggers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.pegman.redditMod.domain.Database.CommentSampleSet;
import space.pegman.redditMod.domain.Database.Stats;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditComment;
import space.pegman.redditMod.mappers.CommentsMapper;
import space.pegman.redditMod.mappers.StatsMapper;
import space.pegman.redditMod.service.DbInteractions.WordsService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.log;

@Service
@Slf4j
public class NaiveBayesTriggerService {

    @Autowired
    CommentsMapper commentsMapper;

    @Autowired
    StatsMapper statsMapper;

    @Autowired
    WordsService wordsService;

    final static double THRESHOLD = 1;
    final static int MIN_MENTIONS = 2;

    /*
        Uses baysian classification to predict spam.
     */
    public boolean isSpam(RedditComment redditComment, CommentSampleSet goodSet, CommentSampleSet spamSet) {
        final String[] words = wordsService.splitRedditComment(redditComment);
        final Map<String, Stats> goodStatsMap = statsMapper.getWordCountsMap(goodSet.getId());
        final Map<String, Stats> spamStatsMap = statsMapper.getWordCountsMap(spamSet.getId());
        final Integer goodComments = goodSet.getNumComments();
        final Integer spamComments = spamSet.getNumComments();

        final double pBadGivenWords = pBadGivenWords(words, goodStatsMap, goodComments, spamStatsMap, spamComments);
        NaiveBayesTriggerService.log.info("Rating ({} in {} not {}) is {}, threshold is {}", redditComment.getName(),  spamSet.getName(), goodSet.getName(), pBadGivenWords, THRESHOLD);
        return pBadGivenWords >= THRESHOLD;
    }

    /*
        Calculate probability of words being in a set
     */
    private double pBadGivenWords(
            String[] words,
            Map<String, Stats> goodMap, Integer goodComments,
            Map<String, Stats> spamMap, Integer spamComments
    ){
        //http://www.cs.ubbcluj.ro/~gabis/DocDiplome/Bayesian/000539771r.pdf figure 2.4
        final Stats defaultStats = new Stats(null, null, 0L, 0.0, null, 1.0, null);
        final HashMap<String, Integer> wordCounts = wordsService.countWords(words);

        //no bias set
        final double logPGoodComment = log(0.5);
        final double logPSpamComment = log(0.5);

        double logScoreSpamGivenWords = logPSpamComment - logPGoodComment;

        for(Map.Entry<String, Integer> wordCount: wordCounts.entrySet()){
            final String word = wordCount.getKey();
            final Stats goodWordStats = goodMap.getOrDefault(word, defaultStats);
            final long goodWordCount = goodWordStats.getCount();
            final Stats spamWordStats = spamMap.getOrDefault(word, defaultStats);
            final long spamWordCount = spamWordStats.getCount();
            final long totalMentions = goodWordStats.getCount()+spamWordStats.getCount();

            if(goodWordCount>2 && spamWordCount>2 && totalMentions>MIN_MENTIONS){
                final double logPWordAndGood = log(goodWordCount)- log(goodComments+spamComments);
                final double logPWordAndSpam = log(spamWordCount)- log(goodComments+spamComments);
                final double logPWordGivenGood = logPWordAndGood-logPGoodComment;
                final double logPWordGivenSpam = logPWordAndSpam-logPSpamComment;

                logScoreSpamGivenWords += logPWordGivenSpam-logPWordGivenGood;
            } else if(goodWordCount<2 && spamWordCount>2  && totalMentions>MIN_MENTIONS){
                logScoreSpamGivenWords += log(0.8)-log(0.2);
            } else if(goodWordCount>2 && spamWordCount<2  && totalMentions>MIN_MENTIONS){
                logScoreSpamGivenWords += log(0.2)-log(0.8);
            }
        }

        return Math.exp(logScoreSpamGivenWords);
    }

}
