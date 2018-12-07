package space.pegman.redditMod.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import space.pegman.redditMod.controllers.ActionsController.ActionCreateRequest;
import space.pegman.redditMod.controllers.ActionsController.ActionDeleteRequest;
import space.pegman.redditMod.domain.Database.Action;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActionsControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    Action[] actions;

    @Before
    public void testCreateAction() {

        ActionCreateRequest createRequest = new ActionCreateRequest("name", 0, "message");

        ResponseEntity<Object> res = restTemplate.postForEntity("/actions", createRequest, Object.class);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }


    @Test
    public void testGetActions() {
        ResponseEntity<Action[]> res = restTemplate.getForEntity("/actions", null, Action[].class);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        actions = res.getBody();
    }


    @After
    public void testRemoveAction() {
        if(actions != null){
            for(Action action : actions){
                ActionDeleteRequest request = new ActionDeleteRequest (action.getId());
                HttpEntity<ActionDeleteRequest> entity = new HttpEntity<>(request);
                ResponseEntity<Object> res = restTemplate.exchange("/actions", HttpMethod.DELETE, entity, Object.class);
                assertEquals(HttpStatus.OK, res.getStatusCode());
            }
        }
    }

}
