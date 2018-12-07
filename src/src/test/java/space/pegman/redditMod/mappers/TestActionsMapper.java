package space.pegman.redditMod.mappers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import space.pegman.redditMod.domain.Database.Action;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestActionsMapper {

    @Autowired
    ActionsMapper mapper;

    Action action;
    Long id;

    @Before
    public void insertAction() {
        this.action = new Action(null, "name", 0, "");
        this.id = mapper.insertAction(this.action);
    }

    @Test
    public void updateAction(){
        this.action.setType(2);
        Assert.assertNotEquals(0L, this.mapper.updateAction(this.action).longValue());
    }

    @After
    public void deleteAction(){
        Assert.assertNotEquals(0, this.mapper.deleteAction(this.action).longValue());
    }
}
