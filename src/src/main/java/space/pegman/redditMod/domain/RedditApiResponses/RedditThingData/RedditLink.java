package space.pegman.redditMod.domain.RedditApiResponses.RedditThingData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import space.pegman.redditMod.domain.RedditCommentAccumulator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown = true)
/*
    Domain class for reddit's link json data type
 */
public class RedditLink extends RedditThingData {

    @JsonProperty("author") String	author;
    @JsonProperty("author_flair_css_class") String	authorFlairCssClass;
    @JsonProperty("author_flair_text") String	authorFlairText;
    @JsonProperty("clicked") boolean	clicked;
    @JsonProperty("domain") String	domain;
    @JsonProperty("hidden") boolean hidden;
    @JsonProperty("is_self") String isSelf;
    @JsonProperty("link_flair_css_class") String linkFlairCssClass;
    @JsonProperty("link_flair_text")  String	linkFlairText;
    @JsonProperty("locked") boolean	locked;
    @JsonProperty("media") Object	media;
    @JsonProperty("media_embed") Object mediaEmbed;
    @JsonProperty("num_comments") int numComments;
    @JsonProperty("over_18") boolean over18;
    @JsonProperty("permalink") String permalink;
    @JsonProperty("saved") boolean saved;
    @JsonProperty("score") int score;
    @JsonProperty("selftext") String selftext;
    @JsonProperty("selftext_html") String selftextHtml;
    @JsonProperty("subreddit") String subreddit;
    @JsonProperty("subreddit_id") String subredditId;
    @JsonProperty("thumbnail") String thumbnail;
    @JsonProperty("title") String title;
    @JsonProperty("distinguished") String distinguished;
    @JsonProperty("stickied") boolean stickied;
    @JsonProperty("ups") int ups;
    @JsonProperty("downs") int downs;
    @JsonProperty("likes") boolean likes;
    @JsonProperty("created") long created;
    @JsonProperty("created_utc") long createdUTC;

    public void collectComments(RedditCommentAccumulator commentAccumulator, String linkId){
        commentAccumulator.addThread(this.getPermalink());
    }
}
