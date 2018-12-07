package space.pegman.redditMod.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class HomepageRedirectController {

    @RequestMapping(value = "/", method= RequestMethod.GET)
    public void redirectToIndex(HttpServletResponse response) throws IOException {
        response.sendRedirect("/index.html");
    }

}
