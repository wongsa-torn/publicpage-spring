package com.sf.publicpage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "main/main";
    }

    @GetMapping("/login")
    public String customLogin() {
        return "login/login";
    }
    
    @GetMapping("/arrival_flight")
    public String AirLogin() {
        return "arrival_flight/arrival_flight";
    }

}