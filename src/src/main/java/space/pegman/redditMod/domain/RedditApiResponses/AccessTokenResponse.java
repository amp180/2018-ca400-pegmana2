package space.pegman.redditMod.domain.RedditApiResponses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
/*
    Domain object to hold response from reddit access_token request.
    https://github.com/reddit-archive/reddit/wiki/OAuth2#application-only-oauth
 */
public class AccessTokenResponse {
    @JsonProperty("access_token")   String accessToken;
    @JsonProperty("token_type")     String tokenType;
    @JsonProperty("device_id")      String deviceId;
    @JsonProperty("expires_in")     long expiresIn;
    @JsonProperty("scope")          String scope;
}
