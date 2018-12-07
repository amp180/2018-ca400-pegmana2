package space.pegman.redditMod.service.Monitoring;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import space.pegman.redditMod.domain.Database.Trigger;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditComment;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TriggerServiceTest {

    @Autowired
    TriggerService triggerService;

    @Test
    public void testCheckTrigger(){
        Trigger t = new Trigger(null, "", Trigger.Types.REGEX, "matc*h", null, null);
        RedditComment c = new RedditComment();
        c.setBody("matcccch");

        assert triggerService.checkTrigger(t, c);
    }
}
