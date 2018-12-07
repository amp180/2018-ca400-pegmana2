package space.pegman.redditMod.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import space.pegman.redditMod.domain.Database.CommentSampleSet;
import space.pegman.redditMod.mappers.CommentSampleSetMapper;
import space.pegman.redditMod.service.CommentFetching.CommentFetcherSchedulingService;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Slf4j
@RestController
public class CommentSampleController {

    @Autowired
    CommentSampleSetMapper commentSampleSetMapper;

    @Autowired
    CommentFetcherSchedulingService fetcher;


    @GetMapping(value="/commentSampleSets", produces="application/json")
    @ResponseBody
    public CommentSampleSet[] getCommentSampleSets(){
        return commentSampleSetMapper.getCommentSampleSets();
    }


    @PostMapping(value="/commentSampleSets", produces="application/json", consumes="application/json")
    public void create(@RequestBody SampleSetCreateRequest json, HttpServletResponse response) {
        final String cleanedUrl = cleanRedditUrl(json.getUrl());
        fetcher.spawnFetchThread(json.getName(), cleanedUrl, json.getCount());
        response.setStatus(HttpStatus.CREATED.value());
    }


    @DeleteMapping(value="/commentSampleSets", produces="application/json", consumes="application/json")
    public void delete(@RequestBody SampleSetDeleteRequest json){
        CommentSampleSet sampleSet = commentSampleSetMapper.getCommentSampleSetById(json.getId());
        sampleSet.setDone(false);
        commentSampleSetMapper.updateSampleSet(sampleSet);
        fetcher.spawnDeleteSetThread(sampleSet);
    }


    public String cleanRedditUrl(String url) {
        return url.replaceAll("http.?://(www.)?", "https://oauth.") + ".json";
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class SampleSetCreateRequest {
        @JsonProperty("name") private String name;
        @JsonProperty("url") private String url;
        @JsonProperty("count") private Integer count;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class SampleSetDeleteRequest {
        @JsonProperty("id") private Long id;
    }

}
