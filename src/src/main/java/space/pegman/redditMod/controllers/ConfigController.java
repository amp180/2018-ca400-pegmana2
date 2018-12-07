package space.pegman.redditMod.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import space.pegman.redditMod.domain.Database.Action;
import space.pegman.redditMod.mappers.SettingsMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
public class ConfigController {

    @Autowired
    SettingsMapper settingsMapper;

    @PostMapping(value="/config")
    void setConfig(
            @RequestParam String clientId, @RequestParam String clientSecret,
            @RequestParam String username, @RequestParam String password,
            HttpServletResponse response
    ) throws IOException {
        settingsMapper.setSetting("clientId", clientId);
        settingsMapper.setSetting("clientSecret", clientSecret);
        settingsMapper.setSetting("username", username);
        settingsMapper.setSetting("password", password);
        response.sendRedirect("/");
    }

}
