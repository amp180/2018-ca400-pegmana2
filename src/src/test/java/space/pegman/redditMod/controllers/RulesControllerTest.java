package space.pegman.redditMod.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import space.pegman.redditMod.controllers.RulesController.CreateRuleRequest;
import space.pegman.redditMod.controllers.RulesController.DeleteRuleRequest;
import space.pegman.redditMod.domain.Database.*;
import space.pegman.redditMod.mappers.ActionsMapper;
import space.pegman.redditMod.mappers.SubredditMapper;
import space.pegman.redditMod.mappers.TriggersMapper;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class RulesControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    TriggersMapper triggersMapper;

    @Autowired
    ActionsMapper actionsMapper;

    @Autowired
    SubredditMapper subredditMapper;

    Rule[] rules;
    Long triggerId;
    Long actionId;
    String sub;

    @Before
    public void testCreateRule() {
        Trigger t = new Trigger(null, "ruleTriggerName", Trigger.Types.ALL, "", null, null);
        triggersMapper.insertTrigger(t);
        triggerId = t.getId();

        Action a = new Action(null, "ruleActionName", Action.Types.APPROVE, "");
        actionsMapper.insertAction(a);
        actionId = a.getId();

        Subreddit s = new Subreddit("/r/dogs");
        sub = s.getSub();

        CreateRuleRequest createRequest = new CreateRuleRequest(sub, "ruleName", triggerId, actionId);

        ResponseEntity<Object> res = restTemplate.postForEntity("/rules", createRequest, Object.class);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }


    @Test
    public void testGetRule() {
        ResponseEntity<Rule[]> res = restTemplate.getForEntity("/rules", null, Rule[].class);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        rules = res.getBody();
    }


    @After
    public void testRemoveRule() {
        if(rules != null){
            for(Rule rule : rules){
                DeleteRuleRequest request = new DeleteRuleRequest(rule.getId());
                HttpEntity<DeleteRuleRequest> entity = new HttpEntity<>(request);
                ResponseEntity<Object> res = restTemplate.exchange("/rules", HttpMethod.DELETE, entity, Object.class);
                assertEquals(HttpStatus.OK, res.getStatusCode());
            }
        }

        triggersMapper.deleteTrigger(triggerId);
        actionsMapper.deleteAction(new Action(actionId, null, null, null));
        subredditMapper.deleteSubreddit(new Subreddit(sub));
    }

}
