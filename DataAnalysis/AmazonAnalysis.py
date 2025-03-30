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
        raise FileNotFoundError(f"O ficheiro '{filepath}' não existe. Por favor, forneça um caminho válido.")
    try:
        data = pd.read_csv(filepath)
        print(f"Ficheiro '{filepath}' carregado com sucesso.")
        return data
    except Exception as e:
        raise Exception(f"Erro ao carregar o ficheiro '{filepath}': {e}")

# Load dataset
filepath = 'DataAnalysis/AmazonSales_Final.csv'
data = load_data(filepath)

# Funções de análise
def show_total_products():
    total_products = len(data)
    messagebox.showinfo("Total Products", f"Total number of products: {total_products}")

def show_price_statistics():
    if 'cost' not in data.columns:
        messagebox.showerror("Error", "The column 'cost' does not exist in the dataset.")
        return
    try:
        min_cost = data['cost'].min()
        max_cost = data['cost'].max()
        mean_cost = round(data['cost'].mean(), 2)  # Formatado para duas casas decimais
        median_cost = round(data['cost'].median(), 2)  # Formatado para duas casas decimais
        messagebox.showinfo(
            "Price Statistics",
            f"Minimum Cost: {min_cost}\nMaximum Cost: {max_cost}\nMean Cost: {mean_cost}\nMedian Cost: {median_cost}"
        )
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while calculating price statistics: {e}")

def show_category_representation():
    if 'category' not in data.columns:
        messagebox.showerror("Error", "The column 'category' does not exist in the dataset.")
        return
    try:
        most_represented = data['category'].value_counts().idxmax()
        least_represented = data['category'].value_counts().idxmin()
        messagebox.showinfo(
            "Category Representation",
            f"Most Represented Category: {most_represented}\nLeast Represented Category: {least_represented}"
        )
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while analyzing categories: {e}")

def show_ratings_chart():
    if 'noRatings' not in data.columns or 'product_name' not in data.columns:
        messagebox.showerror("Error", "The required columns ('noRatings' or 'product_name') do not exist in the dataset.")
        return
    try:
        # Converter a coluna 'noRatings' para numérico, ignorando erros
        data['noRatings'] = pd.to_numeric(data['noRatings'], errors='coerce')
        data.dropna(subset=['noRatings'], inplace=True)  # Remover linhas com valores inválidos em 'noRatings'

        # Selecionar os 15 produtos com mais avaliações
        top15 = data.nlargest(15, 'noRatings')[['product_name', 'noRatings']]

        # Criar o gráfico
        plt.figure(figsize=(12, 8))  # Aumentar o tamanho do gráfico
        sns.barplot(x='noRatings', y='product_name', data=top15, palette='viridis')
        plt.xlabel('Number of Ratings', fontsize=12)
        plt.ylabel('Product Name', fontsize=8)
        plt.title('Top 15 Products by Number of Ratings', fontsize=14)
        plt.xticks(fontsize=10)
        plt.yticks(fontsize=5)  # Diminuir o tamanho da fonte dos nomes dos produtos
        plt.tight_layout()  # Ajustar automaticamente para evitar cortes
        plt.show()
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the chart: {e}")

def show_category_rank_distribution():
    if 'categoryRank' not in data.columns or 'category' not in data.columns:
        messagebox.showerror("Error", "The required columns ('categoryRank' or 'category') do not exist in the dataset.")
        return
    try:
        # Converter a coluna 'categoryRank' para numérico, ignorando erros
        data['categoryRank'] = pd.to_numeric(data['categoryRank'], errors='coerce')
        data.dropna(subset=['categoryRank'], inplace=True)  # Remover linhas com valores inválidos em 'categoryRank'

        # Criar o gráfico
        plt.figure(figsize=(12, 8))
        sns.boxplot(x='category', y='categoryRank', data=data, palette='Set3')
        plt.xlabel('Category', fontsize=12)
        plt.ylabel('Category Rank', fontsize=12)
        plt.title('Distribution of Category Rank by Category', fontsize=14)
        plt.xticks(rotation=45, fontsize=10)  # Rotacionar os rótulos das categorias para melhor visualização
        plt.tight_layout()  # Ajustar automaticamente para evitar cortes
        plt.show()
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the chart: {e}")

def show_correlation():
    if 'categoryRank' not in data.columns or 'noRatings' not in data.columns:
        messagebox.showerror("Error", "The required columns ('categoryRank' or 'noRatings') do not exist in the dataset.")
        return
    try:
        # Converter as colunas para numérico, ignorando erros
        data['categoryRank'] = pd.to_numeric(data['categoryRank'], errors='coerce')
        data['noRatings'] = pd.to_numeric(data['noRatings'], errors='coerce')
        data.dropna(subset=['categoryRank', 'noRatings'], inplace=True)  # Remover linhas inválidas

        # Calcular a correlação
        correlation = data['categoryRank'].corr(data['noRatings'])

        # Criar o gráfico de dispersão
        plt.figure(figsize=(10, 6))
        sns.scatterplot(x='categoryRank', y='noRatings', data=data, alpha=0.6, color='blue')
        plt.xlabel('Category Rank', fontsize=12)
        plt.ylabel('Number of Ratings', fontsize=12)
        plt.title(f'Correlation between Category Rank and Number of Ratings\nCorrelation: {correlation:.2f}', fontsize=14)
        plt.tight_layout()
        plt.show()
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the chart: {e}")

# Interface gráfica
root = tk.Tk()
root.title("Amazon Sales Analysis")

# Botões
btn_total_products = ttk.Button(root, text="Show Total Products", command=show_total_products)
btn_total_products.pack(pady=10)

btn_price_statistics = ttk.Button(root, text="Show Price Statistics", command=show_price_statistics)
btn_price_statistics.pack(pady=10)

btn_category_representation = ttk.Button(root, text="Show Category Representation", command=show_category_representation)
btn_category_representation.pack(pady=10)

btn_ratings_chart = ttk.Button(root, text="Show Ratings Chart", command=show_ratings_chart)
btn_ratings_chart.pack(pady=10)

btn_category_rank_distribution = ttk.Button(root, text="Show Category Rank Distribution", command=show_category_rank_distribution)
btn_category_rank_distribution.pack(pady=10)

btn_correlation = ttk.Button(root, text="Show Correlation - categoryRank & noRatings", command=show_correlation)
btn_correlation.pack(pady=10)

# Iniciar a interface gráfica
root.mainloop()