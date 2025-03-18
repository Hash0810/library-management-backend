package com.lib.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

//	@GetMapping({"/", "/login", "/signup", "/admin", "/user"})
	@GetMapping("/{path:[^\\.]*}")
    public String index() {
        // Serve index.html at these routes
        return "forward:/index.html";
    }
}
