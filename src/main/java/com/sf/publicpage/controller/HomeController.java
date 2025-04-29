package com.sf.publicpage.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    // @GetMapping("/")
    // public String home() {
    // return "main/main";
    // }

    @GetMapping("/")
    public String home(Model model) {
        // ดึงชื่อผู้ใช้ที่ล็อกอินจาก SecurityContext
        String username = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        }

        // ส่งข้อมูลชื่อผู้ใช้ไปยัง HTML
        model.addAttribute("username", username);
        return "main/main";
    }

    @GetMapping("/login")
    public String customLogin() {
        return "login/login";
    }

    // @GetMapping("/test")
    // public String customTest() {
    // return "test";
    // }
    // @GetMapping("/arrival_flight")
    // public String AirLogin() {
    // return "arrival_flight/arrival_flight";
    // }

}