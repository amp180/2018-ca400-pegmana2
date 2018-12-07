package space.pegman.redditMod.service.DbInteractions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.pegman.redditMod.domain.Database.Comment;
import space.pegman.redditMod.domain.Database.CommentSampleSet;
import space.pegman.redditMod.domain.Database.Word;
import space.pegman.redditMod.domain.Database.WordCount;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditComment;
import space.pegman.redditMod.mappers.WordCountMapper;
import space.pegman.redditMod.mappers.WordsMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Service
@Slf4j
public class WordsService {
    private final ArrayList<String> stopWords;
    private final WordCountMapper wordCountMapper;
    private final WordsMapper wordsMapper;

    @Autowired
    public WordsService(WordCountMapper wordCountMapper, WordsMapper wordsMapper){
        this.wordCountMapper = wordCountMapper;
        this.wordsMapper = wordsMapper;

        try(Scanner s = new Scanner(WordsService.class.getResourceAsStream("/stopwords.txt"))) {
            this.stopWords = new ArrayList<>();

            while (s.hasNext()) {
                stopWords.add(s.nextLine());
            }
        } catch (Exception e ){
            log.error("Getting stopwords failed.", e);
            throw e;
        }

    }

    public void addWords(String[] words, Comment comment){
        HashMap<String, Integer> wordCounts = countWords(words);

        for(Map.Entry<String,Integer> wordCount:wordCounts.entrySet()){
            final String word = wordCount.getKey();
            final int count = wordCount.getValue();
            wordsMapper.insertWord(new Word(word));

            WordCount dbWordCount = new WordCount(null, comment.getId(), word, (long)(count));
            log.trace("Inserting comment {}", comment.getRedditId());
            wordCountMapper.insertWordCount(dbWordCount);
        }

    }

    public HashMap<String, Integer> countWords(String[] words){
        HashMap<String, Integer> wordCounts = new HashMap<>();

        for(String word: words){
            wordCounts.put(word, wordCounts.getOrDefault(word, 0)+1);
        }

        return wordCounts;
    }

    public String[] splitRedditComment(RedditComment comment){
        final String commentBody = comment.getBody();
        log.trace("comment: {}", commentBody);

        String cleanedBody = commentBody.replaceAll("\\p{Punct}", "");
        for(String stopWord: stopWords){
            cleanedBody = cleanedBody.replaceAll("\\b"+stopWord+"\\b", " ");
        }

        cleanedBody = cleanedBody.replaceAll("\\s+", " ");
        log.trace("cleanedBody: {}", cleanedBody);
        return cleanedBody.split(" ");
    }

    public void deleteWordCountsbyCommentSample(CommentSampleSet commentSet){
        wordCountMapper.deleteWordCountsBySampleSet(commentSet);
    }
}
