package space.pegman.redditMod.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import space.pegman.redditMod.domain.RedditApiResponses.AccessTokenResponse;
import space.pegman.redditMod.mappers.SettingsMapper;

import java.util.Base64;
import java.util.List;

@Component
@Slf4j
/*
    Class to fetch reddit auth tokens.
 */
public class RedditAuthTokenDao {
    //constants

    private static final String authTokenUrl = "https://www.reddit.com/api/v1/access_token";
    private static final String userAgent = "FYP";
    private static final String grantType = "password";
    private static final String allScopes = "identity edit flair history modself modconfig modflair modlog modposts modwiki mysubreddits privatemessages read report save submit subscribe vote wikiedit wikiread";


    private final RestTemplate restTemplate;
    private final SettingsMapper settingsMapper;
    private String defaultClientId = "<revoked>";
    private String defaultClientSecret = "<revoked_key_went_here>";
    private String defaultUsername = "";
    private String defaultPassword = "";



    //Store last login token;
    private AccessTokenResponse tokenRes;
    private Long loginTimeStamp;

    //Store rate information
    private Float ratelimitRemaining;
    private Float rateLimitReset;
    private Long rateLimitTimestamp;

    @Autowired
    public RedditAuthTokenDao(RestTemplate restTemplate, SettingsMapper settingsMapper){
        this.restTemplate = restTemplate;
        this.settingsMapper = settingsMapper;
    }


    public HttpHeaders getOAuthHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.USER_AGENT, userAgent);
        headers.add("Authorization", "bearer " + tokenRes.getAccessToken());
        return headers;
    }

    public void login(){

        while(true) {
            //Build headers for client credentials auth
            HttpHeaders loginHeaders = new HttpHeaders();
            loginHeaders.add(HttpHeaders.USER_AGENT, "FYP");
            loginHeaders.add(HttpHeaders.AUTHORIZATION, "Basic " + getAuthHeader());

            //Build form data
            MultiValueMap<String, String> authPostData = new LinkedMultiValueMap<String, String>();
            authPostData.add("grant_type", grantType);
            authPostData.add("username", getUsername());
            authPostData.add("password", getPassword());
            authPostData.add("scope", allScopes);

            HttpEntity<Object> authEntity = new HttpEntity<>(authPostData, loginHeaders);
            long timestamp = getTimestamp();
            ResponseEntity<AccessTokenResponse> res = restTemplate.postForEntity(authTokenUrl, authEntity, AccessTokenResponse.class);

            //store rate limit
            HttpHeaders resHeaders = res.getHeaders();
            storeRateLimit(resHeaders, timestamp);

            synchronized (RedditCommentDao.class) {
                if(res.getStatusCodeValue() == 200) {
                    tokenRes = res.getBody();
                    loginTimeStamp = timestamp;
                    log.info(tokenRes.toString());
                    break;
                } else {
                    log.error("login failed with status code {}, retying.", res.getStatusCodeValue());
                }
            }
        }
    }


    public boolean isLoggedIn(){
        return (tokenRes!=null) && (getTimestamp() < (tokenRes.getExpiresIn() + loginTimeStamp) );
    }


    public synchronized void storeRateLimit(HttpHeaders headers, Long timestamp){
        if(headers.containsKey("x-ratelimit-remaining")) {
            ratelimitRemaining = Float.parseFloat(headers.get("x-ratelimit-remaining").get(0));
            rateLimitReset = Float.parseFloat(headers.get("x-ratelimit-reset").get(0));
            rateLimitTimestamp = timestamp;
        }
    }


    public synchronized void obeyRateLimit() throws InterruptedException {
        if(ratelimitRemaining == null) return;
        if(ratelimitRemaining-- <= 0) {
            sleep(getTimestamp() - (long)(rateLimitTimestamp + rateLimitReset + 1));
        }
    }


    public void handleRateLimitExceeded(HttpHeaders responseHeaders) throws InterruptedException {
        List<String> retryAfter = responseHeaders.get("Retry-After");
        //Wait if asked.
        if(retryAfter.size()>0) {
            sleep(Long.parseLong(retryAfter.get(0)));
        } else {
            obeyRateLimit();
        }
    }


    private String getClientId(){
        String dbCid = settingsMapper.getSetting("clientId");
        if(dbCid != null){
            return dbCid;
        }
        return defaultClientId;
    }


    private String getClientSecret(){
        String dbCs = settingsMapper.getSetting("clientSecret");
        if(dbCs != null){
            return dbCs;
        }
        return defaultClientSecret;

    }


    private String getUsername(){
        String dbuser = settingsMapper.getSetting("username");
        if(dbuser != null){
            return dbuser;
        }
        return defaultUsername;
    }


    private String getPassword(){
        String dbpass = settingsMapper.getSetting("password");
        if(dbpass != null){
            return dbpass;
        }
        return defaultPassword;

    }


    private String getAuthHeader() {
        return b64(getClientId() + ":" + getClientSecret());
    }


    public long getTimestamp(){
        return System.currentTimeMillis() / 1000L;
    }


    private static void sleep(Long seconds) throws InterruptedException {
            log.info("Sleeping {} seconds.", seconds);
            Thread.sleep(seconds * 1000);
    }


    private static String b64(String b){
        return Base64.getUrlEncoder().encodeToString(b.getBytes());
    }
}
