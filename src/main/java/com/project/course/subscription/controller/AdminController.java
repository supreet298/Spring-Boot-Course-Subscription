package com.project.course.subscription.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/")
public class AdminController {

    @GetMapping("home")
    public String home(){
        return "welcome home";
    }

    @GetMapping("homes")
    public String homes(){
        return "Welcome to Course Subscription";
    }
}
