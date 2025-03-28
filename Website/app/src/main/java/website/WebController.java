package com.upt.upt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import com.upt.upt.service.UserService;
import com.upt.upt.entity.UserType;


@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid credentials");
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/validate-login")
    public String validateLogin(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) {
        UserType userType = userService.validateUser(username, password);
        if (userType != null) {
            Long userId = userService.getUserIdByUsername(username, userType);
            session.setAttribute("userId", userId);
            session.setAttribute("userType", userType);
            session.setAttribute("username", username);
            switch (userType) {
                case MASTER:
                    return "redirect:/master";
                case DIRECTOR:
                    return "redirect:/director";
                case COORDINATOR:
                    return "redirect:/coordinator";
                default:
                    return "redirect:/login?error=Invalid user type";
            }
        } else {
            return "redirect:/login?error=Invalid credentials";
        }
    }
}