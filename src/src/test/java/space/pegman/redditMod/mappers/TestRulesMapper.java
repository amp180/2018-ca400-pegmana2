package space.pegman.redditMod.mappers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import space.pegman.redditMod.domain.Database.Rule;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRulesMapper {


    @Autowired
    RulesMapper mapper;

    Rule rule;
    Long id;

    Long triggerId = 0L;
    Long actionId = 0L;

    @Before
    public void insertCommentSampleSet(){
        this.rule = new Rule(null, "testRule", triggerId, actionId, "/r/sub");
        this.id = mapper.insertRule(rule);
    }

    @Test
    public void getCommentSampleSetByName(){
        Rule[] rules = mapper.getRulesBySubreddit("/r/sub");

        int i;
        for(i = 0; i<rules.length; i++){
            if(rule.equals(rules[i])) break;
        }

        Assert.assertEquals(rule, mapper.getRulesBySubreddit("/r/sub")[i]);
    }

    @Test
    public void updateCommentSampleSet(){
        rule.setSub("/r/cats");
        Assert.assertEquals(1L, mapper.updateRule(rule).longValue());
    }

    @After
    public void deleteCommentSampleSet() {
        Assert.assertEquals(1L, mapper.deleteRule(rule).longValue());
    }


}
