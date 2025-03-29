package com.hackaton.website.Service;

import com.hackaton.website.Entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
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

        // Assert that the recommendations contain movies from the user's preferred genres
        boolean containsPreferredGenres = recommendations.stream()
                .anyMatch(movie -> user.getMovieGenres().stream()
                        .anyMatch(genre -> movie.get("genres").contains(genre[0])));
        assertTrue(containsPreferredGenres, "Recommendations should include movies from preferred genres for " + user.getName());

        // Print the recommended movies
        System.out.println("Recommendations for " + user.getName() + ":");
        userService.printRecommendedMovies(recommendations);
    }
}
