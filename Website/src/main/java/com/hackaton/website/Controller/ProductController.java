package com.hackaton.website.Controller;

import com.hackaton.website.Entity.User; // Added import for User
import com.hackaton.website.Service.ProductService;
import com.hackaton.website.Service.UserService; // Import UserService
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService; // Add UserService as a dependency

    @GetMapping("/product-battlepass")
    public String serveBattlePassPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login"; // Redireciona para login se o usuário não estiver logado
        }
        model.addAttribute("loggedUser", loggedUser); // Adiciona o usuário logado ao modelo
        return "battlepass";
    }

    @PostMapping("/completeMission")
    @ResponseBody
    public Map<String, Integer> completeMission(HttpSession session, @RequestParam String missionName) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            System.out.println("User not logged in. Returning 0 points.");
            return Map.of("pointsEarned", 0); // Retorna 0 pontos se o usuário não estiver logado
        }

        System.out.println("Mission name received: " + missionName);
        System.out.println("Logged user: " + loggedUser.getName());

        int pointsEarned = 0;
        try {
            pointsEarned = productService.completeMission(loggedUser, missionName);
            session.setAttribute("loggedUser", loggedUser); // Atualiza a sessão com os novos pontos
            System.out.println("Points earned: " + pointsEarned);
        } catch (Exception e) {
            System.err.println("Error completing mission: " + e.getMessage());
            e.printStackTrace();
        }

        return Map.of("pointsEarned", pointsEarned);
    }

    @PostMapping("/claimReward")
    @ResponseBody
    public String claimReward(HttpSession session, @RequestParam int tier) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "not_logged_in"; // Return a clean identifier for not logged in
        }

        try {
            String result = userService.claimReward(loggedUser, tier);
            session.setAttribute("loggedUser", loggedUser); // Update session with claimed reward
            return result.isEmpty() ? "success" : "already_claimed"; // Return clean identifiers
        } catch (Exception e) {
            return "error"; // Return a clean identifier for errors
        }
    }

}