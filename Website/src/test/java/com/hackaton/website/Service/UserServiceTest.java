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

    @Test
    void testRecommendTop3GenresMoviesForDifferentUsers() {
        // Create multiple mock users with different genre preferences
        User user1 = createUser("User1", new String[][]{
            {"Action", "10"}, {"Comedy", "8"}, {"Drama", "6"}, {"Thriller", "4"}
        });

        User user2 = createUser("User2", new String[][]{
            {"Horror", "9"}, {"Sci-Fi", "7"}, {"Fantasy", "5"}, {"Adventure", "3"}
        });

        User user3 = createUser("User3", new String[][]{
            {"Romance", "10"}, {"Thriller", "9"}, {"Adventure", "8"}, {"Comedy", "7"}
        });

        // Test recommendations for each user
        testTop3GenresRecommendationsForUser(user1);
        testTop3GenresRecommendationsForUser(user2);
        testTop3GenresRecommendationsForUser(user3);
    }

    private User createUser(String name, String[][] genres) {
        User user = new User();
        user.setName(name);
        List<User.MovieGenre> movieGenres = new ArrayList<>();
        for (String[] genre : genres) {
            movieGenres.add(new User.MovieGenre(genre[0], genre[1]));
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
                        .anyMatch(genre -> movie.get("genres").contains(genre.getGenre())));
        assertTrue(containsPreferredGenres, "Recommendations should include movies from preferred genres for " + user.getName());

        // Print the recommended movies
        System.out.println("Recommendations for " + user.getName() + ":");
        userService.printRecommendedMovies(recommendations);
    }

    private void testTop3GenresRecommendationsForUser(User user) {
        // Call the recommendTop3GenresMovies method
        Map<String, List<Map<String, String>>> recommendations = userService.recommendTop3GenresMovies(user);

        // Assert that recommendations are not null and contain up to 3 genres
        assertNotNull(recommendations, "Recommendations should not be null for " + user.getName());
        assertEquals(3, recommendations.size(), "Recommendations should contain exactly 3 genres for " + user.getName());

        // Assert that each genre contains up to 20 movies
        recommendations.forEach((genre, movies) -> {
            assertNotNull(movies, "Movies list for genre " + genre + " should not be null");
            assertTrue(movies.size() <= 20, "Movies list for genre " + genre + " should contain up to 20 movies");
        });

        // Print the recommended movies for each genre
        System.out.println("Recommendations for " + user.getName() + ":");
        recommendations.forEach((genre, movies) -> {
            System.out.println("Genre: " + genre);
            System.out.println("Movies:");
            movies.forEach(movie -> {
                System.out.println("  Title: " + movie.get("title") + 
                                   ", Genres: " + movie.get("genres") + 
                                   ", Rating: " + movie.get("averageRating"));
            });
            System.out.println("Total movies in genre '" + genre + "': " + movies.size());
        });
        System.out.println("---------------------------------------------------");
    }
}
