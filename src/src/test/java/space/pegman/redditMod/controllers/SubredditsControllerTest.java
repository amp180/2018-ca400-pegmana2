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
import space.pegman.redditMod.controllers.SubredditsController.CreateSubredditRequest;
import space.pegman.redditMod.controllers.SubredditsController.DeleteSubredditRequest;

import space.pegman.redditMod.domain.Database.Subreddit;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SubredditsControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    Subreddit[] subs;

    @Before
    public void testCreateSub() {
        assertEquals("/r/cats", SubredditsController.cleanSubredditName("http://reddit.com/r/cats///"));
        assertEquals("/r/cats", SubredditsController.cleanSubredditName("https://reddit.com/r/cats///"));
        assertEquals("/r/cats", SubredditsController.cleanSubredditName("/r/cats///"));

        CreateSubredditRequest createRequest = new CreateSubredditRequest("https://www.reddit.com/r/cats");
        ResponseEntity<Object> res = restTemplate.postForEntity("/subreddits", createRequest, Object.class);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }


    @Test
    public void testGetSubs() {
        ResponseEntity<Subreddit[]> res = restTemplate.getForEntity("/subreddits", null, Subreddit[].class);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        subs = res.getBody();
    }

    @After
    public void testRemoveSub() {
        if (subs != null) {
            for (Subreddit sub : subs) {
                DeleteSubredditRequest request = new DeleteSubredditRequest(sub.getSub());
                HttpEntity<DeleteSubredditRequest> entity = new HttpEntity<>(request);
                ResponseEntity<Object> res = restTemplate.exchange("/subreddits", HttpMethod.DELETE, entity, Object.class);
                assertEquals(HttpStatus.OK, res.getStatusCode());
            }
        }
    }
}
