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

    private static final String GENRE_CSV_FOLDER = "src/main/resources/csv/MovieGenresCSV";

    public List<Map<String, String>> recommendMovies(User user) {
        List<String[]> movieGenres = user.getMovieGenres();
        if (movieGenres == null || movieGenres.isEmpty()) {
            throw new IllegalArgumentException("User has no movie genres defined.");
        }

        // Sort genres by user preference (descending order of scores)
        List<String[]> sortedGenres = movieGenres.stream()
                .sorted((a, b) -> Integer.compare(Integer.parseInt(b[1]), Integer.parseInt(a[1])))
                .collect(Collectors.toList());

        List<Map<String, String>> recommendations = new ArrayList<>();
        Set<String> recommendedMovies = new HashSet<>();

        // Load movies from CSVs
        Map<String, List<Map<String, String>>> genreMovies = loadMoviesByGenre();

        // Prioritize recommendations from preferred genres
        int totalScore = sortedGenres.stream()
                .mapToInt(genre -> Integer.parseInt(genre[1]))
                .sum();

        for (String[] genrePreference : sortedGenres) {
            String genre = genrePreference[0];
            int score = Integer.parseInt(genrePreference[1]);
            if (!genreMovies.containsKey(genre)) continue;

            List<Map<String, String>> movies = genreMovies.get(genre);
            Collections.shuffle(movies);

            // Calculate the number of movies to recommend for this genre
            int genreQuota = Math.max(1, (score * 16) / totalScore); // Focus more on preferred genres
            for (Map<String, String> movie : movies) {
                if (recommendedMovies.size() >= 18) break; // Reserve 2 slots for non-preferred genres
                if (recommendedMovies.add(movie.get("title"))) {
                    recommendations.add(movie);
                    if (--genreQuota == 0) break;
                }
            }
        }

        // Add 1 or 2 movies from non-preferred genres
        List<String> nonPreferredGenres = genreMovies.keySet().stream()
                .filter(genre -> sortedGenres.stream().noneMatch(g -> g[0].equals(genre)))
                .collect(Collectors.toList());
        Collections.shuffle(nonPreferredGenres);

        for (String genre : nonPreferredGenres) {
            if (genreMovies.containsKey(genre)) {
                List<Map<String, String>> movies = genreMovies.get(genre);
                Collections.shuffle(movies);
                for (Map<String, String> movie : movies) {
                    if (recommendedMovies.size() >= 20) break;
                    if (recommendedMovies.add(movie.get("title"))) {
                        recommendations.add(movie);
                    }
                }
            }
            if (recommendedMovies.size() >= 20) break;
        }

        return recommendations;
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
                try (CSVParser parser = CSVParser.parse(file, java.nio.charset.StandardCharsets.UTF_8, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
                    List<Map<String, String>> movies = new ArrayList<>();
                    for (CSVRecord record : parser) {
                        movies.add(record.toMap());
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
}
