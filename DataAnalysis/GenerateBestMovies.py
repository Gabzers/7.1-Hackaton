import pandas as pd
import numpy as np
import os

# Load dataset
def load_data(filepath):
    if not os.path.exists(filepath):
        filepath = input("File 'movies.csv' not found.\nPlease enter the correct path to the dataset: ")
        if not os.path.exists(filepath):
            raise FileNotFoundError(f"File not found: {filepath}. Please provide the correct path.")
    return pd.read_csv(filepath)

# Calculate weighted rating
def calculate_weighted_rating(data, m, C):
    data = data.copy()
    data['weightedRating'] = (
        data['numVotes'] / (data['numVotes'] + m) * data['averageRating'] +
        m / (data['numVotes'] + m) * C
    )
    return data

# Generate best movies
def generate_best_movies(data, output_filepath, top_n=100000):  # Adjusted top_n default to 10000
    # Calculate global mean and minimum votes threshold
    C = data['averageRating'].mean()
    m = data['numVotes'].quantile(0.75)
    
    # Filter movies with sufficient votes
    filtered_data = data[data['numVotes'] >= m]
    
    # Calculate weighted rating
    filtered_data = calculate_weighted_rating(filtered_data, m, C)
    
    # Sort by weighted rating and select top N movies
    best_movies = filtered_data.sort_values(by='weightedRating', ascending=False).head(top_n)
    
    # Save to CSV
    best_movies.to_csv(output_filepath, index=False)
    print(f"Best movies saved to {output_filepath}")

# Main function
def main():
    input_filepath = 'd:\\Documentos\\Programação\\7.1-Hackaton\\DataAnalysis\\movies.csv'  # Adjusted dataset path
    output_filepath = 'd:\\Documentos\\Programação\\7.1-Hackaton\\DataAnalysis\\BestMovies.csv'
    
    # Prompt user for file path if the default file is not found
    while not os.path.exists(input_filepath):
        print(f"File '{input_filepath}' not found.")
        input_filepath = input("Please enter the correct path to the dataset: ").strip()
    
    # Load data
    try:
        data = load_data(input_filepath)
    except FileNotFoundError as e:
        print(e)
        return
    
    # Generate best movies
    generate_best_movies(data, output_filepath)

if __name__ == "__main__":
    main()
