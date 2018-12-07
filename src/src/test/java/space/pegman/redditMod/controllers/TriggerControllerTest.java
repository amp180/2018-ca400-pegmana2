package space.pegman.redditMod.controllers;

import com.sun.research.ws.wadl.HTTPMethods;
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
import space.pegman.redditMod.controllers.TriggerController.TriggerCreateRequest;
import space.pegman.redditMod.controllers.TriggerController.TriggerDeleteRequest;
import space.pegman.redditMod.domain.Database.CommentSampleSet;
import space.pegman.redditMod.domain.Database.Trigger;
import space.pegman.redditMod.mappers.CommentSampleSetMapper;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TriggerControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CommentSampleSetMapper sampleSetMapper;

    Long sampleSetId;
    Trigger[] triggers;

    @Before
    public void testCreateTrigger() {
        CommentSampleSet sampleSet = new CommentSampleSet(null, "set", false, null);
        sampleSetMapper.insertSampleSet(sampleSet);
        sampleSetId = sampleSet.getId();

        TriggerCreateRequest createRequest =
                new TriggerCreateRequest(
                        "triggerName",
                        Trigger.Types.BAYSIAN,
                        "",
                        sampleSetId,
                        sampleSetId
                );

        ResponseEntity<Object> res = restTemplate.postForEntity("/triggers", createRequest, Object.class);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }


    @Test
    public void testGetTriggers() {
        ResponseEntity<Trigger[]> res = restTemplate.getForEntity("/triggers", null, Trigger[].class);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        triggers = res.getBody();
    }


    @After
    public void removeDeleteTrigger() {
        sampleSetMapper.deleteSampleSet(new CommentSampleSet(sampleSetId, null, false,null));
        if(triggers != null){
            for(Trigger trigger : triggers){
                TriggerDeleteRequest request = new TriggerDeleteRequest(trigger.getId());
                HttpEntity<TriggerDeleteRequest> entity = new HttpEntity<>(request);
                ResponseEntity<Object> res = restTemplate.exchange("/triggers", HttpMethod.DELETE,  entity, Object.class);
                assertEquals(HttpStatus.OK, res.getStatusCode());
            }
        }
    }

}
