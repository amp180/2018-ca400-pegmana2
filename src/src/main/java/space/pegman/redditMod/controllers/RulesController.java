package space.pegman.redditMod.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.pegman.redditMod.domain.Database.Rule;
import space.pegman.redditMod.domain.Database.Subreddit;
import space.pegman.redditMod.mappers.RulesMapper;
import space.pegman.redditMod.mappers.SubredditMapper;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Slf4j
@RestController
public class RulesController {

    @Autowired
    RulesMapper rulesMapper;

    @Autowired
    SubredditMapper subredditMapper;


    @RequestMapping(value="/rules", produces="application/json")
    @ResponseBody
    Rule[] getRules(){
        return rulesMapper.getRules();
    }

    @PostMapping(value="/rules", produces="application/json", consumes="application/json")
    void createRule(@RequestBody CreateRuleRequest ruleRequest, HttpServletResponse response){

        if (subredditMapper.getSubreddit(ruleRequest.getSub()) == null) {
            subredditMapper.insertSubreddit(new Subreddit(ruleRequest.getSub()));
        }

        rulesMapper.insertRule(new Rule(null, ruleRequest.getName(), ruleRequest.getTriggerId(), ruleRequest.getActionId(), ruleRequest.getSub()));
        response.setStatus(HttpStatus.CREATED.value());
    }


    @DeleteMapping(value="/rules", produces="application/json", consumes="application/json")
    void deleteRule(@RequestBody DeleteRuleRequest deleteRequest){
        final Rule rule = rulesMapper.getRuleById(deleteRequest.getId());

        rulesMapper.deleteRule(rule);

        if(rulesMapper.getRulesBySubreddit(rule.getSub()).length==0){
            subredditMapper.deleteSubreddit(new Subreddit(rule.getSub()));
        }

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class CreateRuleRequest {
        @JsonProperty("sub") String sub;
        @JsonProperty("name") String name;
        @JsonProperty("triggerId") Long triggerId;
        @JsonProperty("actionId") Long actionId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class DeleteRuleRequest {
        @JsonProperty("id") Long id;
    }

}
