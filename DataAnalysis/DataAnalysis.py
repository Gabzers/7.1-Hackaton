import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import os  # Import os for file existence check
import tkinter as tk
from tkinter import ttk
from tkinter import messagebox
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

# Load dataset
def load_data(filepath):
    if not os.path.exists(filepath):
        raise FileNotFoundError(f"The file '{filepath}' does not exist. Please provide a valid path.")
    data = pd.read_csv(filepath)
    # Filter movies up to the year 2024
    data = data[data['releaseYear'] <= 2024]
    return data

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
    stats = numeric_data.describe().T  # Transpose for better visualization
    stats['range'] = stats['max'] - stats['min']  # Add range calculation
    stats['variance'] = numeric_data.var()  # Variance
    stats['cv'] = stats['std'] / stats['mean']  # Coefficient of variation
    stats['skewness'] = numeric_data.skew()  # Skewness
    stats['kurtosis'] = numeric_data.kurt()  # Kurtosis

    # Add the "Name" column at the beginning
    stats.insert(0, 'Name', ['averageRating', 'numVotes', 'releaseYear'])
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

# Load dataset
filepath = 'DataAnalysis/movies.csv'
data = load_data(filepath)

# Perform statistical analysis
stats = perform_statistical_analysis(data)
correlation_matrix = data.select_dtypes(include=[np.number]).corr()

# Function to display summary statistics
def show_summary_statistics():
    stats_window = tk.Toplevel(root)
    stats_window.title("Summary Statistics")
    stats_window.geometry("800x400")

    tree = ttk.Treeview(stats_window, columns=list(stats.columns), show='headings')
    tree.pack(fill=tk.BOTH, expand=True)

    for col in stats.columns:
        tree.heading(col, text=col)
        tree.column(col, width=100)

    for _, row in stats.iterrows():
        tree.insert("", tk.END, values=list(row))

# Function to display correlation matrix
def show_correlation_matrix():
    corr_window = tk.Toplevel(root)
    corr_window.title("Correlation Matrix")
    corr_window.geometry("600x600")

    fig, ax = plt.subplots(figsize=(8, 6))
    sns.heatmap(correlation_matrix, annot=True, cmap='coolwarm', ax=ax)
    ax.set_title("Correlation Matrix")

    canvas = FigureCanvasTkAgg(fig, master=corr_window)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

# Function to display a brief explanation
def show_explanation(title, explanation):
    explanation_window = tk.Toplevel(root)
    explanation_window.title(title)
    explanation_window.geometry("400x200")
    tk.Label(explanation_window, text=explanation, wraplength=350, justify="left").pack(pady=20)

# Function to display distribution of ratings
def show_rating_distribution():
    explanation = (
        "This analysis shows the distribution of average ratings for movies. "
        "It helps to understand how ratings are spread across the dataset."
    )
    show_explanation("Distribution of Ratings", explanation)

    fig, ax = plt.subplots(figsize=(10, 6))
    sns.histplot(data['averageRating'], kde=True, bins=30, color='blue', ax=ax)
    ax.set_title('Distribution of Ratings')
    ax.set_xlabel('Average Rating')
    ax.set_ylabel('Frequency')

    plot_window = tk.Toplevel(root)
    plot_window.title("Distribution of Ratings")
    canvas = FigureCanvasTkAgg(fig, master=plot_window)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

# Function to display distribution of votes
def show_vote_distribution():
    explanation = (
        "This analysis shows the distribution of the number of votes for movies. "
        "It helps to understand how popular movies are based on the number of votes."
    )
    show_explanation("Distribution of Votes", explanation)

    fig, ax = plt.subplots(figsize=(10, 6))
    sns.histplot(data['numVotes'], kde=False, bins=50, color='green', ax=ax)
    ax.set_xscale('log')
    ax.set_title('Distribution of Votes (Log Scale)')
    ax.set_xlabel('Number of Votes (Log Scale)')
    ax.set_ylabel('Frequency')

    plot_window = tk.Toplevel(root)
    plot_window.title("Distribution of Votes")
    canvas = FigureCanvasTkAgg(fig, master=plot_window)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

# Function to display genre statistics
def show_genre_statistics():
    explanation = (
        "This analysis groups movies by genres and calculates statistical metrics "
        "such as mean, median, and standard deviation of ratings for each genre."
    )
    show_explanation("Genre Statistics", explanation)

    genre_stats = analyze_genre_statistics(data)
    stats_window = tk.Toplevel(root)
    stats_window.title("Genre Statistics")
    stats_window.geometry("800x400")

    tree = ttk.Treeview(stats_window, columns=list(genre_stats.columns), show='headings')
    tree.pack(fill=tk.BOTH, expand=True)

    for col in genre_stats.columns:
        tree.heading(col, text=col)
        tree.column(col, width=100)

    for _, row in genre_stats.iterrows():
        tree.insert("", tk.END, values=list(row))

# Function to display distribution by release year
def show_release_year_distribution():
    explanation = (
        "This chart shows the distribution of the number of movies released per year. "
        "It helps to identify trends in movie production over time."
    )
    show_explanation("Distribution by Release Year", explanation)

    fig, ax = plt.subplots(figsize=(10, 6))
    sns.histplot(data['releaseYear'], bins=50, color='purple', ax=ax)
    ax.set_title('Distribution by Release Year')
    ax.set_xlabel('Release Year')
    ax.set_ylabel('Number of Movies')

    plot_window = tk.Toplevel(root)
    plot_window.title("Distribution by Release Year")
    canvas = FigureCanvasTkAgg(fig, master=plot_window)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

# Function to display popularity by genre
def show_popularity_by_genre():
    explanation = (
        "This chart shows the popularity of genres based on the total number of votes. "
        "It helps to identify which genres are most popular among viewers."
    )
    show_explanation("Popularity by Genre", explanation)

    genre_popularity = data.groupby('genres')['numVotes'].sum().reset_index().sort_values(by='numVotes', ascending=False)
    fig, ax = plt.subplots(figsize=(12, 8))
    sns.barplot(x='numVotes', y='genres', data=genre_popularity, palette='viridis', ax=ax)
    ax.set_title('Popularity by Genre')
    ax.set_xlabel('Total Number of Votes')
    ax.set_ylabel('Genre')

    plot_window = tk.Toplevel(root)
    plot_window.title("Popularity by Genre")
    canvas = FigureCanvasTkAgg(fig, master=plot_window)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

# Function to display ratings trend over time
def show_ratings_trend():
    explanation = (
        "This chart shows the trend of average ratings over time. "
        "It helps to understand whether recent movies are receiving better or worse ratings."
    )
    show_explanation("Ratings Trend Over Time", explanation)

    ratings_trend = data.groupby('releaseYear')['averageRating'].mean().reset_index()
    fig, ax = plt.subplots(figsize=(10, 6))
    sns.lineplot(x='releaseYear', y='averageRating', data=ratings_trend, marker='o', ax=ax)
    ax.set_title('Ratings Trend Over Time')
    ax.set_xlabel('Release Year')
    ax.set_ylabel('Average Ratings')

    # Ajustar os intervalos dos eixos
    ax.set_xticks(range(int(ratings_trend['releaseYear'].min()), int(ratings_trend['releaseYear'].max()) + 1, 5))  # De 5 em 5 anos
    ax.set_yticks(range(1, 11))  # De 1 em 1 para os ratings

    plot_window = tk.Toplevel(root)
    plot_window.title("Ratings Trend Over Time")
    canvas = FigureCanvasTkAgg(fig, master=plot_window)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

# Function to display popularity trend over time
def show_popularity_trend():
    explanation = (
        "This chart shows the trend of movie popularity over time, "
        "based on the total number of votes per year."
    )
    show_explanation("Popularity Trend Over Time", explanation)

    popularity_trend = data.groupby('releaseYear')['numVotes'].sum().reset_index()
    fig, ax = plt.subplots(figsize=(10, 6))
    sns.lineplot(x='releaseYear', y='numVotes', data=popularity_trend, marker='o', ax=ax)
    ax.set_title('Popularity Trend Over Time')
    ax.set_xlabel('Release Year')
    ax.set_ylabel('Total Number of Votes')

    plot_window = tk.Toplevel(root)
    plot_window.title("Popularity Trend Over Time")
    canvas = FigureCanvasTkAgg(fig, master=plot_window)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

# Function to display top genres by ratings
def show_top_genres_by_ratings():
    explanation = (
        "This chart shows the genres with the highest average ratings. "
        "It helps to identify which genres are perceived as having the highest quality by viewers."
    )
    show_explanation("Top Genres by Ratings", explanation)

    top_genres = data.groupby('genres')['averageRating'].mean().reset_index().sort_values(by='averageRating', ascending=False).head(10)
    fig, ax = plt.subplots(figsize=(12, 8))
    sns.barplot(x='averageRating', y='genres', data=top_genres, palette='coolwarm', ax=ax)
    ax.set_title('Top Genres by Ratings')
    ax.set_xlabel('Average Ratings')
    ax.set_ylabel('Genre')

    plot_window = tk.Toplevel(root)
    plot_window.title("Top Genres by Ratings")
    canvas = FigureCanvasTkAgg(fig, master=plot_window)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

# Main Tkinter window
root = tk.Tk()
root.title("Statistical Analysis Interface")
root.geometry("400x700")

tk.Label(root, text="Statistical Analysis Dashboard", font=("Arial", 16)).pack(pady=10)

# Buttons for statistical analyses
tk.Button(root, text="Summary Statistics", command=show_summary_statistics).pack(pady=5)
tk.Button(root, text="Correlation Matrix", command=show_correlation_matrix).pack(pady=5)

# Buttons for visualizations
tk.Button(root, text="Distribution of Ratings", command=show_rating_distribution).pack(pady=5)
tk.Button(root, text="Distribution of Votes", command=show_vote_distribution).pack(pady=5)
tk.Button(root, text="Genre Statistics", command=show_genre_statistics).pack(pady=5)
tk.Button(root, text="Distribution by Release Year", command=show_release_year_distribution).pack(pady=5)
tk.Button(root, text="Popularity by Genre", command=show_popularity_by_genre).pack(pady=5)
tk.Button(root, text="Ratings Trend Over Time", command=show_ratings_trend).pack(pady=5)
tk.Button(root, text="Popularity Trend Over Time", command=show_popularity_trend).pack(pady=5)
tk.Button(root, text="Top Genres by Ratings", command=show_top_genres_by_ratings).pack(pady=5)

root.mainloop()
