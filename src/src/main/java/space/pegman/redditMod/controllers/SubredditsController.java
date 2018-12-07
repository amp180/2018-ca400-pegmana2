package space.pegman.redditMod.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import space.pegman.redditMod.domain.Database.Rule;
import space.pegman.redditMod.domain.Database.Subreddit;
import space.pegman.redditMod.mappers.RulesMapper;
import space.pegman.redditMod.mappers.SubredditMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@RestController
public class SubredditsController {

    @Autowired
    SubredditMapper subredditMapper;

    @Autowired
    RulesMapper rulesMapper;


    @RequestMapping(value="/subreddits", produces="application/json")
    @ResponseBody
    Subreddit[] getSubreddits(){
        return subredditMapper.getSubreddits();
    }


    @PostMapping(value="/subreddits", produces="application/json", consumes="application/json")
    void createSubreddit(@RequestBody CreateSubredditRequest createRequest, HttpServletResponse response) throws IOException {
        final Pattern linkPattern = Pattern.compile("http(s)?://(www\\.)?reddit.com(/r/[0-9a-z_\\.]+)/?.*");
        String sub = createRequest.getSubreddit();
        sub = cleanSubredditName(sub);

        if(sub==null) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "malformed subreddit");
        } else {
            subredditMapper.insertSubreddit(new Subreddit(sub));
            response.setStatus(HttpStatus.CREATED.value());
        }
    }


    @DeleteMapping(value="/subreddits", produces="application/json", consumes="application/json")
    void deleteSubreddit(@RequestBody DeleteSubredditRequest deleteRequest){
        final Rule[] rules = rulesMapper.getRulesBySubreddit(deleteRequest.getSubreddit());

        for(Rule rule : rules){
            rulesMapper.deleteRule(rule);
        }

        subredditMapper.deleteSubreddit(new Subreddit(deleteRequest.getSubreddit()));
    }


    static String cleanSubredditName(String sub) {
        final Pattern linkPattern = Pattern.compile("https?://(www\\.)?reddit.com(/r/[0-9a-z_\\.]+)/?.*");

        if (sub == null) {
            return null;
        }

        //remove trailing /
        while (sub.endsWith("/")) {
            sub = sub.substring(0, sub.length() - 1);
        }

        //handle link to sub
        if (sub.startsWith("http://") || sub.startsWith("https://")) {
            Matcher linkMatcher = linkPattern.matcher(sub.trim());
            if (linkMatcher.find()) {
                return linkMatcher.group(2);
            }
            //handle sub name
        } else if (sub.startsWith("/r/")) {
            return sub;
        }

        return null;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class CreateSubredditRequest {
        @JsonProperty("subreddit") String subreddit;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class DeleteSubredditRequest {
        @JsonProperty("subreddit") String subreddit;
    }

}
