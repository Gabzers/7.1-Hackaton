package com.hackaton.website.Service;

import com.hackaton.website.Entity.User;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Service class responsible for user-related business logic.
 * Provides methods for recommending movies, managing missions, and more.
 * 
 * @author Gabriel Proença
 */
@Service
public class UserService {

    private static final String GENRE_CSV_FOLDER = "website/src/main/resources/csv/MovieGenresCSV";

    /**
     * Recommends movies based on the user's preferred genres.
     *
     * @param user the user for whom to recommend movies
     * @return a list of recommended movies
     */
    public List<Map<String, String>> recommendMovies(User user) {
        List<User.MovieGenre> movieGenres = user.getMovieGenres();
        if (movieGenres == null || movieGenres.isEmpty()) {
            throw new IllegalArgumentException("User has no movie genres defined.");
        }

        // Sort genres by user preference (descending order of scores)
        List<User.MovieGenre> sortedGenres = movieGenres.stream()
                .sorted((a, b) -> Integer.compare(Integer.parseInt(b.getScore()), Integer.parseInt(a.getScore())))
                .collect(Collectors.toList());

        List<Map<String, String>> recommendations = new ArrayList<>();
        Set<String> recommendedMovies = new HashSet<>();

        // Load movies from CSVs
        Map<String, List<Map<String, String>>> genreMovies = loadMoviesByGenre();

        // Calculate total score and handle case where all scores are zero
        int totalScore = sortedGenres.stream()
                .mapToInt(genre -> Integer.parseInt(genre.getScore()))
                .sum();
        if (totalScore == 0) {
            throw new IllegalArgumentException("User has no valid preferences (all scores are zero).");
        }

        // Prioritize recommendations from preferred genres
        for (User.MovieGenre genrePreference : sortedGenres) {
            String genre = genrePreference.getGenre();
            int score = Integer.parseInt(genrePreference.getScore());
            if (!genreMovies.containsKey(genre)) continue;

            List<Map<String, String>> movies = genreMovies.get(genre);
            Collections.shuffle(movies);

            // Ensure releaseYear is present
            for (Map<String, String> movie : movies) {
                movie.putIfAbsent("releaseYear", "Unknown");
            }

            // Calculate the number of movies to recommend for this genre
            int genreQuota = Math.max(1, (score * 18) / totalScore); // Adjusted to allocate up to 18 slots
            for (Map<String, String> movie : movies) {
                if (recommendedMovies.size() >= 10) break; // Limit total recommendations to 10
                if (recommendedMovies.add(movie.get("title"))) {
                    recommendations.add(movie);
                    if (--genreQuota == 0) break;
                }
            }
        }

        // Add movies from non-preferred genres if slots are still available
        if (recommendedMovies.size() < 10) {
            List<String> nonPreferredGenres = genreMovies.keySet().stream()
                    .filter(genre -> sortedGenres.stream().noneMatch(g -> g.getGenre().equals(genre)))
                    .collect(Collectors.toList());
            Collections.shuffle(nonPreferredGenres);

            for (String genre : nonPreferredGenres) {
                if (!genreMovies.containsKey(genre)) continue;

                List<Map<String, String>> movies = genreMovies.get(genre);
                Collections.shuffle(movies);
                for (Map<String, String> movie : movies) {
                    if (recommendedMovies.size() >= 10) break;
                    if (recommendedMovies.add(movie.get("title"))) {
                        recommendations.add(movie);
                    }
                }
                if (recommendedMovies.size() >= 10) break;
            }
        }

        return recommendations;
    }

    /**
     * Recommends movies for the user's top 3 preferred genres.
     *
     * @param user the user for whom to recommend movies
     * @return a map of genres to their recommended movies
     */
    public Map<String, List<Map<String, String>>> recommendTop3GenresMovies(User user) {
        List<User.MovieGenre> movieGenres = user.getMovieGenres();
        if (movieGenres == null || movieGenres.isEmpty()) {
            throw new IllegalArgumentException("User has no movie genres defined.");
        }

        // Sort genres by user preference (descending order of scores)
        List<User.MovieGenre> sortedGenres = movieGenres.stream()
                .sorted((a, b) -> {
                    int scoreComparison = Integer.compare(Integer.parseInt(b.getScore()), Integer.parseInt(a.getScore()));
                    if (scoreComparison == 0) {
                        // Randomize order if scores are equal
                        return new Random().nextInt(2) * 2 - 1; // Randomly return -1 or 1
                    }
                    return scoreComparison;
                })
                .limit(3) // Limit to top 3 genres
                .collect(Collectors.toList());

        // Check if all top 3 genres have a score of 0
        boolean allScoresZero = sortedGenres.stream()
                .allMatch(genre -> Integer.parseInt(genre.getScore()) == 0);
        if (allScoresZero) {
            System.err.println("All top 3 genres have a score of 0. No recommendations available.");
            return Collections.emptyMap(); // Return an empty map
        }

        Map<String, List<Map<String, String>>> genreRecommendations = new HashMap<>();
        Map<String, List<Map<String, String>>> genreMovies = loadMoviesByGenre();

        // Recommend movies for each of the top 3 genres
        for (User.MovieGenre genrePreference : sortedGenres) {
            String genre = genrePreference.getGenre();
            if (!genreMovies.containsKey(genre)) continue;

            List<Map<String, String>> movies = genreMovies.get(genre);
            Collections.shuffle(movies);

            List<Map<String, String>> recommendations = new ArrayList<>();
            Set<String> recommendedMovies = new HashSet<>();

            for (Map<String, String> movie : movies) {
                if (recommendations.size() >= 10) break; // Limit to 10 movies
                if (recommendedMovies.add(movie.get("title"))) {
                    recommendations.add(movie);
                }
            }

            genreRecommendations.put(genre, recommendations);
        }

        return genreRecommendations;
    }

    /**
     * Recommends the top 10 movies based on the user's preferences.
     *
     * @param user the user for whom to recommend movies
     * @return a list of the top 10 recommended movies
     */
    public List<Map<String, String>> recommendTop10Movies(User user) {
        // Simulate a database or external API call to fetch all movies
        List<Map<String, String>> allMovies = fetchAllMovies();

        // Filter movies by user's preferred genres
        List<Map<String, String>> filteredMovies = allMovies.stream()
            .filter(movie -> user.getMovieGenres().stream()
                .anyMatch(genre -> movie.get("genres").contains(genre.getGenre())))
            .collect(Collectors.toList());

        // Sort movies by average rating (descending order)
        filteredMovies.sort((movie1, movie2) -> 
            Double.compare(
                Double.parseDouble(movie2.get("averageRating")), 
                Double.parseDouble(movie1.get("averageRating"))
            )
        );

        // Return the top 10 movies
        return filteredMovies.stream().limit(10).collect(Collectors.toList());
    }

    /**
     * Recommends movies from the user's least preferred genres.
     *
     * @param user the user for whom to recommend movies
     * @return a list of recommended movies from non-preferred genres
     */
    public List<Map<String, String>> recommendLeastPreferredMovies(User user) {
        List<User.MovieGenre> movieGenres = user.getMovieGenres();
        if (movieGenres == null || movieGenres.isEmpty()) {
            throw new IllegalArgumentException("User has no movie genres defined.");
        }

        // Identify genres the user has not rated or rated with the lowest scores
        Set<String> preferredGenres = movieGenres.stream()
                .filter(genre -> Integer.parseInt(genre.getScore()) > 0)
                .map(User.MovieGenre::getGenre)
                .collect(Collectors.toSet());

        Map<String, List<Map<String, String>>> genreMovies = loadMoviesByGenre();
        List<String> nonPreferredGenres = genreMovies.keySet().stream()
                .filter(genre -> !preferredGenres.contains(genre))
                .collect(Collectors.toList());

        List<Map<String, String>> recommendations = new ArrayList<>();
        Set<String> recommendedMovies = new HashSet<>();

        // Shuffle non-preferred genres and recommend movies
        Collections.shuffle(nonPreferredGenres);
        for (String genre : nonPreferredGenres) {
            if (!genreMovies.containsKey(genre)) continue;

            List<Map<String, String>> movies = genreMovies.get(genre);
            Collections.shuffle(movies);

            for (Map<String, String> movie : movies) {
                if (recommendedMovies.size() >= 10) break; // Limit to 10 movies
                if (recommendedMovies.add(movie.get("title"))) {
                    recommendations.add(movie);
                }
            }
            if (recommendedMovies.size() >= 10) break;
        }

        return recommendations;
    }

    /**
     * Fetches all movies from a simulated database or external API.
     *
     * @return a list of all movies
     */
    private List<Map<String, String>> fetchAllMovies() {
        // Example movie data
        List<Map<String, String>> movies = new ArrayList<>();
        movies.add(Map.of("title", "Movie1", "genres", "Action,Comedy", "averageRating", "8.5"));
        movies.add(Map.of("title", "Movie2", "genres", "Drama,Thriller", "averageRating", "7.8"));
        // ...add more movies...
        return movies;
    }

    /**
     * Loads movies by genre from CSV files.
     *
     * @return a map of genres to their movies
     */
    private Map<String, List<Map<String, String>>> loadMoviesByGenre() {
        Map<String, List<Map<String, String>>> genreMovies = new HashMap<>();
        File folder = Paths.get(GENRE_CSV_FOLDER).toFile();
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalStateException("Genre CSV folder not found: " + folder.getAbsolutePath());
        }

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".csv")) {
                String genre = file.getName().replace(".csv", "");
                try (CSVParser parser = CSVParser.parse(file, java.nio.charset.StandardCharsets.UTF_8, 
                        CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
                    List<Map<String, String>> movies = new ArrayList<>();
                    for (CSVRecord record : parser) {
                        Map<String, String> movie = new HashMap<>();
                        movie.put("title", record.get("title"));
                        movie.put("genres", record.get("genres"));
                        movie.put("averageRating", record.get("averageRating"));

                        // Parse releaseYear as an integer and format it correctly
                        String releaseYear = record.isMapped("releaseYear") ? record.get("releaseYear") : "Unknown";
                        if (!releaseYear.equals("Unknown")) {
                            try {
                                releaseYear = releaseYear.contains(".") 
                                    ? releaseYear.split("\\.")[0] // Remove decimal part
                                    : releaseYear;
                            } catch (Exception e) {
                                releaseYear = "Unknown";
                            }
                        }
                        movie.put("releaseYear", releaseYear);

                        movies.add(movie);
                    }
                    genreMovies.put(genre, movies);
                } catch (IOException e) {
                    System.err.println("Error reading CSV for genre: " + genre);
                }
            }
        }

        return genreMovies;
    }

    /**
     * Retrieves genres for a movie based on its title.
     *
     * @param movieTitle the title of the movie
     * @return a list of genres for the movie
     */
    public List<String> getGenresByMovieTitle(String movieTitle) {
        File folder = Paths.get(GENRE_CSV_FOLDER).toFile();
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalStateException("Genre CSV folder not found: " + folder.getAbsolutePath());
        }

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".csv")) {
                try (CSVParser parser = CSVParser.parse(file, java.nio.charset.StandardCharsets.UTF_8,
                        CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
                    for (CSVRecord record : parser) {
                        if (record.get("title").equalsIgnoreCase(movieTitle)) {
                            String genres = record.get("genres");
                            return Arrays.asList(genres.split(","));
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error reading CSV file: " + file.getName());
                }
            }
        }

        return Collections.emptyList(); // Retornar lista vazia se o filme não for encontrado
    }

    /**
     * Recommends random products from predefined CSV files.
     *
     * @return a list of recommended products
     */
    public List<Map<String, String>> recommendRandomProducts() {
        String[] csvPaths = {
            "website/src/main/resources/csv/CostBenefit_Results/Products_10_To_15_Euros.csv",
            "website/src/main/resources/csv/CostBenefit_Results/Products_5_To_10_Euros.csv",
            "website/src/main/resources/csv/CostBenefit_Results/Products_Under_5_Euros.csv"
        };

        List<Map<String, String>> selectedProducts = new ArrayList<>();

        for (String csvPath : csvPaths) {
            File csvFile = Paths.get(csvPath).toFile();
            if (!csvFile.exists()) {
                System.err.println("CSV file not found: " + csvPath);
                continue;
            }

            try (CSVParser parser = CSVParser.parse(csvFile, java.nio.charset.StandardCharsets.UTF_8,
                    CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
                List<Map<String, String>> products = new ArrayList<>();
                for (CSVRecord record : parser) {
                    Map<String, String> product = new HashMap<>();
                    product.put("product_name", record.get("product_name"));
                    product.put("category", record.get("category"));
                    product.put("cost", record.get("cost"));
                    products.add(product);
                }
                Collections.shuffle(products);
                selectedProducts.addAll(products.stream().limit(4).toList());
            } catch (IOException e) {
                System.err.println("Error reading CSV file: " + csvPath);
            }
        }

        return selectedProducts;
    }

    /**
     * Prints the recommended movies to the console.
     *
     * @param recommendations the list of recommended movies
     */
    public void printRecommendedMovies(List<Map<String, String>> recommendations) {
        System.out.println("Recommended Movies:");
        for (Map<String, String> movie : recommendations) {
            System.out.println("Title: " + movie.get("title") + ", Genres: " + movie.get("genres") + 
                               ", Rating: " + movie.get("averageRating"));
        }
    }

    /**
     * Completes a mission for the user and awards points.
     *
     * @param user        the user completing the mission
     * @param missionName the name of the mission
     * @return a map containing the points earned
     */
    public Map<String, Integer> completeMission(User user, String missionName) {
        System.out.println("Completing mission: " + missionName);
        System.out.println("User before update: Points = " + user.getPoints() + ", ProgressBar = " + user.getProgressBar());

        Map<String, Integer> missionPoints = Map.of(
            "DailyLogin", 20, 
            "watch3Movies", 50,
            "Buysomething", 75,
            "Rateamovie", 20,
            "Likeamovie", 20
        );

        if (!missionPoints.containsKey(missionName)) {
            System.err.println("Invalid mission name: " + missionName);
            throw new IllegalArgumentException("Invalid mission name: " + missionName);
        }

        int pointsEarned = missionPoints.get(missionName);
        user.setPoints((user.getPoints() != null ? user.getPoints() : 0) + pointsEarned);

        // Update progress bar
        int currentProgress = user.getProgressBar() != null ? user.getProgressBar() : 0;
        int updatedProgress = currentProgress + pointsEarned;
        if (updatedProgress > 1000) {
            updatedProgress = 1000; // Cap progress bar at 1000
        }
        user.setProgressBar(updatedProgress);

        // Log para depuração
        System.out.println("User after update: Points = " + user.getPoints() + ", ProgressBar = " + user.getProgressBar());

        return Map.of("pointsEarned", pointsEarned);
    }

    /**
     * Claims a reward for the user based on the specified tier.
     *
     * @param user the user claiming the reward
     * @param tier the tier of the reward
     * @return a message indicating the result of the operation
     */
    public String claimReward(User user, int tier) {
        int rewardPoints = tier * 10; // Points awarded for the given tier

        // Ensure progressBar is initialized
        if (user.getPoints() == null) {
            user.setPoints(0);
        }

        Set<String> completedMissions = user.getCompletedMissions();
        if (completedMissions.contains("Reward" + tier)) {
            return "Reward for Tier " + tier + " has already been claimed.";
        }

        // Add points to the user's total and mark reward as claimed
        user.setPoints(user.getPoints() + rewardPoints);
        completedMissions.add("Reward" + tier);
        user.setCompletedMissions(completedMissions);

        return "Reward for Tier " + tier + " claimed successfully!";
    }
}