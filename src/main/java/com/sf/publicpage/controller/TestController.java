package com.sf.publicpage.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${app.test.message:ไม่พบค่าที่ config.properties}")
    private String testMessage;

    @GetMapping("/test-config")
    public String testConfig() {
        return "Message จาก config.properties: " + testMessage;
    }
}