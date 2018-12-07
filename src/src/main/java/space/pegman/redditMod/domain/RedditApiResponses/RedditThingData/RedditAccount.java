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
    Domain class for reddit's account json data type
 */
public class RedditAccount extends RedditThingData {

    @JsonProperty("comment_karma") int commentKarma;
    @JsonProperty("has_mail") boolean hasMail;
    @JsonProperty("has_mod_mail") boolean hasModMail;
    @JsonProperty("has_verified_email") boolean hasVerifiedEmail;
    @JsonProperty("id") String id;
    @JsonProperty("inbox_count") int inboxCount;
    @JsonProperty("is_friend") boolean isFriend;
    @JsonProperty("is_gold") boolean isGold;
    @JsonProperty("is_mod")  boolean isMod;
    @JsonProperty("link_karma") int linkKarma;
    @JsonProperty("modhash") String modhash;
    @JsonProperty("name") String name;
    @JsonProperty("over_18") boolean over18;
    @JsonProperty("created") long created;
    @JsonProperty("created_utc") long createdUTC;

    public void collectComments(RedditCommentAccumulator commentAccumulator, String linkId ){ }
}
