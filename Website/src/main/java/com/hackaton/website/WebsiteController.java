package com.hackaton.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.hackaton.website.Entity.User;

import jakarta.servlet.http.HttpSession;

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
    public String serveMoviePage(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login"; // Redirect to login if no user is logged in
        }
        return "movie"; // Serve movie.html
    }

}
