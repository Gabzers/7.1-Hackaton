import java.util.List;
import java.util.stream.Collectors;

public class RecommendationEngine {
    public List<Movie> filterMovies(List<Movie> movies, UserPreferences preferences) {
        return movies.stream()
            .filter(movie -> preferences.getFavoriteGenres().stream()
                                         .anyMatch(genre -> movie.getGenre().contains(getGenreName(genre))))
            .filter(movie -> filterByTime(movie, preferences.getPreferredTime()))
            .filter(movie -> filterByDevice(movie, preferences.getPreferredDevice()))
            .filter(movie -> !movie.getGenre().equalsIgnoreCase(preferences.getAvoidGenres()))
            .collect(Collectors.toList());
    }

    private boolean filterByTime(Movie movie, int timeChoice) {
        // Implement logic to filter by time
        return true; // Replace with actual logic
    }

    private boolean filterByDevice(Movie movie, int deviceChoice) {
        // Implement logic to filter by device
        return true; // Replace with actual logic
    }

    private String getGenreName(int choice) {
        // Map genre choice number to genre name
        switch (choice) {
            case 1: return "Action";
            case 2: return "Fantasy";
            case 3: return "Adventure";
            case 4: return "Comedy";
            case 5: return "Drama";
            case 6: return "Science Fiction";
            case 7: return "Horror";
            case 8: return "Thriller";
            case 9: return "Romance";
            case 10: return "Animation";
            case 11: return "Documentary";
            case 12: return "Crime";
            case 13: return "Musical";
            case 14: return "Biographical";
            case 15: return "Historical";
            case 16: return "Western";
            default: return "";
        }
    }
}
