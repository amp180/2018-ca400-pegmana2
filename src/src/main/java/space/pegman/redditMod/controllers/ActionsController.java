package space.pegman.redditMod.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import space.pegman.redditMod.domain.Database.Action;
import space.pegman.redditMod.domain.Database.Rule;
import space.pegman.redditMod.mappers.ActionsMapper;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@RestController
public class ActionsController {

    @Autowired
    ActionsMapper mapper;


    @RequestMapping(value="/actions", produces="application/json")
    @ResponseBody
    Action[] getActions(){
        return mapper.getActions();
    }


    @PostMapping(value="/actions", produces="application/json", consumes="application/json")
    void createAction(@RequestBody ActionCreateRequest createRequest, HttpServletResponse response){
        mapper.insertAction(new Action(null, createRequest.getName(), createRequest.getType(), createRequest.getMessage()));
        response.setStatus(HttpStatus.CREATED.value());
    }


    @DeleteMapping(value="/actions", produces="application/json", consumes="application/json")
    void DeleteAction(@RequestBody ActionDeleteRequest deleteRequest){
        mapper.deleteAction(new Action(deleteRequest.getId(), null, null, null));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ActionCreateRequest {
        @JsonProperty("name") private String name;
        @JsonProperty("type") private Integer type;
        @JsonProperty("message") private String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class ActionDeleteRequest {
        @JsonProperty("id") private Long id;
    }

}
