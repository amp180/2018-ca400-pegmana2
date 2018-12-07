package space.pegman.redditMod.domain.RedditApiResponses.RedditThingData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThing;
import space.pegman.redditMod.domain.RedditApiResponses.RedditThingData.RedditThingData;
import space.pegman.redditMod.domain.RedditCommentAccumulator;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown = true)
/*
    Domain class for the reddit listing json data type
 */
public class RedditListingData extends RedditThingData {
    @JsonProperty("before") String	before;
    @JsonProperty("after") String	after;
    @JsonProperty("modhash") String	modhash;
    @JsonProperty("children") ArrayList<RedditThing> children;
    @JsonProperty("name") String name;

    public void collectComments(RedditCommentAccumulator commentAccumulator, String linkId){

        if(name==null && linkId != null){
            name=linkId;
        }

        if(this.after!=null) {
            commentAccumulator.addAfter(this.after);
        }

        if (this.before!=null){
            commentAccumulator.addAfter(this.before);
        }

        for(RedditThing thing:children){
            thing.collectComments(commentAccumulator, name);
        }
    }

}
