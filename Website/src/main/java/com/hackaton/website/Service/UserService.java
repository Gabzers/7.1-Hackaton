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

    private static final String GENRE_CSV_FOLDER = "DataAnalysis/MovieGenresCSV";

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
        Random random = new Random();

        // Load movies from CSVs
        Map<String, List<Map<String, String>>> genreMovies = loadMoviesByGenre();

        // Recommend movies based on user preferences
        Set<String> recommendedMovies = new HashSet<>();
        for (String[] genrePreference : sortedGenres) {
            String genre = genrePreference[0];
            if (genreMovies.containsKey(genre)) {
                List<Map<String, String>> movies = genreMovies.get(genre);
                Collections.shuffle(movies); // Shuffle to ensure randomness
                for (Map<String, String> movie : movies) {
                    if (recommendedMovies.size() >= 10) break;
                    if (recommendedMovies.add(movie.get("title"))) {
                        recommendations.add(movie);
                    }
                }
            }
        }

        // Occasionally recommend movies from other genres
        List<String> otherGenres = genreMovies.keySet().stream()
                .filter(genre -> sortedGenres.stream().noneMatch(g -> g[0].equals(genre)))
                .collect(Collectors.toList());
        Collections.shuffle(otherGenres);
        for (String genre : otherGenres) {
            if (genreMovies.containsKey(genre)) {
                List<Map<String, String>> movies = genreMovies.get(genre);
                Collections.shuffle(movies);
                for (Map<String, String> movie : movies) {
                    if (recommendedMovies.size() >= 10) break;
                    if (recommendedMovies.add(movie.get("title"))) {
                        recommendations.add(movie);
                    }
                }
            }
        }

        return recommendations;
    }

    private Map<String, List<Map<String, String>>> loadMoviesByGenre() {
        Map<String, List<Map<String, String>>> genreMovies = new HashMap<>();
        File folder = Paths.get(GENRE_CSV_FOLDER).toFile();
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalStateException("Genre CSV folder not found: " + GENRE_CSV_FOLDER);
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
}
