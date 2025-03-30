import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Collect user preferences through modular methods
        List<Integer> genreChoiceList = askFavoriteGenres(scanner);
        int timeChoice = askPreferredTime(scanner);
        int deviceChoice = askPreferredDevice(scanner);
        String favorites = askFavoriteActorsDirectors(scanner);
        int preferenceChoice = askNewOrClassic(scanner);
        String avoid = askGenresToAvoid(scanner);
        String demographics = askDemographics(scanner);

        // Displaying the answers for confirmation
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
            .filter(movie -> filterByGenres(movie, genreChoiceList))
            .filter(movie -> filterByTime(movie, timeChoice))
            .filter(movie -> filterByDevice(movie, deviceChoice))
            .filter(movie -> filterByAvoid(movie, avoid))
            .collect(Collectors.toList());

        System.out.println("\nFiltered movies: " + recommendedMovies.size());
        recommendedMovies.forEach(movie -> System.out.println(Arrays.toString(movie)));

        // Save recommendations to a new CSV file
        System.out.println("\nSaving recommendations to 'UserRecommendations.csv'...");
        saveRecommendations(recommendedMovies, "UserRecommendations.csv");
        System.out.println("Recommendations saved successfully!");

        // Generate a top 10 movies list based on user preferences
        System.out.println("\nGenerating Top 10 Movies for 'Trending Now'...");
        List<String[]> top10Movies = recommendedMovies.stream()
            .limit(10)
            .collect(Collectors.toList());
        saveTrendingNow(top10Movies, "TrendingNow.csv");
        System.out.println("Top 10 Movies saved successfully for 'Trending Now'!");

        scanner.close();
    }

    private static List<Integer> askFavoriteGenres(Scanner scanner) {
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
        System.out.println("17. Mystery");
        System.out.println("18. War");
        System.out.println("19. Family");
        System.out.println("20. Sport");
        System.out.println("21. Fantasy Adventure");
        System.out.println("22. Superhero");
        System.out.println("23. Psychological Thriller");
        System.out.println("24. Epic");
        System.out.println("25. Noir");
        System.out.print("Enter up to 3 numbers separated by commas: ");
        String[] genreChoices = scanner.nextLine().split(",");
        return Arrays.stream(genreChoices)
                     .map(String::trim)
                     .map(Integer::parseInt)
                     .collect(Collectors.toList());
    }

    private static int askPreferredTime(Scanner scanner) {
        System.out.println("\n## What is your preferred time to watch movies? (morning, afternoon, evening)");
        System.out.println("Choose an option:");
        System.out.println("1. Morning");
        System.out.println("2. Afternoon");
        System.out.println("3. Evening");
        System.out.print("Enter the corresponding number: ");
        int timeChoice = scanner.nextInt();
        scanner.nextLine();
        return timeChoice;
    }

    private static int askPreferredDevice(Scanner scanner) {
        System.out.println("\n## What device do you usually use to watch? (TV, smartphone, PC)");
        System.out.println("Choose an option:");
        System.out.println("1. TV");
        System.out.println("2. Smartphone");
        System.out.println("3. PC");
        System.out.print("Enter the corresponding number: ");
        int deviceChoice = scanner.nextInt();
        scanner.nextLine();
        return deviceChoice;
    }

    private static String askFavoriteActorsDirectors(Scanner scanner) {
        System.out.println("\n## Who are your favorite actors, directors, or franchises?");
        return scanner.nextLine();
    }

    private static int askNewOrClassic(Scanner scanner) {
        System.out.println("\n## Do you prefer new or classic movies?");
        System.out.println("Choose an option:");
        System.out.println("1. New");
        System.out.println("2. Classic");
        System.out.print("Enter the corresponding number: ");
        int preferenceChoice = scanner.nextInt();
        scanner.nextLine();
        return preferenceChoice;
    }

    private static String askGenresToAvoid(Scanner scanner) {
        System.out.println("\n## Are there any genres or movies you definitely don't want to see?");
        System.out.println("Enter the name of the genre or movie (or 'None' if there are none):");
        return scanner.nextLine();
    }

    private static String askDemographics(Scanner scanner) {
        System.out.println("\n## What is your age group and gender? (e.g., 25 years old, Male)");
        return scanner.nextLine();
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
        return genreChoices.stream().anyMatch(choice -> movie[1].contains(getGenreName(choice)));
    }

    private static String getGenreName(int choice) {
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
            case 17: return "Mystery";
            case 18: return "War";
            case 19: return "Family";
            case 20: return "Sport";
            case 21: return "Fantasy Adventure";
            case 22: return "Superhero";
            case 23: return "Psychological Thriller";
            case 24: return "Epic";
            case 25: return "Noir";
            default: return "";
        }
    }

    private static boolean filterByTime(String[] movie, int timeChoice) {
        String preferredTime = timeChoice == 1 ? "Morning" : timeChoice == 2 ? "Afternoon" : "Evening";
        return movie[2].equalsIgnoreCase(preferredTime);
    }

    private static boolean filterByDevice(String[] movie, int deviceChoice) {
        String preferredDevice = deviceChoice == 1 ? "TV" : deviceChoice == 2 ? "Smartphone" : "PC";
        return movie[3].equalsIgnoreCase(preferredDevice);
    }

    private static boolean filterByAvoid(String[] movie, String avoid) {
        if (avoid.equalsIgnoreCase("None")) {
            return true;
        }
        return !movie[1].equalsIgnoreCase(avoid);
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

    private static void saveTrendingNow(List<String[]> movies, String filePath) {
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