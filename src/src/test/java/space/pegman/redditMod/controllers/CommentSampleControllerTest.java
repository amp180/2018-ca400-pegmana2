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
import space.pegman.redditMod.controllers.CommentSampleController.SampleSetCreateRequest;
import space.pegman.redditMod.controllers.CommentSampleController.SampleSetDeleteRequest;
import space.pegman.redditMod.domain.Database.CommentSampleSet;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentSampleControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    CommentSampleSet[] sets;

    @Before
    public void testCreateSet() {
        SampleSetCreateRequest createRequest = new SampleSetCreateRequest("setName", "http://www.reddit.com/r/cats", 5);

        ResponseEntity<Object> res = restTemplate.postForEntity("/commentSampleSets", createRequest, Object.class);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }


    @Test
    public void testGetSet() {
        ResponseEntity<CommentSampleSet[]> res = restTemplate.getForEntity("/commentSampleSets", null, CommentSampleSet[].class);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        sets = res.getBody();
    }


    @After
    public void testRemoveSet() {
        if(sets != null){
            for(CommentSampleSet set : sets){
                SampleSetDeleteRequest request = new SampleSetDeleteRequest(set.getId());
                HttpEntity<SampleSetDeleteRequest> entity = new HttpEntity<>(request);
                ResponseEntity<Object> res = restTemplate.exchange("/commentSampleSets", HttpMethod.DELETE, entity, Object.class);
                assertEquals(HttpStatus.OK, res.getStatusCode());
            }
        }
    }

}
