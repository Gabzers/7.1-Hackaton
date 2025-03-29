package com.hackaton.website.Controller;

import com.hackaton.website.Entity.User;
import com.hackaton.website.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password) {
        // Create a new User object with the received data
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        // Save the user in the database
        userRepository.save(user);

        // Redirect to the login page
        return "login"; // Ensure that the login.html page exists
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            Model model) {
        // Check if the user exists in the database
        User user = userRepository.findByEmailAndPassword(email, password);

        if (user != null) {
            // Successful login, redirect to the home or dashboard page
            return "home"; // Ensure that the dashboard.html page exists
        } else {
            // Login failed, add an error message and redirect to the login page
            model.addAttribute("error", "Incorrect email or password. Please try again.");
            return "login"; // Ensure that the login.html page exists
        }
    }
}