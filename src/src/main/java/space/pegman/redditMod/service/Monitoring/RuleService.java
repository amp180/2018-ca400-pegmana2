package space.pegman.redditMod.service.Monitoring;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.pegman.redditMod.domain.Database.Action;
import space.pegman.redditMod.domain.Database.Rule;
import space.pegman.redditMod.domain.Database.Trigger;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditComment;
import space.pegman.redditMod.mappers.ActionsMapper;
import space.pegman.redditMod.mappers.RulesMapper;
import space.pegman.redditMod.mappers.TriggersMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Slf4j
@Service
public class RuleService {

    @Autowired
    RulesMapper rulesMapper;

    @Autowired
    ActionsMapper actionsMapper;

    @Autowired
    TriggersMapper triggersMapper;

    @Autowired
    TriggerService triggerService;

    @Autowired
    ActionService actionService;


    /*
        Gets the rules for a subreddit and applies them to a list of comments.
     */
    public void applyRulesBySubreddit(String subreddit, Set<RedditComment> comments){
        HashMap<Trigger, Action> rules = getRulesForSubreddit(subreddit);

        if(rules==null || comments==null) {return;}

        for (RedditComment comment : comments){
            for(Map.Entry<Trigger, Action> rule : rules.entrySet()) {
                final Trigger trigger = rule.getKey();
                final Action action = rule.getValue();

                final boolean stop = applyRuleToComment(trigger, action, comment);

                if (stop) {
                    break;
                }
            }
        }
    }

    /*
        Applies rule to comment, returns true if no more rules should be applied.
     */
    public boolean applyRuleToComment(Trigger trigger, Action action, RedditComment redditComment) {
        final boolean applies = triggerService.checkTrigger(trigger, redditComment);

        return applies && actionService.takeAction(action, redditComment);
    }


    /*
        Fetches trigger:action associations into a hashmap.
     */
    public HashMap<Trigger, Action> getRulesForSubreddit(String subreddit){
        final Rule[] rules = rulesMapper.getRulesBySubreddit(subreddit);
        final HashMap<Trigger, Action> ruleMap = new HashMap<>();

        for(Rule rule: rules){
            final Trigger t = triggersMapper.getTriggerById(rule.getTrigger());
            final Action a = actionsMapper.getActionById(rule.getAction());

            ruleMap.put(t, a);
        }

        return ruleMap;
    }

}
