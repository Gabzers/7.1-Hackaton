package com.hackaton.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebsiteController {

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login"; // Redirect to /login without .html
    }

    @GetMapping("/login")
    public String serveLoginPage() {
        return "login"; // Return the view name without the "templates/" prefix
    }


    @GetMapping("/register")
    public String serveRegistrationPage() {
        return "registration"; // Serve registration.html
    }

    @GetMapping("/movie")
    public String serveMoviePage() {
        return "movie"; // Serve movie.html
    }

}
