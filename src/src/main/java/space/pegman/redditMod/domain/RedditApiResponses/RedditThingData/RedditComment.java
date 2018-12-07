package space.pegman.redditMod.domain.RedditApiResponses.RedditThingData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThing;
import space.pegman.redditMod.domain.RedditCommentAccumulator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown = true)
/*
    Domain class for reddit's comment json data type
 */
public class RedditComment extends RedditThingData {

    @JsonProperty("id") String id;
    @JsonProperty("name") String name;
    @JsonProperty("approved_by") String	approvedBy;
    @JsonProperty("author") String	author;
    @JsonProperty("author_flair_css_class") String	authorFlairCssClass;
    @JsonProperty("author_flair_text") String	authorFlairText;
    @JsonProperty("banned_by") String bannedBy;
    @JsonProperty("body") String	body;
    @JsonProperty("body_html") String   bodyHtml;
    @JsonProperty("guilded") int	gilded;
    @JsonProperty("link_author") String	linkAuthor;
    @JsonProperty("link_id") String	linkId;
    @JsonProperty("link_title") String	linkTitle;
    @JsonProperty("link_url") String	linkUrl;
    @JsonProperty("num_reports") int	num_reports;
    @JsonProperty("parent_id") String	parentId;
    @JsonProperty("saved") boolean	saved;
    @JsonProperty("score") int	score;
    @JsonProperty("score_hidden") boolean	scoreHidden;
    @JsonProperty("subreddit") String	subreddit;
    @JsonProperty("subreddit_id") String	subredditId;
    @JsonProperty("distinguished") String	distinguished;
    @JsonProperty("ups") int ups;
    @JsonProperty("downs") int downs;
    @JsonProperty("likes") boolean likes;
    @JsonProperty("created") long created;
    @JsonProperty("created_utc") long createdUTC;
    @JsonProperty("replies") RedditThing replies;

    public void collectComments(RedditCommentAccumulator commentAccumulator, String linkId){
        commentAccumulator.addComment(this);

        if(this.linkId==null){
            this.linkId = linkId;
        }
        if(replies!=null && !commentAccumulator.getCollectsReplies()) {
            replies.collectComments(commentAccumulator, this.linkId);
        }
    }

}
