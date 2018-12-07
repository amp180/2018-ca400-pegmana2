package space.pegman.redditMod.service.DbInteractions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import space.pegman.redditMod.domain.Database.Comment;
import space.pegman.redditMod.domain.Database.CommentSampleSet;
import space.pegman.redditMod.domain.Database.Stats;
import space.pegman.redditMod.domain.Database.WordCount;
import space.pegman.redditMod.mappers.CommentSampleSetMapper;
import space.pegman.redditMod.mappers.CommentsMapper;
import space.pegman.redditMod.mappers.StatsMapper;
import space.pegman.redditMod.mappers.WordCountMapper;
import space.pegman.redditMod.service.Monitoring.Triggers.KNearestTriggerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.pow;

@Service
@Slf4j
public class StatsService {

    @Autowired
    private WordCountMapper wordCountMapper;

    @Autowired
    private StatsMapper statsMapper;

    @Autowired
    private CommentSampleSetMapper commentSampleSetMapper;


    public void calculateStatsForCommentSet(CommentSampleSet sampleSet){
        WordCount[] dbWordCounts = wordCountMapper.getWordCountsByCommentSet(sampleSet);
        MultiValueMap<String, Long> wordCounts = new LinkedMultiValueMap<>();

        for(WordCount dbWordCount : dbWordCounts){
            final String word = dbWordCount.getWord();
            wordCounts.add(word, dbWordCount.getCount());
        }

        for(MultiValueMap.Entry<String, List<Long>> entry : wordCounts.entrySet()){
            final String word = entry.getKey();
            final List<Long> counts = entry.getValue();
            final CommentSampleSet updatedSet = commentSampleSetMapper.getCommentSampleSetById(sampleSet.getId());
            final int numCommentsWithoutWord = updatedSet.getNumComments() - counts.size();
            counts.addAll(Collections.nCopies(numCommentsWithoutWord, 0L));

            final double wordMedian = median_l(counts);
            final long wordSum = sum_l(counts);
            final double wordMean = (double)(wordSum)/(double)(counts.size());
            final double wordStdDev = stdDev(counts, wordMean);
            final double medianAbsoluteDeviation = Math.sqrt(medianDistFrom(counts, wordMedian));

            Stats dbStats = new Stats(
                    word,
                    sampleSet.getId(),
                    wordSum,
                    wordMean,
                    wordMedian,
                    wordStdDev,
                    medianAbsoluteDeviation
            );

            statsMapper.insertStats(dbStats);
        }


    }


    public void deleteStatsForCommentSet(CommentSampleSet sampleSet) {
        statsMapper.deleteStatsBySampleSet(sampleSet);
    }


    protected Long sum_l(List<Long> values) {
        long sum = 0l;

        for(final Long v : values) {
            sum += v;
        }

        return sum;
    }

    protected double variance(List<Long> values, double mean){
        double sum = 0l;

        for(long value : values){
            sum += pow((double)(value)-mean, 2);
        }

        return sum/values.size();
    }

    protected double stdDev(List<Long> values, double mean){
        return Math.sqrt(variance(values, mean));
    }


    protected double median_l(List<Long> values){
        values.sort(Long::compareTo);
        final int halfway = values.size()/2;

        if(values.size()%2==0){
            return (double)(values.get(halfway)+(values.get(halfway-1)))/2;
        } else {
            return values.get(halfway);
        }
    }


    protected double median_d(List<Double> values) {
        values.sort(Double::compareTo);
        final int halfway = values.size()/2;

        if(values.size()%2==0){
            return (values.get(halfway)+(values.get(halfway-1)))/2;
        } else {
            return values.get(halfway);
        }
    }


    protected double medianDistFrom(List<Long> values, double from){
        ArrayList<Double> distances = new ArrayList<>();

        for(Long value:values){
            distances.add(Math.abs((double)(value)-from));
        }

        return median_d(distances);
    }

}
