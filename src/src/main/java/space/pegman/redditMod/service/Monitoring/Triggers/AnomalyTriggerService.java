package space.pegman.redditMod.service.Monitoring.Triggers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.pegman.redditMod.domain.Database.CommentSampleSet;
import space.pegman.redditMod.domain.Database.Stats;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditComment;
import space.pegman.redditMod.mappers.StatsMapper;
import space.pegman.redditMod.service.DbInteractions.WordsService;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AnomalyTriggerService {

    @Autowired
    private StatsMapper statsMapper;

    @Autowired
    private WordsService wordsService;

    final double THRESHOLD = 0.0000001;

    /*
        Checks if a comment is an anomaly from a set by using the independent gausian probability to detect anomalies.
        Uses logs to avoid double underflow.
     */
    public boolean isAnomaly(RedditComment redditComment, CommentSampleSet set){
        String[] words = wordsService.splitRedditComment(redditComment);
        HashMap<String, Integer> wordCounts = wordsService.countWords(words);

        double totalLogProbability = Math.log(1);
        for(Map.Entry<String, Integer> wordCount: wordCounts.entrySet()){
            final String word = wordCount.getKey();
            final Integer count = wordCount.getValue();
            final Stats stats = statsMapper.getStats(word, set.getId());

            if(stats!= null) {
                final double mean = stats.getMeanCount();
                final double stdDev = stats.getStdDev();
                final double probability = Util.standardize(count, mean, stdDev);

                totalLogProbability += Math.log(probability);
            } else {
                totalLogProbability += Math.log(0.01);
            }
        }

        return totalLogProbability<=(Math.log(THRESHOLD)*Math.log(words.length));
    }


}
