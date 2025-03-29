import os
import pandas as pd

def create_genre_csvs(input_filepath, output_folder):
    # Load the BestMovies.csv file
    if not os.path.exists(input_filepath):
        raise FileNotFoundError(f"Input file not found: {input_filepath}")
    
    data = pd.read_csv(input_filepath)
    
    # Ensure the output folder exists
    os.makedirs(output_folder, exist_ok=True)
    
    # Process each genre individually
    all_genres = set()
    data['genres'] = data['genres'].fillna('')  # Handle NaN genres
    for genres in data['genres']:
        all_genres.update(genres.split(', '))
    
    for genre in all_genres:
        if genre:  # Skip empty genres
            genre_data = data[data['genres'].str.contains(rf'\b{genre}\b', na=False)]
            genre_filepath = os.path.join(output_folder, f"{genre}.csv")
            genre_data.to_csv(genre_filepath, index=False)
            print(f"CSV for genre '{genre}' saved to {genre_filepath}")

def main():
    input_filepath = os.path.join('DataAnalysis', 'BestMovies.csv')  # Path to BestMovies.csv
    output_folder = os.path.join('DataAnalysis', 'MovieGenresCSV')  # Folder for genre CSVs
    
    try:
        create_genre_csvs(input_filepath, output_folder)
    except FileNotFoundError as e:
        print(e)

if __name__ == "__main__":
    main()
