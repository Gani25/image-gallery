package com.sprk.imagegallery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("imagegallery")
public class LoginController {

    @GetMapping("/user/showLoginForm")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/accessdenied")
    public String showErrorPage() {
        return "access-denied";
    }

}
