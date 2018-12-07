package space.pegman.redditMod.dao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThing;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditMore;
import space.pegman.redditMod.domain.RedditCommentAccumulator;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Integer.min;


@Component
@Slf4j
/*
    Class to fetch and post requests about reddit comments.
 */
public class RedditCommentDao {

    //constants
    public static final String baseUrl = "https://oauth.reddit.com";
    private static final String commentUrl = baseUrl+ "/api/comment";
    private static final String commentRemovalUrl = baseUrl+"/api/remove";
    private static final String moreChildrenUrl = baseUrl+"/api/morechildren";
    private static final String banUrl = baseUrl+"/api/friend";
    private static final String approveUrl =  baseUrl+"/api/approve";
    private static final Lock moreChildrenLock = new ReentrantLock();

    //members
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final RedditAuthTokenDao auth;

    @Autowired
    public RedditCommentDao(RedditAuthTokenDao auth, RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.auth = auth;

        this.mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }


    public RedditThing[] getThings(String thingUrl) throws InterruptedException {
        final Object resObj = get(thingUrl, null);
        if(resObj == null){
            return null;
        }
        return mapper.convertValue(resObj, RedditThing[].class);
    }


    public boolean postReply(String replyToId, String text) throws InterruptedException {

        final String replyUri = UriBuilder.fromUri(commentUrl)
                .queryParam("return_rtjson", false)
                .queryParam("thing_id", replyToId)
                .build()
                .toString()
                +"&text="+text;

        ResponseEntity<Object> response = post(replyUri, null, Object.class);
        return response.getStatusCodeValue() == 200;
    }


    public boolean removeComment(String commentId) throws InterruptedException {

        final String populatedRemovalUrl = UriBuilder.fromUri(commentRemovalUrl)
                .queryParam("api_type", "json")
                .queryParam("id", commentId)
                .build().toString();

        ResponseEntity<Object> response = post(populatedRemovalUrl, null, Object.class);
        return response.getStatusCodeValue() == 200;
    }

    public boolean banUserForComment(final String author, final String subReddit) throws InterruptedException {

        final String populatedBanUrl = UriBuilder.fromUri(banUrl)
                .queryParam("api_type", "json")
                .queryParam("action", "add")
                .queryParam("type", "banned")
                .queryParam("id", "#banned")
                .queryParam("name", author)
                .queryParam("r", subReddit)
                .build().toString();

        ResponseEntity<Object> response = post( populatedBanUrl , null, Object.class);
        return response.getStatusCodeValue() == 200;
    }

    public boolean approveComment(String commentId) throws InterruptedException {
        final String populatedApproveUrl = UriBuilder.fromUri(approveUrl)
                .queryParam("id", commentId)
                .build().toString();

        ResponseEntity<Object> response = post( populatedApproveUrl , null, Object.class);
        return response.getStatusCodeValue() == 200;
    }


    public RedditThing[] getMoreChildren(RedditMore more) throws InterruptedException {
        final String linkId = more.getLinkId();
        final String id = more.getId();
        final ArrayList<String> moreIds = more.getChildren();
        final ArrayList<RedditThing> things = new ArrayList<>();

        for(int i=0; i<moreIds.size(); i += 100){
            things.addAll(
                    Arrays.asList(
                            getMoreChildren(
                                    linkId,
                                    id,
                                    moreIds.subList(i, min(i+100, moreIds.size()))
                            )
                    )
            );
        }

        return things.toArray(new RedditThing[things.size()]);
    }


    public RedditThing[] getMoreChildren(String linkId, String id, List<String> children) throws InterruptedException {
        final URI moreUri = UriBuilder.fromUri(moreChildrenUrl)
                .queryParam("api_type", "json")
                .queryParam("id", id)
                .queryParam("children", String.join(",", children))
                .queryParam("limit_children", true)
                .queryParam("link_id", linkId)
                .build();

        //Reddit api rules say that moreChildren should only be called once concurrently.
        moreChildrenLock.lockInterruptibly();
        Object resObj = get(moreUri.toString(), null);
        moreChildrenLock.unlock();

        return mapper.convertValue(resObj, RedditThing[].class);
    }


    private <T> ResponseEntity<T> post(String url, Object obj, Class<T> respClass) throws InterruptedException {

        //log in
        if(!auth.isLoggedIn()){
            auth.login();
        }

        //Wait if api limit exceeded;
        auth.obeyRateLimit();

        Long timestamp = auth.getTimestamp();

        //encode json body
        String body = null;
        try {
            body = mapper.writeValueAsString(obj);
        } catch (Exception e){
            log.error(e.getLocalizedMessage());
        }

        //Get Headers
        HttpHeaders headers = auth.getOAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //Post
        HttpEntity<String> replyEntity = new HttpEntity<>(body, headers);
        ResponseEntity<T> response = restTemplate.postForEntity(url, replyEntity, respClass);

        //Update rate limit
        auth.storeRateLimit(response.getHeaders(), timestamp);

        return response;
    }


    private Object get(String url, Object obj) throws InterruptedException {

        //log in
        if(!auth.isLoggedIn()){
            auth.login();
        }

        //Wait if api limit exceeded;
        auth.obeyRateLimit();

        //Make req.
        HttpHeaders headers = auth.getOAuthHeaders();

        Long timestamp = auth.getTimestamp();
        HttpEntity<Object> entity = new HttpEntity<>(obj, headers);
        ResponseEntity<Object> r = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);

        //store rate limit usage information.
        HttpHeaders resHeaders = r.getHeaders();
        auth.storeRateLimit(resHeaders, timestamp);

        //handle response codes
        if (r.getStatusCodeValue() == 429){
            log.debug("Rate limit exceeded, retrying.");
            auth.handleRateLimitExceeded(headers);
            //retry
            return get(url, obj);
        } else if (r.getStatusCodeValue() > 299) {
            log.error("http error: {}", r.getStatusCodeValue());
            return null;
        }

        return r.getBody();
    }

}
