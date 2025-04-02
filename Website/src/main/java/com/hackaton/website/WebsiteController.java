package com.hackaton.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.hackaton.website.Entity.User;

import jakarta.servlet.http.HttpSession;

/**
 * Controller responsible for handling general website navigation.
 * Provides endpoints for serving login, registration, and other pages.
 * 
 * @author Gabriel Proença
 */
@Controller
public class WebsiteController {

    /**
     * Redirects the root URL to the login page.
     *
     * @return a redirect to the login page
     */
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login"; // Redirect to /login without .html
    }

    /**
     * Serves the login page.
     *
     * @return the name of the login view
     */
    @GetMapping("/login")
    public String serveLoginPage() {
        return "login"; // Return the view name without the "templates/" prefix
    }

    /**
     * Serves the registration page.
     *
     * @return the name of the registration view
     */
    @GetMapping("/register")
    public String serveRegistrationPage() {
        return "registration"; // Serve registration.html
    }

    /**
     * Serves the movie page for the logged-in user.
     * Redirects to the login page if no user is logged in.
     *
     * @param session the HTTP session
     * @return the name of the movie view or a redirect to the login page
     */
    @GetMapping("/movie")
    public String serveMoviePage(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login"; // Redirect to login if no user is logged in
        }
        return "movie"; // Serve movie.html
    }
}
