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

@Service
public class UserService {

    private static final String GENRE_CSV_FOLDER = "website/src/main/resources/csv/MovieGenresCSV";

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

            // Calculate the number of movies to recommend for this genre
            int genreQuota = Math.max(1, (score * 18) / totalScore); // Adjusted to allocate up to 18 slots
            for (Map<String, String> movie : movies) {
                if (recommendedMovies.size() >= 20) break; // Limit total recommendations to 20
                if (recommendedMovies.add(movie.get("title"))) {
                    recommendations.add(movie);
                    if (--genreQuota == 0) break;
                }
            }
        }

        // Add movies from non-preferred genres if slots are still available
        if (recommendedMovies.size() < 20) {
            List<String> nonPreferredGenres = genreMovies.keySet().stream()
                    .filter(genre -> sortedGenres.stream().noneMatch(g -> g.getGenre().equals(genre)))
                    .collect(Collectors.toList());
            Collections.shuffle(nonPreferredGenres);

            for (String genre : nonPreferredGenres) {
                if (!genreMovies.containsKey(genre)) continue;

                List<Map<String, String>> movies = genreMovies.get(genre);
                Collections.shuffle(movies);
                for (Map<String, String> movie : movies) {
                    if (recommendedMovies.size() >= 20) break;
                    if (recommendedMovies.add(movie.get("title"))) {
                        recommendations.add(movie);
                    }
                }
                if (recommendedMovies.size() >= 20) break;
            }
        }

        return recommendations;
    }

    public Map<String, List<Map<String, String>>> recommendTop3GenresMovies(User user) {
        List<User.MovieGenre> movieGenres = user.getMovieGenres();
        if (movieGenres == null || movieGenres.isEmpty()) {
            throw new IllegalArgumentException("User has no movie genres defined.");
        }

        // Sort genres by user preference (descending order of scores)
        List<User.MovieGenre> sortedGenres = movieGenres.stream()
                .sorted((a, b) -> Integer.compare(Integer.parseInt(b.getScore()), Integer.parseInt(a.getScore())))
                .limit(3) // Limit to top 3 genres
                .collect(Collectors.toList());

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
                if (recommendations.size() >= 15) break; // alterar numero de filmes escolhidos
                if (recommendedMovies.add(movie.get("title"))) {
                    recommendations.add(movie);
                }
            }

            genreRecommendations.put(genre, recommendations);
        }

        return genreRecommendations;
    }

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
                if (recommendedMovies.size() >= 20) break; // Limit to 20 movies
                if (recommendedMovies.add(movie.get("title"))) {
                    recommendations.add(movie);
                }
            }
            if (recommendedMovies.size() >= 20) break;
        }

        return recommendations;
    }

    // Simulated method to fetch all movies (replace with actual implementation)
    private List<Map<String, String>> fetchAllMovies() {
        // Example movie data
        List<Map<String, String>> movies = new ArrayList<>();
        movies.add(Map.of("title", "Movie1", "genres", "Action,Comedy", "averageRating", "8.5"));
        movies.add(Map.of("title", "Movie2", "genres", "Drama,Thriller", "averageRating", "7.8"));
        // ...add more movies...
        return movies;
    }

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

    public void printRecommendedMovies(List<Map<String, String>> recommendations) {
        System.out.println("Recommended Movies:");
        for (Map<String, String> movie : recommendations) {
            System.out.println("Title: " + movie.get("title") + ", Genres: " + movie.get("genres") + 
                               ", Rating: " + movie.get("averageRating"));
        }
    }

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

    public String claimReward(User user, int tier) {
        int requiredProgress = tier * 50; // Progress required for the given tier

        // Ensure progressBar is initialized
        if (user.getProgressBar() == null) {
            user.setProgressBar(0);
        }

        Set<String> completedMissions = user.getCompletedMissions();
        if (completedMissions.contains("Reward" + tier)) {
            return "Reward for Tier " + tier + " has already been claimed.";
        }

        // Check if the user has enough progress to claim the reward
        if (user.getProgressBar() >= requiredProgress) {
            // Deduct only the progress required for this tier and mark reward as claimed
            user.setProgressBar(user.getProgressBar() - 50); // Deduct 50 points for the current reward
            completedMissions.add("Reward" + tier);
            user.setCompletedMissions(completedMissions);

            return "Reward for Tier " + tier + " claimed successfully!";
        }

        // If not enough progress, return an empty string (no message)
        return "";
    }
}