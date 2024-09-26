package com.tk.integration.controller;

//import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
//@Tag(name = "Test Controller")
public class TestController {
    @GetMapping("/")
    public String allAccess() {
        return "Application is running properly.";
    }
}
