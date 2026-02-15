package com.krstf.newsfeed;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontController {
    @RequestMapping(value = {"/", "/{x:[\\w\\-]+}", "/{x:[\\w\\-]+}/{y:[\\w\\-]+}"})
    public String forward() {
        return "forward:/index.html";
    }
}
