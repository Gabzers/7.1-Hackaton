package com.hackaton.website.Controller;

import com.hackaton.website.Entity.User; // Import User entity
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

/**
 * Controller responsible for handling product-related endpoints.
 * 
 * @author Gabriel Proença
 * @author Diogo Sequeira
 * @author André Ferreira
 * @author Ruben Silva
 * @author João Rebelo
 * @author Rafael Barbosa
 * @author Paulo Brochado
 */
@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService; // Inject UserService dependency

    /**
     * Serves the Battle Pass page.
     * Redirects to login if the user is not logged in.
     *
     * @param session the HTTP session
     * @param model   the model to pass attributes to the view
     * @return the name of the view to render
     */
    @GetMapping("/product-battlepass")
    public String serveBattlePassPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login"; // Redirect to login if the user is not logged in
        }
        model.addAttribute("loggedUser", loggedUser); // Add the logged user to the model
        return "battlepass";
    }

    /**
     * Completes a mission for the logged user.
     * Returns the points earned for completing the mission.
     *
     * @param session     the HTTP session
     * @param missionName the name of the mission to complete
     * @return a map containing the points earned
     */
    @PostMapping("/completeMission")
    @ResponseBody
    public Map<String, Integer> completeMission(HttpSession session, @RequestParam String missionName) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            System.out.println("User not logged in. Returning 0 points.");
            return Map.of("pointsEarned", 0); // Return 0 points if the user is not logged in
        }

        System.out.println("Mission name received: " + missionName);
        System.out.println("Logged user: " + loggedUser.getName());

        int pointsEarned = 0;
        try {
            pointsEarned = productService.completeMission(loggedUser, missionName);
            session.setAttribute("loggedUser", loggedUser); // Update session with new points
            System.out.println("Points earned: " + pointsEarned);
        } catch (Exception e) {
            System.err.println("Error completing mission: " + e.getMessage());
            e.printStackTrace();
        }

        return Map.of("pointsEarned", pointsEarned);
    }

    /**
     * Claims a reward for the logged user based on the specified tier.
     * Returns a status indicating the result of the operation.
     *
     * @param session the HTTP session
     * @param tier    the tier of the reward to claim
     * @return a string indicating the result of the operation
     */
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