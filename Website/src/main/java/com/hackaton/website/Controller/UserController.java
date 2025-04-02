package com.hackaton.website.Controller;

import com.hackaton.website.Entity.User;
import com.hackaton.website.Repository.UserRepository;
import com.hackaton.website.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import com.hackaton.website.Entity.User.MovieGenre;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Arrays;
import com.hackaton.website.Entity.Shop;
import com.hackaton.website.Entity.Product;
import com.hackaton.website.Entity.Movies;
import com.hackaton.website.Repository.ShopRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.hibernate.Hibernate;
import java.time.Duration;
import java.util.Set;

/**
 * Controller responsible for handling user-related endpoints.
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
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ShopRepository shopRepository;

    /**
     * Registers a new user and initializes their data.
     * Redirects to the home page upon successful registration.
     *
     * @param name     the name of the user
     * @param email    the email of the user
     * @param password the password of the user
     * @param model    the model to pass attributes to the view
     * @param session  the HTTP session
     * @return the name of the view to render
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               Model model,
                               HttpSession session) { // Add HttpSession to the method parameters
        logger.info("Registering user with name: {}, email: {}", name, email);

        // Check if email already exists
        if (userRepository.findByEmail(email) != null) {
            logger.warn("Email already exists: {}", email);
            model.addAttribute("error", "This email is already in use. Please use another one.");
            return "registration"; // Redirect back to the registration page
        }

        // Create a new User object with the received data
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPoints(0); // Initialize points as 0
        user.setExp(0); // Initialize exp as 0
        logger.debug("User object created: {}", user);

        // Initialize genres with score 0
        List<MovieGenre> movieGenres = new ArrayList<>();
        List<String> allGenres = List.of("Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", 
                                         "Documentary", "Drama", "Family", "Fantasy", "Film-Noir", "Game-Show", 
                                         "History", "Horror", "Music", "Musical", "Mystery", "News", "Reality-TV", 
                                         "Romance", "Sci-Fi", "Short", "Sport", "Talk-Show", "Thriller", "War", "Western");

        for (String genre : allGenres) {
            movieGenres.add(new MovieGenre(genre, "0")); // Initialize all genres with score 0
        }
        user.setMovieGenres(movieGenres);
        logger.debug("Initialized movie genres for user: {}", movieGenres);

        // Initialize missions as unavailable
        user.setCompletedMissions(Set.of("RateAMovie", "Watch5Ads")); // Mark these missions as completed initially

        // Save the user to the database
        userRepository.save(user);
        logger.info("User saved to database: {}", user);

        // Add the user to the session
        session.setAttribute("loggedUser", user);

        // Redirect to the home page with the firstTime parameter
        return "redirect:/home?userId=" + user.getId() + "&firstTime=true";
    }

    /**
     * Saves the selected movie genres for a user.
     * Adds points to the user for completing the action.
     *
     * @param userId          the ID of the user
     * @param selectedGenres  the list of selected genres
     * @param model           the model to pass attributes to the view
     * @param session         the HTTP session
     * @return the name of the view to render
     */
    @PostMapping("/save-genres")
    public String saveGenres(@RequestParam("userId") Long userId,
                             @RequestParam(value = "genres", required = false) List<String> selectedGenres,
                             Model model,
                             HttpSession session) {
        logger.info("Saving genres for user with ID: {}", userId);

        // Fetch the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Ensure the list of selected genres is not null
        if (selectedGenres == null) {
            selectedGenres = new ArrayList<>();
        }

        // Update the user's genres
        List<MovieGenre> updatedGenres = new ArrayList<>();
        List<String> allGenres = List.of("Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", 
                                         "Documentary", "Drama", "Family", "Fantasy", "Film-Noir", "Game-Show", 
                                         "History", "Horror", "Music", "Musical", "Mystery", "News", "Reality-TV", 
                                         "Romance", "Sci-Fi", "Short", "Sport", "Talk-Show", "Thriller", "War", "Western");

        for (String genre : allGenres) {
            if (selectedGenres.contains(genre)) {
                updatedGenres.add(new MovieGenre(genre, "10")); // Checkbox selected, score = 10
            } else {
                updatedGenres.add(new MovieGenre(genre, "0")); // Checkbox not selected, score = 0
            }
        }

        // Update the user's genres
        user.setMovieGenres(updatedGenres);

        // Add 100 points to the user
        int currentPoints = user.getPoints() != null ? user.getPoints() : 0;
        user.setPoints(currentPoints + 100); // Updated from 50 to 100
        logger.info("Added 100 points to user. New total: {}", user.getPoints());

        // Save the changes to the database
        userRepository.save(user);
        logger.info("Updated user saved to database: {}", user);

        // Add the user to the model and session
        session.setAttribute("loggedUser", user);
        model.addAttribute("loggedUser", user);

        // Redirect to /home
        return "redirect:/home";
    }

    /**
     * Logs in a user by verifying their email and password.
     * Redirects to the home page upon successful login.
     *
     * @param email    the email of the user
     * @param password the password of the user
     * @param model    the model to pass attributes to the view
     * @param session  the HTTP session
     * @return the name of the view to render
     */
    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            Model model,
                            HttpSession session) {
        logger.info("Attempting to log in user with email: {}", email);

        // Verify if the user exists in the database
        User user = userRepository.findByEmailAndPassword(email, password);
        if (user != null) {
            logger.info("User logged in successfully: {}", user);

            // Add the user to the session
            session.setAttribute("loggedUser", user);

            // Redirect to /home
            return "redirect:/home";
        } else {
            logger.error("Login failed for email: {}", email);

            // Add error message to the model
            model.addAttribute("error", "Email or password wrong. Try again.");

            // Redirect to the login page
            return "login"; // Ensure the login.html page exists
        }
    }

    /**
     * Serves the home page for the logged-in user.
     * Fetches movie recommendations based on user preferences.
     *
     * @param session the HTTP session
     * @param model   the model to pass attributes to the view
     * @return the name of the view to render
     */
    @GetMapping("/home")
    public String serveHomePage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login"; // Redirect to login if no user is logged in
        }

        // Fetch the user with movieGenres eagerly loaded
        User userWithGenres = userRepository.findWithMovieGenresById(loggedUser.getId());
        if (userWithGenres == null) {
            return "redirect:/login";
        }

        // Check if the user has movie genres defined
        if (userWithGenres.getMovieGenres() == null || userWithGenres.getMovieGenres().isEmpty()) {
            model.addAttribute("error", "You have not defined your favorite movie genres. Please update your preferences.");
            model.addAttribute("recommendations", Collections.emptyList());
            return "home";
        }

        try {
            // Fetch top 3 genres and their top 10 movies
            Map<String, List<Map<String, String>>> topGenresMovies = userService.recommendTop3GenresMovies(userWithGenres);
            model.addAttribute("topGenresMovies", topGenresMovies);

            // Fetch recommendations for "Trending Now"
            List<Map<String, String>> recommendations = userService.recommendMovies(userWithGenres);
            model.addAttribute("recommendations", recommendations);

            // Fetch least preferred movie recommendations
            List<Map<String, String>> leastPreferredRecommendations = userService.recommendLeastPreferredMovies(userWithGenres);
            model.addAttribute("leastPreferredRecommendations", leastPreferredRecommendations);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("recommendations", Collections.emptyList());
        }

        return "home";
    }

    /**
     * Adds points to the logged-in user.
     *
     * @param requestBody the request body containing the points to add
     * @param session     the HTTP session
     * @return a response message indicating success or failure
     */
    @PostMapping("/add-points")
    @ResponseBody
    public String addPoints(@RequestBody Map<String, Integer> requestBody, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "User not logged in";
        }

        int pointsToAdd = requestBody.getOrDefault("points", 0);
        int currentPoints = loggedUser.getPoints() != null ? loggedUser.getPoints() : 0;
        loggedUser.setPoints(currentPoints + pointsToAdd);

        try {
            // Save the updated user to the database immediately
            userRepository.save(loggedUser);
            session.setAttribute("loggedUser", loggedUser); // Update the session with the updated user
            logger.info("Points added successfully. New total: {}", loggedUser.getPoints());
            return "Points added successfully";
        } catch (Exception e) {
            logger.error("Error saving points to the database: {}", e.getMessage());
            return "Error saving points";
        }
    }

    /**
     * Adds experience points (EXP) to the logged-in user.
     *
     * @param requestBody the request body containing the EXP to add
     * @param session     the HTTP session
     * @return a response message indicating success or failure
     */
    @PostMapping("/add-exp")
    @ResponseBody
    public String addExp(@RequestBody Map<String, Integer> requestBody, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "User not logged in";
        }

        int expToAdd = requestBody.getOrDefault("exp", 0);
        int currentExp = loggedUser.getExp() != null ? loggedUser.getExp() : 0;
        loggedUser.setExp(currentExp + expToAdd);

        // Update the tier based on EXP
        int newTier = (loggedUser.getExp() / 1000) + 1;
        logger.info("User EXP updated: {}, New Tier: {}", loggedUser.getExp(), newTier);

        userRepository.save(loggedUser);
        session.setAttribute("loggedUser", loggedUser);

        return "Exp added successfully";
    }

    /**
     * Serves the Battle Pass page for the logged-in user.
     *
     * @param session the HTTP session
     * @param model   the model to pass attributes to the view
     * @return the name of the view to render
     */
    @GetMapping("/battlepass")
    public String serveBattlePassPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", loggedUser);
        return "battlepass";
    }

    /**
     * Serves the Shop page for the logged-in user.
     * Refreshes the shop if the last refresh was more than 7 days ago.
     *
     * @param session the HTTP session
     * @param model   the model to pass attributes to the view
     * @return the name of the view to render
     */
    @GetMapping("/shop")
    public String serveShopPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        Shop userShop = shopRepository.findByUserId(loggedUser.getId());
        LocalDateTime now = LocalDateTime.now();

        if (userShop == null || userShop.getCreationDate().plusDays(7).isBefore(now)) {
            List<Map<String, String>> randomProducts = userService.recommendRandomProducts();
            List<Map<String, String>> recommendedMovies = userService.recommendMovies(loggedUser);

            List<Product> products = randomProducts.stream().map(product -> {
                String cost = product.get("cost").replace(".", "");
                String productName = product.get("product_name");
                String[] words = productName.split("\\s+");
                if (productName.length() > 60) {
                    productName = productName.substring(0, 60) + "...";
                } else if (words.length > 8) { // Fix: Use .length instead of .length()
                    productName = String.join(" ", Arrays.copyOf(words, 8)) + "...";
                }
                return new Product(productName, product.get("category"), cost);
            }).toList();

            List<Movies> movies = recommendedMovies.stream().map(movie -> 
                new Movies(
                    movie.get("title"),
                    movie.get("genres"),
                    movie.get("averageRating"),
                    movie.get("releaseYear")
                )
            ).toList();

            userShop = new Shop(products);
            userShop.setMovies(movies);
            userShop.setUser(loggedUser);
            shopRepository.save(userShop);
        }

        LocalDateTime nextRefresh = userShop.getCreationDate().plusDays(7);
        String formattedCountdownDate = nextRefresh.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME); // Format as ISO 8601

        model.addAttribute("shop", userShop);
        model.addAttribute("countdownDate", formattedCountdownDate);

        // Log data for debugging
        logger.info("Shop: {}", userShop);
        logger.info("Products: {}", userShop.getProducts());
        logger.info("Movies: {}", userShop.getMovies());
        logger.info("Countdown Date: {}", formattedCountdownDate);

        return "shop";
    }

    /**
     * Redeems a product for the logged-in user.
     *
     * @param productId the ID of the product to redeem
     * @param session   the HTTP session
     * @return a response map indicating success or failure
     */
    @PostMapping("/redeem-product")
    @ResponseBody
    public Map<String, Object> redeemProduct(@RequestParam("productId") Long productId, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return Map.of("success", false, "message", "User not logged in");
        }

        Shop userShop = shopRepository.findByUserId(loggedUser.getId());
        if (userShop == null) {
            return Map.of("success", false, "message", "Shop not found");
        }

        for (Product product : userShop.getProducts()) {
            if (product.getId().equals(productId)) {
                if (Boolean.TRUE.equals(product.getIsRedeemed())) {
                    return Map.of("success", false, "message", "Product already redeemed");
                }
                if (loggedUser.getPoints() < Integer.parseInt(product.getCost())) {
                    return Map.of("success", false, "message", "Insufficient points");
                }
                product.setIsRedeemed(true);
                loggedUser.setPoints(loggedUser.getPoints() - Integer.parseInt(product.getCost()));
                shopRepository.save(userShop);
                userRepository.save(loggedUser);
                session.setAttribute("loggedUser", loggedUser);
                return Map.of("success", true, "message", "Product redeemed successfully", "newPoints", loggedUser.getPoints());
            }
        }

        return Map.of("success", false, "message", "Product not found");
    }

    /**
     * Redeems a movie for the logged-in user.
     *
     * @param movieId the ID of the movie to redeem
     * @param session the HTTP session
     * @return a response map indicating success or failure
     */
    @PostMapping("/redeem-movie")
    @ResponseBody
    public Map<String, Object> redeemMovie(@RequestParam("movieId") Long movieId, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return Map.of("success", false, "message", "User not logged in");
        }

        Shop userShop = shopRepository.findByUserId(loggedUser.getId());
        if (userShop == null) {
            return Map.of("success", false, "message", "Shop not found");
        }

        for (Movies movie : userShop.getMovies()) {
            if (movie.getId().equals(movieId)) {
                if (Boolean.TRUE.equals(movie.getIsRedeemed())) {
                    return Map.of("success", false, "message", "Movie already redeemed");
                }
                if (loggedUser.getPoints() < 999) { // Assuming movie cost is 999 points
                    return Map.of("success", false, "message", "Insufficient points");
                }
                movie.setIsRedeemed(true);
                loggedUser.setPoints(loggedUser.getPoints() - 999);
                shopRepository.save(userShop);
                userRepository.save(loggedUser);
                session.setAttribute("loggedUser", loggedUser);
                return Map.of("success", true, "message", "Movie redeemed successfully", "newPoints", loggedUser.getPoints());
            }
        }

        return Map.of("success", false, "message", "Movie not found");
    }

    /**
     * Checks the status of missions for the logged-in user.
     *
     * @param session the HTTP session
     * @return a map indicating the availability of missions
     */
    @GetMapping("/check-mission-status")
    @ResponseBody
    public Map<String, Boolean> checkMissionStatus(HttpSession session) {
        logger.info("Checking mission status...");
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("No logged-in user found.");
            return Map.of("rateMovieMissionAvailable", false, "watchAdsMissionAvailable", false);
        }

        // Fetch the user from the database with completedMissions eagerly loaded
        User userFromDb = userRepository.findById(loggedUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Hibernate.initialize(userFromDb.getCompletedMissions()); // Initialize the collection

        boolean rateMovieMissionAvailable = userFromDb.getCompletedMissions() != null &&
                                            !userFromDb.getCompletedMissions().contains("RateAMovie");

        boolean watchAdsMissionAvailable = userFromDb.getCompletedMissions() != null &&
                                           !userFromDb.getCompletedMissions().contains("Watch5Ads");

        logger.info("Mission 'Rate a movie' available: {}", rateMovieMissionAvailable);
        logger.info("Mission 'Watch 5 Ads' available: {}", watchAdsMissionAvailable);

        return Map.of(
            "rateMovieMissionAvailable", rateMovieMissionAvailable,
            "watchAdsMissionAvailable", watchAdsMissionAvailable
        );
    }

    /**
     * Marks a mission as available for the logged-in user.
     *
     * @param requestBody the request body containing the mission name
     * @param session     the HTTP session
     * @return a response message indicating success or failure
     */
    @PostMapping("/mark-mission-available")
    @ResponseBody
    public String markMissionAsAvailable(@RequestBody Map<String, String> requestBody, HttpSession session) {
        logger.info("Marking mission as available...");
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("No logged-in user found.");
            return "User not logged in";
        }

        // Fetch the user from the database with completedMissions initialized
        User userFromDb = userRepository.findById(loggedUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Hibernate.initialize(userFromDb.getCompletedMissions()); // Initialize the collection

        String missionName = requestBody.get("missionName");
        logger.info("Mission name received: {}", missionName);
        if (missionName != null) {
            userFromDb.getCompletedMissions().remove(missionName); // Ensure it's available
            userRepository.save(userFromDb);
            session.setAttribute("loggedUser", userFromDb);
            logger.info("Mission '{}' marked as available.", missionName);
            return "Mission marked as available";
        }

        logger.error("Invalid mission name: {}", missionName);
        return "Invalid mission name";
    }

    /**
     * Completes a mission for the logged-in user.
     *
     * @param requestBody the request body containing the mission name and points
     * @param session     the HTTP session
     * @return a response message indicating success or failure
     */
    @PostMapping("/complete-mission")
    @ResponseBody
    public String completeMission(@RequestBody Map<String, Object> requestBody, HttpSession session) {
        logger.info("Completing mission...");
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("No logged-in user found.");
            return "User not logged in";
        }

        // Fetch the user from the database with completedMissions initialized
        User userFromDb = userRepository.findById(loggedUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Hibernate.initialize(userFromDb.getCompletedMissions()); // Initialize the collection

        String missionName = (String) requestBody.get("missionName");
        int points = (int) requestBody.get("points");
        logger.info("Mission name: {}, Points: {}", missionName, points);

        if (missionName != null && (missionName.equals("RateAMovie") || missionName.equals("Watch5Ads"))) {
            // Log current exp before update
            logger.info("Current EXP before update: {}", userFromDb.getExp());

            // Mark mission as completed
            userFromDb.getCompletedMissions().add(missionName);

            // Update exp
            int currentExp = userFromDb.getExp() != null ? userFromDb.getExp() : 0;
            userFromDb.setExp(currentExp + points);

            // Log updated exp
            logger.info("Updated EXP after mission completion: {}", userFromDb.getExp());

            // Save user
            userRepository.save(userFromDb);
            session.setAttribute("loggedUser", userFromDb);

            logger.info("Mission '{}' completed successfully. Points added: {}", missionName, points);
            return "Mission completed successfully";
        }

        logger.error("Invalid mission name: {}", missionName);
        return "Invalid mission name";
    }

    /**
     * Checks the daily login status for the logged-in user.
     *
     * @param session the HTTP session
     * @return a map indicating the availability of the daily login mission
     */
    @GetMapping("/check-daily-login-status")
    @ResponseBody
    public Map<String, Object> checkDailyLoginStatus(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return Map.of("missionAvailable", false, "timeRemaining", 0);
        }

        LocalDateTime lastLogin = loggedUser.getLastDailyLogin();
        LocalDateTime now = LocalDateTime.now();

        if (lastLogin == null || Duration.between(lastLogin, now).toHours() >= 24) {
            return Map.of("missionAvailable", true, "timeRemaining", 0);
        }

        long secondsRemaining = Duration.between(now, lastLogin.plusHours(24)).getSeconds();
        return Map.of("missionAvailable", false, "timeRemaining", secondsRemaining);
    }

    /**
     * Completes the daily login mission for the logged-in user.
     *
     * @param session the HTTP session
     * @return a response message indicating success or failure
     */
    @PostMapping("/complete-daily-login-mission")
    @ResponseBody
    public String completeDailyLoginMission(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "User not logged in";
        }

        LocalDateTime lastLogin = loggedUser.getLastDailyLogin();
        LocalDateTime now = LocalDateTime.now();

        if (lastLogin != null && Duration.between(lastLogin, now).toHours() < 24) {
            return "Mission not available yet";
        }

        loggedUser.setLastDailyLogin(now);
        int currentExp = loggedUser.getExp() != null ? loggedUser.getExp() : 0;
        loggedUser.setExp(currentExp + 50); // Add 50 XP for completing the mission
        userRepository.save(loggedUser);
        session.setAttribute("loggedUser", loggedUser);

        return "Mission completed successfully";
    }

    /**
     * Logs out the user by invalidating the session.
     *
     * @param session the HTTP session
     * @return the name of the view to render
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalidate the session
        return "redirect:/login"; // Redirect to the login page
    }

    /**
     * Serves the Points Info page for the logged-in user.
     *
     * @param session the HTTP session
     * @param model   the model to pass attributes to the view
     * @return the name of the view to render
     */
    @GetMapping("/points-info")
    public String servePointsInfoPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login"; // Redirect to login if no user is logged in
        }
        model.addAttribute("loggedUser", loggedUser);
        return "points-info"; // Ensure the points-info.html template exists
    }

    /**
     * Updates the genre scores for the logged-in user.
     *
     * @param requestBody the request body containing the movie genres
     * @param session     the HTTP session
     * @return a response message indicating success or failure
     */
    @PostMapping("/update-genre-scores")
    @ResponseBody
    public String updateGenreScores(@RequestBody Map<String, List<String>> requestBody, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "User not logged in";
        }

        // Fetch the user with the movieGenres collection initialized
        User userWithGenres = userRepository.findById(loggedUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Hibernate.initialize(userWithGenres.getMovieGenres()); // Initialize the collection

        List<String> movieGenres = requestBody.get("movieGenres");
        if (movieGenres == null || movieGenres.isEmpty()) {
            return "Invalid movie genres";
        }

        // Update the user's genre scores
        List<User.MovieGenre> updatedGenres = userWithGenres.getMovieGenres().stream()
            .map(genre -> {
                if (movieGenres.contains(genre.getGenre())) {
                    int updatedScore = Integer.parseInt(genre.getScore()) + 5; // Increment score
                    genre.setScore(String.valueOf(updatedScore));
                }
                return genre;
            })
            .toList();

        userWithGenres.setMovieGenres(updatedGenres);
        userRepository.save(userWithGenres);
        session.setAttribute("loggedUser", userWithGenres);

        return "Genre scores updated successfully";
    }

    /**
     * Retrieves the genres of a movie based on its title.
     *
     * @param title the title of the movie
     * @return a map containing the genres and other information
     */
    @GetMapping("/get-movie-genres")
    @ResponseBody
    public Map<String, Object> getMovieGenres(@RequestParam("title") String title) {
        List<String> genres = userService.getGenresByMovieTitle(title);

        if (genres.isEmpty()) {
            return Map.of("found", false);
        }

        return Map.of(
            "found", true,
            "title", title,
            "genres", genres
        );
    }
}