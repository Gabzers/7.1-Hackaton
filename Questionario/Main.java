import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Question: Favorite Movie Genre
        // Identifies the user's central preference to filter movies according to their taste.
        System.out.println("## What is your favorite movie genre? (e.g., action, fantasy, adventure, comedy, drama, science fiction, horror, thriller, romance, animation, documentary, crime, musical, biographical, historical, western)");
        System.out.println("Choose up to 3 options:");
        System.out.println("1. Action");
        System.out.println("2. Fantasy");
        System.out.println("3. Adventure");
        System.out.println("4. Comedy");
        System.out.println("5. Drama");
        System.out.println("6. Science Fiction");
        System.out.println("7. Horror");
        System.out.println("8. Thriller");
        System.out.println("9. Romance");
        System.out.println("10. Animation");
        System.out.println("11. Documentary");
        System.out.println("12. Crime");
        System.out.println("13. Musical");
        System.out.println("14. Biographical");
        System.out.println("15. Historical");
        System.out.println("16. Western");
        System.out.print("Enter up to 3 numbers separated by commas: ");
        String[] genreChoices = scanner.nextLine().split(",");
        List<Integer> genreChoiceList = Arrays.stream(genreChoices)
                                              .map(String::trim)
                                              .map(Integer::parseInt)
                                              .collect(Collectors.toList());

        // 2. Question: Preferred Time to Watch Movies
        // Synchronizes recommendations according to the viewing time (e.g., short series during lunch).
        System.out.println("\n## What is your preferred time to watch movies? (morning, afternoon, evening)");
        System.out.println("Choose an option:");
        System.out.println("1. Morning");
        System.out.println("2. Afternoon");
        System.out.println("3. Evening");
        System.out.print("Enter the corresponding number: ");
        int timeChoice = scanner.nextInt();
        scanner.nextLine();

        // 3. Question: Device Used to Watch
        // Adapts the visual experience according to the device (TV, smartphone, PC).
        System.out.println("\n## What device do you usually use to watch? (TV, smartphone, PC)");
        System.out.println("Choose an option:");
        System.out.println("1. TV");
        System.out.println("2. Smartphone");
        System.out.println("3. PC");
        System.out.print("Enter the corresponding number: ");
        int deviceChoice = scanner.nextInt();
        scanner.nextLine();

        // 4. Question: Favorite Actors, Directors, or Franchises
        // Identifies specific affinities that influence recommendations.
        System.out.println("\n## Who are your favorite actors, directors, or franchises?");
        String favorites = scanner.nextLine();

        // 5. Question: Preference for New or Classic Movies
        // Aligns the catalog with the user's temporal taste.
        System.out.println("\n## Do you prefer new or classic movies?");
        System.out.println("Choose an option:");
        System.out.println("1. New");
        System.out.println("2. Classic");
        System.out.print("Enter the corresponding number: ");
        int preferenceChoice = scanner.nextInt();
        scanner.nextLine();

        // 6. Question: Genres or Movies the User Dislikes
        // Avoids irrelevant recommendations or those that may cause dissatisfaction.
        System.out.println("\n## Are there any genres or movies you definitely don't want to see?");
        System.out.println("Enter the name of the genre or movie (or 'None' if there are none):");
        String avoid = scanner.nextLine();

        // 7. Question: Age Group and Gender (Demographic Data)
        // Demographic data helps create accurate profiles and correlate preferences with specific groups.
        System.out.println("\n## What is your age group and gender? (e.g., 25 years old, Male)");
        String demographics = scanner.nextLine();

        // Displaying the answers for confirmation (can be adapted as needed)
        System.out.println("\n--- Questionnaire Answers ---");
        System.out.println("Favorite genres (numbers): " + genreChoiceList);
        System.out.println("Preferred time (number): " + timeChoice);
        System.out.println("Device used (number): " + deviceChoice);
        System.out.println("Favorite actors/directors/franchises: " + favorites);
        System.out.println("Preference for movies: " + (preferenceChoice == 1 ? "New" : "Classic"));
        System.out.println("Genres or movies to avoid: " + avoid);
        System.out.println("Age group and gender: " + demographics);

        // Load data from BestMovies.csv
        List<String[]> bestMovies = loadBestMovies("BestMovies.csv");

        // Filter movies based on questionnaire answers
        System.out.println("\nFiltering movies based on your preferences...");
        List<String[]> recommendedMovies = bestMovies.stream()
            .filter(movie -> {
                boolean matches = filterByGenres(movie, genreChoiceList);
                System.out.println("Genre filter for movie " + Arrays.toString(movie) + ": " + matches);
                return matches;
            })
            .filter(movie -> {
                boolean matches = filterByTime(movie, timeChoice);
                System.out.println("Time filter for movie " + Arrays.toString(movie) + ": " + matches);
                return matches;
            })
            .filter(movie -> {
                boolean matches = filterByDevice(movie, deviceChoice);
                System.out.println("Device filter for movie " + Arrays.toString(movie) + ": " + matches);
                return matches;
            })
            .filter(movie -> {
                boolean matches = filterByAvoid(movie, avoid);
                System.out.println("Avoid filter for movie " + Arrays.toString(movie) + ": " + matches);
                return matches;
            })
            .collect(Collectors.toList());

        System.out.println("\nFiltered movies: " + recommendedMovies.size());
        recommendedMovies.forEach(movie -> System.out.println(Arrays.toString(movie)));

        // Save recommendations to a new CSV file
        System.out.println("\nSaving recommendations to 'UserRecommendations.csv'...");
        saveRecommendations(recommendedMovies, "UserRecommendations.csv");
        System.out.println("Recommendations saved successfully!");

        scanner.close();
    }

    private static List<String[]> loadBestMovies(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Error: The file '" + filePath + "' does not exist. Creating a default file...");
            createDefaultBestMoviesFile(filePath);
        }

        List<String[]> movies = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                movies.add(line.split(","));
            }
        } catch (IOException e) {
            System.err.println("Error loading the file: " + e.getMessage());
        }
        return movies;
    }

    private static void createDefaultBestMoviesFile(String filePath) {
        List<String[]> defaultMovies = Arrays.asList(
            new String[]{"1", "Action", "Morning", "TV", "Actor1", "New"},
            new String[]{"2", "Comedy", "Afternoon", "Smartphone", "Actor2", "Classic"},
            new String[]{"3", "Drama", "Evening", "PC", "Actor3", "New"}
        );

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] movie : defaultMovies) {
                bw.write(String.join(",", movie));
                bw.newLine();
            }
            System.out.println("Default 'BestMovies.csv' file created successfully.");
        } catch (IOException e) {
            System.err.println("Error creating the default file: " + e.getMessage());
        }
    }

    private static boolean filterByGenres(String[] movie, List<Integer> genreChoices) {
        // Implement logic to filter by multiple genres based on genreChoices
        // Example: movie[1] contains the genre of the movie
        return genreChoices.stream().anyMatch(choice -> movie[1].contains(getGenreName(choice)));
    }

    private static String getGenreName(int choice) {
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

    private static boolean filterByTime(String[] movie, int timeChoice) {
        // Implement logic to filter by preferred time
        return true; // Replace with actual logic
    }

    private static boolean filterByDevice(String[] movie, int deviceChoice) {
        // Implement logic to filter by device
        return true; // Replace with actual logic
    }

    private static boolean filterByAvoid(String[] movie, String avoid) {
        // Implement logic to avoid specific genres or movies
        return !movie[1].equalsIgnoreCase(avoid); // Basic example
    }

    private static void saveRecommendations(List<String[]> movies, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] movie : movies) {
                bw.write(String.join(",", movie));
                bw.newLine();
            }
            System.out.println("File saved: " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving the file: " + e.getMessage());
        }
    }
}