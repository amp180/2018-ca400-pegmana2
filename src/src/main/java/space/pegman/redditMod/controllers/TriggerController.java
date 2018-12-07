package space.pegman.redditMod.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import space.pegman.redditMod.domain.Database.Trigger;
import space.pegman.redditMod.mappers.TriggersMapper;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@RestController
public class TriggerController {

    @Autowired
    TriggersMapper mapper;


    @RequestMapping(value="/triggers", produces="application/json")
    @ResponseBody
    Trigger[] getTriggers(){
        return mapper.getTriggers();
    }


    @PostMapping(value="/triggers", produces="application/json", consumes="application/json")
    void createTrigger(@RequestBody TriggerCreateRequest createRequest, HttpServletResponse response) {
        mapper.insertTrigger(
                new Trigger(
                        null,
                        createRequest.getName(),
                        createRequest.getType(),
                        createRequest.getRegex(),
                        createRequest.getSampleSet(),
                        createRequest.getBadSampleSet()
                )
        );

        response.setStatus(HttpStatus.CREATED.value());
    }


    @DeleteMapping(value="/triggers", produces="application/json", consumes="application/json")
    void deleteTrigger(@RequestBody TriggerDeleteRequest deleteRequest){
        mapper.deleteTrigger(deleteRequest.getId());
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class TriggerCreateRequest {
       @JsonProperty("name") String name;
       @JsonProperty("type") int type;
       @JsonProperty("regex") String regex;
       @JsonProperty("goodSampleSet") Long sampleSet;
       @JsonProperty("badSampleSet") Long badSampleSet;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class TriggerDeleteRequest {
        @JsonProperty("id") long id;
    }

}
