package com.hackaton.website.Service;

import com.hackaton.website.Entity.Movie;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);
    private final ResourceLoader resourceLoader;

    public MovieService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<Movie> readMoviesFromCSV() {
        List<Movie> movies = new ArrayList<>();
        Resource resource = resourceLoader.getResource("classpath:csv/MovieGenresCSV");
        File directory;
        try {
            directory = resource.getFile();
        } catch (IOException e) {
            logger.error("Error accessing directory: csv/MovieGenresCSV", e);
            return movies;
        }

        if (directory.exists() && directory.isDirectory()) {
            File[] csvFiles = directory.listFiles((dir, name) -> name.endsWith(".csv"));
            if (csvFiles != null) {
                logger.info("Found {} CSV files in directory: {}", csvFiles.length, directory.getAbsolutePath());
                for (File csvFile : csvFiles) {
                    logger.info("Reading file: {}", csvFile.getName());
                    movies.addAll(readMoviesFromSingleCSV(csvFile));
                }
            } else {
                logger.warn("No CSV files found in directory: {}", directory.getAbsolutePath());
            }
        } else {
            logger.error("Directory does not exist or is not a directory: {}", directory.getAbsolutePath());
        }
        return movies;
    }

    private List<Movie> readMoviesFromSingleCSV(File csvFile) {
        List<Movie> movies = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> lines = reader.readAll();
            logger.info("File {} contains {} lines", csvFile.getName(), lines.size());
            for (String[] line : lines) {
                if (line.length >= 8) {
                    try {
                        Movie movie = new Movie(
                            Long.parseLong(line[0].replace("tt", "")), // id (convert to Long by removing "tt")
                            line[1],                                   // title
                            line[2],                                   // type
                            line[3],                                   // genres
                            Double.parseDouble(line[4]),               // averageRating
                            (int) Double.parseDouble(line[5]),         // numVotes (convert to Integer)
                            (int) Double.parseDouble(line[6]),         // releaseYear (convert to Integer)
                            Double.parseDouble(line[7])                // weightedRating
                        );
                        movies.add(movie);
                    } catch (NumberFormatException e) {
                        logger.error("Error parsing line in file {}: {}", csvFile.getName(), String.join(",", line), e);
                    }
                } else {
                    logger.warn("Skipping malformed line in file {}: {}", csvFile.getName(), String.join(",", line));
                }
            }
        } catch (IOException | CsvException e) {
            logger.error("Error reading file: {}", csvFile.getName(), e);
        }
        logger.info("Parsed {} movies from file {}", movies.size(), csvFile.getName());
        return movies;
    }
}
