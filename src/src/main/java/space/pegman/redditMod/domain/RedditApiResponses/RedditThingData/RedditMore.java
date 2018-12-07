package space.pegman.redditMod.domain.RedditApiResponses.RedditThingData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThing;
import space.pegman.redditMod.domain.RedditCommentAccumulator;

import java.util.ArrayList;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
/*
    Domain object for the reddit api's more type.
 */
public class RedditMore extends RedditThingData {
    String id;
    int count;
    String name;
    @JsonProperty("link_id") String	linkId;
    ArrayList<String> children;

    public void collectComments(RedditCommentAccumulator commentAccumulator, String linkId){
        if(linkId != null) {
            this.linkId = linkId;
        }
        if(this.linkId!=null){
            commentAccumulator.addMore(this);
        }
    }
}
