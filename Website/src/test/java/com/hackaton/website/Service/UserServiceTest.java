package com.hackaton.website.Service;

import com.hackaton.website.Entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRecommendMoviesForDifferentUsers() {
        // Create multiple mock users with different genre preferences
        User user1 = createUser("User1", new String[][]{
            {"Action", "1"}, {"Comedy", "22"}, {"Drama", "20"} , {"Thriller", "15"}
        });

        User user2 = createUser("User2", new String[][]{
            {"Horror", "4"}, {"Sci-Fi", "50"}, {"Fantasy", "2"}
        });

        User user3 = createUser("User3", new String[][]{
            {"Romance", "5"}, {"Thriller", "4"}, {"Adventure", "3"}
        });

        // Test recommendations for each user
        testRecommendationsForUser(user1);
        testRecommendationsForUser(user2);
        testRecommendationsForUser(user3);
    }

    private User createUser(String name, String[][] genres) {
        User user = new User();
        user.setName(name);
        List<String[]> movieGenres = new ArrayList<>();
        for (String[] genre : genres) {
            movieGenres.add(genre);
        }
        user.setMovieGenres(movieGenres);
        return user;
    }

    private void testRecommendationsForUser(User user) {
        // Call the recommendMovies method
        List<Map<String, String>> recommendations = userService.recommendMovies(user);

        // Assert that recommendations are not null and contain up to 20 movies
        assertNotNull(recommendations, "Recommendations should not be null for " + user.getName());
        assertTrue(recommendations.size() <= 20, "Recommendations should contain up to 20 movies for " + user.getName());

        // Print the user's preferred genres
        System.out.println("Preferred genres for " + user.getName() + ":");
        user.getMovieGenres().forEach(genre -> System.out.println("  Genre: " + genre[0] + ", Score: " + genre[1]));

        // Print the recommended movies
        System.out.println("Recommendations for " + user.getName() + ":");
        recommendations.forEach(movie -> System.out.println(
                "Title: " + movie.get("title") + 
                ", Genres: " + movie.get("genres") + 
                ", Rating: " + movie.get("averageRating")));

        // Assert that the recommendations contain movies from the user's preferred genres
        boolean containsPreferredGenres = recommendations.stream()
                .anyMatch(movie -> user.getMovieGenres().stream()
                        .anyMatch(genre -> movie.get("genres").contains(genre[0])));

        if (!containsPreferredGenres) {
            System.out.println("Error: None of the recommended movies match the user's preferred genres.");
        }

        assertTrue(containsPreferredGenres, "Recommendations should include movies from preferred genres for " + user.getName());
    }

    @Test
    void testGenerateTop10ByTop3Genres() {
        UserService userService = new UserService();

        // Case 1: User with distinct genre scores
        User user1 = new User();
        user1.setMovieGenres(Arrays.asList(
                new String[]{"Action", "10"},
                new String[]{"Comedy", "8"},
                new String[]{"Drama", "40"},
                new String[]{"Horror", "12"},
                new String[]{"Fantasy", "50"}
        ));

        // Case 2: User with tied genre scores
        User user2 = new User();
        user2.setMovieGenres(Arrays.asList(
                new String[]{"Action", "20"},
                new String[]{"Comedy", "20"},
                new String[]{"Drama", "20"},
                new String[]{"Horror", "10"}
        ));

        // Case 3: User with only 2 genres
        User user3 = new User();
        user3.setMovieGenres(Arrays.asList(
                new String[]{"Action", "30"},
                new String[]{"Comedy", "25"}
        ));

        // Test each user
        testTop10ByTop3GenresForUser(userService, user1, "Fantasy", "Drama", "Horror");
        testTop10ByTop3GenresForUser(userService, user2, "Action", "Comedy", "Drama");
        testTop10ByTop3GenresForUser(userService, user3, "Action", "Comedy", null);
    }

    private void testTop10ByTop3GenresForUser(UserService userService, User user, String expectedGenre1, String expectedGenre2, String expectedGenre3) {
        // Call the method
        Map<String, List<Map<String, String>>> top10ByGenre = userService.generateTop10ByTop3Genres(user);

        // Debug: Print the top 3 genres
        System.out.println("Top 3 genres for user:");
        user.getMovieGenres().stream()
                .sorted((a, b) -> Integer.compare(Integer.parseInt(b[1]), Integer.parseInt(a[1])))
                .limit(3)
                .forEach(genre -> System.out.println("  Genre: " + genre[0] + ", Score: " + genre[1]));

        // Debug: Print the results
        System.out.println("Top 10 Movies by Genre:");
        top10ByGenre.forEach((genre, movies) -> {
            System.out.println("Genre: " + genre);
            movies.forEach(movie -> System.out.println(
                    "  Title: " + movie.get("title") + 
                    ", Genres: " + movie.get("genres") + 
                    ", Rating: " + movie.get("averageRating")));
        });

        // Assertions
        assertEquals(expectedGenre3 == null ? 2 : 3, top10ByGenre.size(), "Should return top 10 movies for the correct number of genres");
        assertTrue(top10ByGenre.containsKey(expectedGenre1), "Should include '" + expectedGenre1 + "' genre");
        assertTrue(top10ByGenre.containsKey(expectedGenre2), "Should include '" + expectedGenre2 + "' genre");
        if (expectedGenre3 != null) {
            assertTrue(top10ByGenre.containsKey(expectedGenre3), "Should include '" + expectedGenre3 + "' genre");
        }

        for (List<Map<String, String>> movies : top10ByGenre.values()) {
            assertTrue(movies.size() <= 10, "Each genre should have up to 10 movies");
        }
    }
}
