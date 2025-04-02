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

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ShopRepository shopRepository;

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

        // Criar um novo objeto User com os dados recebidos
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPoints(0); // Inicializar pontos como 0
        user.setExp(0); // Inicializar exp como 0
        logger.debug("User object created: {}", user);

        // Inicializar os gêneros com score 0
        List<MovieGenre> movieGenres = new ArrayList<>();
        List<String> allGenres = List.of("Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", 
                                         "Documentary", "Drama", "Family", "Fantasy", "Film-Noir", "Game-Show", 
                                         "History", "Horror", "Music", "Musical", "Mystery", "News", "Reality-TV", 
                                         "Romance", "Sci-Fi", "Short", "Sport", "Talk-Show", "Thriller", "War", "Western");

        for (String genre : allGenres) {
            movieGenres.add(new MovieGenre(genre, "0")); // Inicializar todos os gêneros com score 0
        }
        user.setMovieGenres(movieGenres);
        logger.debug("Initialized movie genres for user: {}", movieGenres);

        // Initialize missions as unavailable
        user.setCompletedMissions(Set.of("RateAMovie", "Watch5Ads")); // Mark these missions as completed initially

        // Salvar o usuário no banco de dados
        userRepository.save(user);
        logger.info("User saved to database: {}", user);

        // Add the user to the session
        session.setAttribute("loggedUser", user);

        // Redirect to the home page with the firstTime parameter
        return "redirect:/home?userId=" + user.getId() + "&firstTime=true";
    }

    @PostMapping("/save-genres")
    public String saveGenres(@RequestParam("userId") Long userId,
                             @RequestParam(value = "genres", required = false) List<String> selectedGenres,
                             Model model,
                             HttpSession session) {
        logger.info("Saving genres for user with ID: {}", userId);

        // Buscar o usuário pelo ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Garantir que a lista de gêneros selecionados não seja nula
        if (selectedGenres == null) {
            selectedGenres = new ArrayList<>();
        }

        // Atualizar os gêneros do usuário
        List<MovieGenre> updatedGenres = new ArrayList<>();
        List<String> allGenres = List.of("Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", 
                                         "Documentary", "Drama", "Family", "Fantasy", "Film-Noir", "Game-Show", 
                                         "History", "Horror", "Music", "Musical", "Mystery", "News", "Reality-TV", 
                                         "Romance", "Sci-Fi", "Short", "Sport", "Talk-Show", "Thriller", "War", "Western");

        for (String genre : allGenres) {
            if (selectedGenres.contains(genre)) {
                updatedGenres.add(new MovieGenre(genre, "10")); // Checkbox selecionado, score = 10
            } else {
                updatedGenres.add(new MovieGenre(genre, "0")); // Checkbox não selecionado, score = 0
            }
        }

        // Atualizar os gêneros do usuário
        user.setMovieGenres(updatedGenres);

        // Adicionar 50 pontos ao usuário
        int currentPoints = user.getPoints() != null ? user.getPoints() : 0;
        user.setPoints(currentPoints + 50);
        logger.info("Added 50 points to user. New total: {}", user.getPoints());

        // Salvar as alterações no banco de dados
        userRepository.save(user);
        logger.info("Updated user saved to database: {}", user);

        // Adicionar o usuário ao modelo e à sessão
        session.setAttribute("loggedUser", user);
        model.addAttribute("loggedUser", user);

        // Redirecionar para /home
        return "redirect:/home";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            Model model,
                            HttpSession session) {
        logger.info("Attempting to log in user with email: {}", email);

        // Verificar se o usuário existe no banco de dados
        User user = userRepository.findByEmailAndPassword(email, password);
        if (user != null) {
            logger.info("User logged in successfully: {}", user);

            // Adicionar o usuário à sessão
            session.setAttribute("loggedUser", user);

            // Redirecionar para /home
            return "redirect:/home";
        } else {
            logger.error("Login failed for email: {}", email);

            // Adicionar mensagem de erro ao modelo
            model.addAttribute("error", "Email or password wrong. Try again.");

            // Redirecionar para a página de login
            return "login"; // Certifique-se de que a página login.html existe
        }
    }

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

        userRepository.save(loggedUser);
        session.setAttribute("loggedUser", loggedUser);

        return "Points added successfully";
    }

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

        // Atualizar o tier com base no exp
        int newTier = (loggedUser.getExp() / 1000) + 1;
        logger.info("User EXP updated: {}, New Tier: {}", loggedUser.getExp(), newTier);

        userRepository.save(loggedUser);
        session.setAttribute("loggedUser", loggedUser);

        return "Exp added successfully";
    }

    @GetMapping("/battlepass")
    public String serveBattlePassPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", loggedUser);
        return "battlepass";
    }

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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalidate the session
        return "redirect:/login"; // Redirect to the login page
    }

    @GetMapping("/points-info")
    public String servePointsInfoPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login"; // Redirect to login if no user is logged in
        }
        model.addAttribute("loggedUser", loggedUser);
        return "points-info"; // Ensure the points-info.html template exists
    }

    @PostMapping("/update-genre-scores")
    @ResponseBody
    public String updateGenreScores(@RequestBody Map<String, List<String>> requestBody, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "User not logged in";
        }

        // Buscar o usuário com a coleção movieGenres inicializada
        User userWithGenres = userRepository.findById(loggedUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Hibernate.initialize(userWithGenres.getMovieGenres()); // Inicializar a coleção

        List<String> movieGenres = requestBody.get("movieGenres");
        if (movieGenres == null || movieGenres.isEmpty()) {
            return "Invalid movie genres";
        }

        // Atualizar a pontuação dos gêneros do usuário
        List<User.MovieGenre> updatedGenres = userWithGenres.getMovieGenres().stream()
            .map(genre -> {
                if (movieGenres.contains(genre.getGenre())) {
                    int updatedScore = Integer.parseInt(genre.getScore()) + 5; // Incrementar pontuação
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