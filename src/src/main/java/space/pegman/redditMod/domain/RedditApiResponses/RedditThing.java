package space.pegman.redditMod.domain.RedditApiResponses;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditThingData;
import space.pegman.redditMod.domain.RedditCommentAccumulator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
/*
    Container class for the reddit api's "thing" type.

    https://github.com/reddit-archive/reddit/wiki/JSON
    https://www.reddit.com/dev/api/
    https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations#type-handling
 */
public class RedditThing {
    @JsonProperty("id") String id;
    @JsonProperty("kind") String kind;
    @JsonProperty("data") Object data;
    @JsonProperty("before") String before;
    @JsonProperty("after") String after;
    @JsonProperty("modhash") String modhash;

    String linkId;

    public RedditThingData getData() {
        return RedditThingData.fromJacksonObject(data, kind);
    }

    public void collectComments(RedditCommentAccumulator commentAccumulator, String linkId){
        if(this.getData()!=null) {
            this.getData().collectComments(commentAccumulator, linkId);
        }
    }
}
