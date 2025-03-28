import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import os  # Import os for file existence check

# Load dataset
def load_data(filepath):
    if not os.path.exists(filepath):
        raise FileNotFoundError(f"The file '{filepath}' does not exist. Please provide a valid path.")
    return pd.read_csv(filepath)

# EDA: Distribution of ratings
def analyze_rating_distribution(data):
    plt.figure(figsize=(10, 6))
    sns.histplot(data['averageRating'], kde=True, bins=30, color='blue')
    plt.title('Distribution of Ratings')
    plt.xlabel('Average Rating')
    plt.ylabel('Frequency')
    plt.show()

# EDA: Group by genres and calculate statistics
def analyze_genre_statistics(data):
    genre_stats = data.groupby('genres')['averageRating'].agg(['mean', 'median', 'std']).reset_index()
    print(genre_stats)
    return genre_stats

# EDA: Distribution of votes
def analyze_vote_distribution(data):
    plt.figure(figsize=(10, 6))
    sns.histplot(data['numVotes'], kde=True, bins=30, color='green')
    plt.title('Distribution of Votes')
    plt.xlabel('Number of Votes')
    plt.ylabel('Frequency')
    plt.show()

# EDA: Correlation analysis
def analyze_correlations(data):
    numeric_data = data.select_dtypes(include=[np.number])  # Select only numeric columns
    correlation_matrix = numeric_data.corr()
    sns.heatmap(correlation_matrix, annot=True, cmap='coolwarm')
    plt.title('Correlation Matrix')
    plt.show()

# Statistical analysis
def perform_statistical_analysis(data):
    numeric_data = data.select_dtypes(include=[np.number])  # Select only numeric columns
    stats = numeric_data.describe().T  # Transpose for better readability
    stats['range'] = stats['max'] - stats['min']  # Add range calculation
    print("Statistical Analysis:")
    print(stats)
    return stats

# Weighted rating formula
def calculate_weighted_rating(data, m, C):
    data = data.copy()  # Create a copy to avoid modifying the original DataFrame slice
    data.loc[:, 'weightedRating'] = (
        data['numVotes'] / (data['numVotes'] + m) * data['averageRating'] +
        m / (data['numVotes'] + m) * C
    )
    return data

# Rankings by genre
def generate_genre_rankings(data):
    rankings = data.sort_values(by='weightedRating', ascending=False).groupby('genres').head(10)
    print(rankings)
    return rankings

# Visualization: Bar plots and boxplots
def visualize_genre_distributions(data):
    plt.figure(figsize=(12, 8))
    sns.boxplot(x='genres', y='averageRating', data=data)
    plt.xticks(rotation=90)
    plt.title('Rating Distribution by Genre')
    plt.show()

# Visualization: Scatterplot
def visualize_scatterplot(data, x_col, y_col):
    plt.figure(figsize=(10, 6))
    sns.scatterplot(x=data[x_col], y=data[y_col], alpha=0.7)
    plt.title(f'{y_col} vs {x_col}')
    plt.xlabel(x_col)
    plt.ylabel(y_col)
    plt.show()

# Validation: Apply filters
def filter_movies(data, min_votes):
    filtered_data = data[data['numVotes'] >= min_votes]
    print(f"Filtered dataset contains {len(filtered_data)} movies.")
    return filtered_data

# Sensitivity testing for m
def test_sensitivity(data, m_values, C):
    for m in m_values:
        data = calculate_weighted_rating(data, m, C)
        print(f"Top movies for m={m}:")
        print(data.nlargest(5, 'weightedRating')[['title', 'weightedRating']])

# Main function to execute the analysis
def main():
    filepath = 'DataAnalysis/movies.csv'  # Adjusted dataset path to match folder structure
    data = load_data(filepath)
    
    # Perform EDA
    analyze_rating_distribution(data)
    genre_stats = analyze_genre_statistics(data)
    analyze_vote_distribution(data)
    analyze_correlations(data)
    
    # Perform statistical analysis
    perform_statistical_analysis(data)
    
    # Calculate metrics and rankings
    C = data['averageRating'].mean()
    m = data['numVotes'].quantile(0.75)
    data = calculate_weighted_rating(data, m, C)
    rankings = generate_genre_rankings(data)
    
    # Visualizations
    visualize_genre_distributions(data)
    visualize_scatterplot(data, 'numVotes', 'averageRating')
    
    # Validation
    filtered_data = filter_movies(data, min_votes=50)
    test_sensitivity(filtered_data, m_values=[10, 50, 100], C=C)

if __name__ == "__main__":
    main()
