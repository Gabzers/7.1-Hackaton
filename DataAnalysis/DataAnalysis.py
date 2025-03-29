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

# Statistical analysis
def perform_statistical_analysis(data):
    numeric_data = data.select_dtypes(include=[np.number])  # Select only numeric columns
    stats = numeric_data.describe().T  # Transpose for better visualization
    stats = stats.drop(['25%', '50%', '75%'], axis=1)  # Remove quartiles
    stats['range'] = stats['max'] - stats['min']  # Add range calculation
    stats['variance'] = numeric_data.var()  # Variance
    stats['mode'] = numeric_data.mode().iloc[0]  # Add mode calculation

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

# Function to display summary statistics
def show_summary_statistics():
    stats_window = tk.Toplevel(root)
    stats_window.title("Summary Statistics")
    stats_window.geometry("800x600")

    # Format the stats DataFrame to one decimal place
    formatted_stats = stats.copy()
    formatted_stats = formatted_stats.dropna()  # Remove rows with NaN values
    formatted_stats.iloc[:, 1:] = formatted_stats.iloc[:, 1:].round(1)  # Round all numeric columns to 1 decimal place

    # Separate quantitative and qualitative data
    qualitative_data = data.select_dtypes(include=['object']).nunique().reset_index()
    qualitative_data.columns = ['Column', 'Unique Values']

    # Quantitative data table
    tk.Label(stats_window, text="Quantitative Data", font=("Arial", 14)).pack(pady=5)
    tree_quantitative = ttk.Treeview(stats_window, columns=list(formatted_stats.columns), show='headings')
    tree_quantitative.pack(fill=tk.BOTH, expand=True)

    for col in formatted_stats.columns:
        tree_quantitative.heading(col, text=col, anchor="center")  # Center the column headers
        tree_quantitative.column(col, width=100, anchor="center")  # Center the column values

    for _, row in formatted_stats.iterrows():
        tree_quantitative.insert("", tk.END, values=list(row))

    # Qualitative data table
    tk.Label(stats_window, text="Qualitative Data", font=("Arial", 14)).pack(pady=5)
    tree_qualitative = ttk.Treeview(stats_window, columns=list(qualitative_data.columns), show='headings')
    tree_qualitative.pack(fill=tk.BOTH, expand=True)

    for col in qualitative_data.columns:
        tree_qualitative.heading(col, text=col, anchor="center")  # Center the column headers
        tree_qualitative.column(col, width=150, anchor="center")  # Center the column values

    for _, row in qualitative_data.iterrows():
        tree_qualitative.insert("", tk.END, values=list(row))

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

# Function to display genre statistics
def show_genre_statistics():
    explanation = (
        "This analysis groups movies by genres and calculates statistical metrics "
        "such as mean and standard deviation of ratings for each genre."
    )
    show_explanation("Genre Statistics", explanation)

    genre_stats = analyze_genre_statistics(data)

    # Remover a coluna "Median"
    genre_stats = genre_stats.drop(columns=['median'])

    # Remover linhas com valores NaN
    genre_stats = genre_stats.dropna()

    # Arredondar os valores para no máximo uma casa decimal
    genre_stats.iloc[:, 1:] = genre_stats.iloc[:, 1:].round(1)

    # Ordenar os gêneros pela média (mean) em ordem decrescente
    genre_stats = genre_stats.sort_values(by='mean', ascending=False)

    stats_window = tk.Toplevel(root)
    stats_window.title("Genre Statistics")
    stats_window.geometry("800x400")

    tree = ttk.Treeview(stats_window, columns=list(genre_stats.columns), show='headings')
    tree.pack(fill=tk.BOTH, expand=True)

    for col in genre_stats.columns:
        tree.heading(col, text=col, anchor="center")  # Center the column headers
        tree.column(col, width=100, anchor="center")  # Center the column values

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
        "This chart shows the popularity of the top 10 independent genres based on the total number of votes. "
        "It helps to identify which genres are most popular among viewers."
    )
    show_explanation("Popularity by Genre", explanation)

    # Filtrar apenas gêneros independentes (sem combinações)
    independent_genres = data['genres'].str.split(',').explode().str.strip().value_counts().reset_index()
    independent_genres.columns = ['Genre', 'Count']

    # Corrigir duplicatas de gêneros (ex.: "Drama" e "drama")
    independent_genres['Genre'] = independent_genres['Genre'].str.capitalize()
    independent_genres = independent_genres.groupby('Genre', as_index=False).sum()

    # Calcular a popularidade por gênero independente
    genre_popularity = (
        data.assign(genres=data['genres'].str.split(','))
        .explode('genres')
        .assign(genres=lambda df: df['genres'].str.strip().str.capitalize())  # Corrigir duplicatas
        .groupby('genres')['numVotes']
        .sum()
        .reset_index()
        .sort_values(by='numVotes', ascending=False)
    )

    # Filtrar os 10 gêneros mais populares
    top_genres = genre_popularity[genre_popularity['genres'].isin(independent_genres['Genre'])].head(10)

    # Criar o gráfico
    fig, ax = plt.subplots(figsize=(12, 8))
    sns.barplot(x='numVotes', y='genres', data=top_genres, palette='viridis', ax=ax)
    ax.set_title('Popularity by Genre (Top 10 Independent Genres)')
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

    # Ajustar os intervalos dos anos no eixo X para 10 em 10
    ax.set_xticks(range(int(popularity_trend['releaseYear'].min()), int(popularity_trend['releaseYear'].max()) + 1, 10))

    plot_window = tk.Toplevel(root)
    plot_window.title("Popularity Trend Over Time")
    canvas = FigureCanvasTkAgg(fig, master=plot_window)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

# Function to display top genres by ratings
def show_top_genres_by_ratings():
    explanation = (
        "This chart shows the genres with the highest average ratings, "
        "considering only individual genres (no combinations). "
        "It helps to identify which genres are perceived as having the highest quality by viewers."
    )
    show_explanation("Top Genres by Ratings", explanation)

    # Filtrar apenas gêneros individuais (sem combinações)
    individual_genres = data['genres'].str.split(',').explode().str.strip().value_counts().reset_index()
    individual_genres.columns = ['Genre', 'Count']

    # Corrigir duplicatas de gêneros (ex.: "Drama" e "drama")
    individual_genres['Genre'] = individual_genres['Genre'].str.capitalize()
    individual_genres = individual_genres.groupby('Genre', as_index=False).sum()

    # Calcular a média de ratings por gênero individual
    genre_ratings = (
        data.assign(genres=data['genres'].str.split(','))
        .explode('genres')
        .assign(genres=lambda df: df['genres'].str.strip().str.capitalize())  # Corrigir duplicatas
        .groupby('genres')['averageRating']
        .mean()
        .reset_index()
        .sort_values(by='averageRating', ascending=False)
    )

    # Filtrar apenas gêneros individuais
    genre_ratings = genre_ratings[genre_ratings['genres'].isin(individual_genres['Genre'])].head(10)

    # Criar o gráfico
    fig, ax = plt.subplots(figsize=(12, 8))
    sns.barplot(x='averageRating', y='genres', data=genre_ratings, palette='coolwarm', ax=ax)
    ax.set_title('Top Genres by Ratings (Individual Genres)')
    ax.set_xlabel('Average Ratings')
    ax.set_ylabel('Genre')

    plot_window = tk.Toplevel(root)
    plot_window.title("Top Genres by Ratings")
    canvas = FigureCanvasTkAgg(fig, master=plot_window)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

# Function to display the relationship between averageRating and numVotes
def show_rating_votes_relationship():
    explanation = (
        "This bar chart shows the relationship between the average rating of movies "
        "and the number of votes they received. It helps to identify whether movies "
        "with more votes tend to have higher ratings or if popular movies often have low ratings."
    )
    show_explanation("Relationship Between Ratings and Votes", explanation)

    # Agrupar os dados por faixas de votos e calcular a média de ratings
    data['vote_bins'] = pd.cut(data['numVotes'], bins=[0, 100, 1000, 10000, 100000, 1000000], 
                               labels=['0-100', '101-1k', '1k-10k', '10k-100k', '100k+'])
    grouped_data = data.groupby('vote_bins')['averageRating'].mean().reset_index()

    # Criar o gráfico de barras
    fig, ax = plt.subplots(figsize=(10, 6))
    sns.barplot(x='vote_bins', y='averageRating', data=grouped_data, palette='coolwarm', ax=ax)
    ax.set_title('Relationship Between Ratings and Votes')
    ax.set_xlabel('Number of Votes (Bins)')
    ax.set_ylabel('Average Rating')

    plot_window = tk.Toplevel(root)
    plot_window.title("Ratings vs Votes")
    canvas = FigureCanvasTkAgg(fig, master=plot_window)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

# Function to display the top 10 genres with the most movies
def show_top_genres_by_movie_count():
    explanation = (
        "This bar chart shows the top 10 genres with the most movies. "
        "Only isolated genres (no combinations) are considered."
    )
    show_explanation("Top Genres by Movie Count", explanation)

    # Filtrar apenas gêneros isolados (sem combinações)
    isolated_genres = data['genres'].str.split(',').explode().str.strip().value_counts().reset_index()
    isolated_genres.columns = ['Genre', 'Movie Count']

    # Corrigir duplicatas de gêneros (ex.: "Drama" e "drama")
    isolated_genres['Genre'] = isolated_genres['Genre'].str.capitalize()
    isolated_genres = isolated_genres.groupby('Genre', as_index=False).sum()

    # Selecionar os 10 gêneros com mais filmes
    top_genres = isolated_genres.sort_values(by='Movie Count', ascending=False).head(10)

    # Criar o gráfico de barras
    fig, ax = plt.subplots(figsize=(12, 8))
    sns.barplot(x='Movie Count', y='Genre', data=top_genres, palette='viridis', ax=ax)
    ax.set_title('Top 10 Genres by Movie Count')
    ax.set_xlabel('Number of Movies')
    ax.set_ylabel('Genre')

    plot_window = tk.Toplevel(root)
    plot_window.title("Top Genres by Movie Count")
    canvas = FigureCanvasTkAgg(fig, master=plot_window)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

    # Fechar o gráfico do Matplotlib para evitar conflitos
    plt.close(fig)

# Function to display the 30 highest-rated and 30 lowest-rated movies
def show_extreme_ratings():
    explanation = (
        "This bar chart shows the 30 movies with the highest ratings and the 30 movies with the lowest ratings. "
        "The movies are separated into two sections: highest-rated and lowest-rated."
    )
    show_explanation("Extreme Ratings", explanation)

    # Selecionar os 30 filmes com as notas mais altas
    highest_rated = data.nlargest(30, 'averageRating')[['title', 'averageRating']]
    highest_rated['Category'] = 'Highest Rated'

    # Selecionar os 30 filmes com as notas mais baixas
    lowest_rated = data.nsmallest(30, 'averageRating')[['title', 'averageRating']]
    lowest_rated['Category'] = 'Lowest Rated'

    # Combinar os dois conjuntos de dados
    extreme_ratings = pd.concat([highest_rated, lowest_rated])

    # Criar o gráfico de barras
    fig, ax = plt.subplots(figsize=(12, 16))  # Aumentar a altura para acomodar mais filmes
    sns.barplot(
        x='averageRating', 
        y='title', 
        hue='Category', 
        data=extreme_ratings, 
        dodge=False, 
        palette={'Highest Rated': 'green', 'Lowest Rated': 'red'}, 
        ax=ax
    )
    ax.set_title('Movies with Extreme Ratings (Highest and Lowest)')
    ax.set_xlabel('Average Rating')
    ax.set_ylabel('Movie Title')
    ax.legend(title='Category')

    # Ajustar a escala do eixo X para 1 em 1
    ax.set_xticks(range(1, 11))

    plot_window = tk.Toplevel(root)
    plot_window.title("Extreme Ratings")
    canvas = FigureCanvasTkAgg(fig, master=plot_window)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

    # Fechar o gráfico do Matplotlib para evitar conflitos
    plt.close(fig)

# Function to display 100 movies with many votes but relatively low ratings in a table
def show_high_votes_low_ratings():
    explanation = (
        "This table shows 100 movies that have a high number of votes but relatively low ratings. "
        "It helps to identify popular movies that are not well-rated."
    )
    show_explanation("High Votes, Low Ratings", explanation)

    # Filtrar filmes com muitos votos e ratings baixos
    high_votes_low_ratings = data[(data['numVotes'] > data['numVotes'].quantile(0.9)) & 
                                  (data['averageRating'] < data['averageRating'].median())]
    top_100 = high_votes_low_ratings.nsmallest(100, 'averageRating')[['title', 'numVotes', 'averageRating']]

    # Criar uma nova janela para exibir a tabela
    table_window = tk.Toplevel(root)
    table_window.title("High Votes, Low Ratings")
    table_window.geometry("800x600")

    # Criar a tabela interativa
    tree = ttk.Treeview(table_window, columns=list(top_100.columns), show='headings')
    tree.pack(fill=tk.BOTH, expand=True)

    # Configurar os cabeçalhos das colunas
    for col in top_100.columns:
        tree.heading(col, text=col, anchor="center")
        tree.column(col, anchor="center", width=200)

    # Inserir os dados na tabela
    for _, row in top_100.iterrows():
        tree.insert("", tk.END, values=list(row))

# Main Tkinter window
root = tk.Tk()
root.title("Statistical Analysis Interface")
root.geometry("400x850")  # Adjusted height to accommodate the new button

tk.Label(root, text="Statistical Analysis Dashboard", font=("Arial", 16)).pack(pady=10)

# Buttons for statistical analyses
tk.Button(root, text="Summary Statistics", command=show_summary_statistics).pack(pady=5)

# Buttons for visualizations
tk.Button(root, text="Distribution of Ratings", command=show_rating_distribution).pack(pady=5)
tk.Button(root, text="Genre Statistics", command=show_genre_statistics).pack(pady=5)
tk.Button(root, text="Distribution by Release Year", command=show_release_year_distribution).pack(pady=5)
tk.Button(root, text="Popularity by Genre", command=show_popularity_by_genre).pack(pady=5)
tk.Button(root, text="Ratings Trend Over Time", command=show_ratings_trend).pack(pady=5)
tk.Button(root, text="Popularity Trend Over Time", command=show_popularity_trend).pack(pady=5)
tk.Button(root, text="Top Genres by Ratings", command=show_top_genres_by_ratings).pack(pady=5)
tk.Button(root, text="Ratings vs Votes", command=show_rating_votes_relationship).pack(pady=5)
tk.Button(root, text="Top Genres by Movie Count", command=show_top_genres_by_movie_count).pack(pady=5)
tk.Button(root, text="Extreme Ratings", command=show_extreme_ratings).pack(pady=5)
tk.Button(root, text="High Votes, Low Ratings", command=show_high_votes_low_ratings).pack(pady=5)  # Updated button

root.mainloop()