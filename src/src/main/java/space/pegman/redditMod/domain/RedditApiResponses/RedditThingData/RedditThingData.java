package space.pegman.redditMod.domain.RedditApiResponses.RedditThingData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import space.pegman.redditMod.domain.RedditCommentAccumulator;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Slf4j
/*
    Abstract base class for the reddit "thing" type's data member.
 */
public abstract class RedditThingData {
    private static final ObjectMapper mapper = getObjectMapper();

    /*
        Converts the structure of maps/lists/primitives that jasckson returns into
        the correct domain class. Necessary because Jackson's polymorphic deserialization
        doesn't work with reddit's kind/data pattern, believe me I tried.
     */
    public static RedditThingData fromJacksonObject(Object thingData, String kind){
        //Types of data, see the table @ https://www.reddit.com/dev/api/#fullnames
        log.debug("Decoding reddit response of type {}: {} ", kind, thingData);
        if (thingData==null) return null;
        switch (kind){
            case "t1": return mapper.convertValue(thingData, RedditComment.class);
            case "t2": return mapper.convertValue(thingData, RedditAccount.class);
            case "t3": return mapper.convertValue(thingData, RedditLink.class);
            case "t5": return mapper.convertValue(thingData, RedditSubReddit.class);
            case "more": return mapper.convertValue(thingData, RedditMore.class);
            case "Listing": return mapper.convertValue(thingData, RedditListingData.class);
            default: return null;
        }
    }

    /*
        Returns a new mapper unless the mapper field is set.
     */
    private static synchronized ObjectMapper getObjectMapper(){
        if(mapper==null) {
            ObjectMapper newMapper = new ObjectMapper();
            newMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            newMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            return newMapper;
        }
        return mapper;
    }

    public abstract void collectComments(RedditCommentAccumulator commentAccumulator, String linkId);

}
