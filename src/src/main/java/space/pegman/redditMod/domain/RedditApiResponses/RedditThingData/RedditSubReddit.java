package space.pegman.redditMod.domain.RedditApiResponses.RedditThingData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import space.pegman.redditMod.domain.RedditCommentAccumulator;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
/*
    Domain class for reddit subreddit json data type.
 */
public class RedditSubReddit extends RedditThingData {

    @JsonProperty("accounts_active") int accountsActive;
    @JsonProperty("comment_score_hide_mins")  int commentScoreHideMins; //minutes
    @JsonProperty("description") String  description;
    @JsonProperty("description_html") String descriptionHtml;
    @JsonProperty("display_name") String displayName;
    @JsonProperty("header_img") String headerImg;
    @JsonProperty("header_size") int[] headerSize;
    @JsonProperty("header_title") String headerTitle;
    @JsonProperty("over18") boolean over18;
    @JsonProperty("public_description") String publicDescription;
    @JsonProperty("public_traffic") boolean publicTraffic;
    @JsonProperty("subscribers") long subscribers;
    @JsonProperty("submission_type") String submissionType;
    @JsonProperty("submit_link_label") String submitLinkLabel;
    @JsonProperty("submit_text_label") String submitTextLabel;
    @JsonProperty("subreddit_type") String subredditType;
    @JsonProperty("title") String title;
    @JsonProperty("url") String url;
    @JsonProperty("url_is_banned") boolean userIsBanned;
    @JsonProperty("user_is_contributer") boolean userIsContributed;
    @JsonProperty("user_is_moderator") boolean userIsModerator;
    @JsonProperty("user_is_subscriber") boolean userIsSubscriber;

    public void collectComments(RedditCommentAccumulator commentAccumulator, String linkId ){ }

}
