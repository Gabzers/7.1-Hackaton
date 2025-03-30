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

def show_category_summary():
    if 'category' not in data.columns or 'cost' not in data.columns or 'noRatings' not in data.columns:
        messagebox.showerror("Error", "The required columns ('category', 'cost', 'noRatings') do not exist in the dataset.")
        return
    try:
        # Converter as colunas para numérico, ignorando erros
        data['cost'] = pd.to_numeric(data['cost'], errors='coerce')
        data['noRatings'] = pd.to_numeric(data['noRatings'], errors='coerce')

        # Agrupar por categoria e calcular as médias
        summary = data.groupby('category').agg(
            avg_cost=('cost', 'mean'),
            avg_noRatings=('noRatings', 'mean')
        ).reset_index()

        # Arredondar os valores para duas casas decimais
        summary['avg_cost'] = summary['avg_cost'].round(2)
        summary['avg_noRatings'] = summary['avg_noRatings'].round(2)

        # Criar uma nova janela para exibir a tabela
        summary_window = tk.Toplevel(root)
        summary_window.title("Category Summary")

        # Criar uma tabela usando Treeview
        tree = ttk.Treeview(summary_window, columns=('Category', 'Average Cost', 'Average NoRatings'), show='headings')
        tree.heading('Category', text='Category')
        tree.heading('Average Cost', text='Average Cost')
        tree.heading('Average NoRatings', text='Average NoRatings')

        # Centralizar os valores nas colunas
        tree.column('Category', anchor='center')
        tree.column('Average Cost', anchor='center')
        tree.column('Average NoRatings', anchor='center')

        # Inserir os dados na tabela
        for _, row in summary.iterrows():
            tree.insert('', tk.END, values=(row['category'], row['avg_cost'], row['avg_noRatings']))

        tree.pack(fill=tk.BOTH, expand=True)
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the summary table: {e}")

def show_low_cost_high_ratings():
    if 'cost' not in data.columns or 'noRatings' not in data.columns or 'product_name' not in data.columns:
        messagebox.showerror("Error", "The required columns ('cost', 'noRatings', 'product_name') do not exist in the dataset.")
        return
    try:
        # Converter as colunas para numérico, ignorando erros
        data['cost'] = pd.to_numeric(data['cost'], errors='coerce')
        data['noRatings'] = pd.to_numeric(data['noRatings'], errors='coerce')

        # Filtrar os produtos com baixo custo, alto número de avaliações (mínimo 10,000) e limitar a 100 produtos
        filtered_data = data[(data['noRatings'] >= 10000)].nsmallest(100, 'cost')[['product_name', 'cost', 'noRatings', 'category']]

        # Criar uma nova janela para exibir a tabela
        table_window = tk.Toplevel(root)
        table_window.title("Low Cost, High Ratings")

        # Criar uma tabela usando Treeview
        tree = ttk.Treeview(table_window, columns=('Product Name', 'Cost', 'NoRatings', 'Category'), show='headings')
        tree.heading('Product Name', text='Product Name')
        tree.heading('Cost', text='Cost')
        tree.heading('NoRatings', text='NoRatings')
        tree.heading('Category', text='Category')

        # Centralizar os valores nas colunas
        tree.column('Product Name', anchor='center')
        tree.column('Cost', anchor='center')
        tree.column('NoRatings', anchor='center')
        tree.column('Category', anchor='center')

        # Inserir os dados na tabela
        for _, row in filtered_data.iterrows():
            tree.insert('', tk.END, values=(row['product_name'], row['cost'], row['noRatings'], row['category']))

        tree.pack(fill=tk.BOTH, expand=True)
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the table: {e}")

def show_average_price_per_category():
    if 'category' not in data.columns or 'cost' not in data.columns:
        messagebox.showerror("Error", "The required columns ('category' or 'cost') do not exist in the dataset.")
        return
    try:
        # Converter a coluna 'cost' para numérico, ignorando erros
        data['cost'] = pd.to_numeric(data['cost'], errors='coerce')
        data.dropna(subset=['cost'], inplace=True)  # Remover linhas com valores inválidos em 'cost'

        # Calcular a média de preços por categoria
        avg_price_per_category = data.groupby('category')['cost'].mean().sort_values(ascending=False)

        # Criar o gráfico
        plt.figure(figsize=(12, 8))
        sns.barplot(x=avg_price_per_category.values, y=avg_price_per_category.index, palette='coolwarm')
        plt.xlabel('Average Price', fontsize=12)
        plt.ylabel('Category', fontsize=12)
        plt.title('Average Price per Category', fontsize=14)
        plt.tight_layout()  # Ajustar automaticamente para evitar cortes
        plt.show()
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the chart: {e}")

def show_price_range_distribution():
    if 'cost' not in data.columns:
        messagebox.showerror("Error", "The column 'cost' does not exist in the dataset.")
        return
    try:
        # Converter a coluna 'cost' para numérico, ignorando erros
        data['cost'] = pd.to_numeric(data['cost'], errors='coerce')
        data.dropna(subset=['cost'], inplace=True)  # Remover linhas com valores inválidos em 'cost'

        # Definir faixas de preços
        bins = [0, 50, 100, 200, 500, 1000, 5000, 10000, np.inf]
        labels = ['0-50', '51-100', '101-200', '201-500', '501-1000', '1001-5000', '5001-10000', '10000+']
        data['price_range'] = pd.cut(data['cost'], bins=bins, labels=labels, right=False)

        # Contar o número de produtos em cada faixa de preço
        price_range_counts = data['price_range'].value_counts().sort_index()

        # Criar o gráfico
        plt.figure(figsize=(12, 8))
        sns.barplot(x=price_range_counts.index, y=price_range_counts.values, palette='Blues_d')
        plt.xlabel('Price Range', fontsize=12)
        plt.ylabel('Number of Products', fontsize=12)
        plt.title('Number of Products per Price Range', fontsize=14)
        plt.tight_layout()  # Ajustar automaticamente para evitar cortes
        plt.show()
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the chart: {e}")

def show_products_by_ratings():
    if 'noRatings' not in data.columns or 'product_name' not in data.columns:
        messagebox.showerror("Error", "The required columns ('noRatings' or 'product_name') do not exist in the dataset.")
        return
    try:
        # Ordenar os produtos pelo número de avaliações em ordem decrescente
        sorted_data = data.sort_values(by='noRatings', ascending=False)[['product_name', 'noRatings', 'cost', 'category']]

        # Criar uma nova janela para exibir a tabela
        table_window = tk.Toplevel(root)
        table_window.title("Products by Number of Ratings")

        # Criar uma tabela usando Treeview
        tree = ttk.Treeview(table_window, columns=('Product Name', 'NoRatings', 'Cost', 'Category'), show='headings')
        tree.heading('Product Name', text='Product Name')
        tree.heading('NoRatings', text='Number of Ratings')
        tree.heading('Cost', text='Cost')
        tree.heading('Category', text='Category')

        # Centralizar os valores nas colunas
        tree.column('Product Name', anchor='center')
        tree.column('NoRatings', anchor='center')
        tree.column('Cost', anchor='center')
        tree.column('Category', anchor='center')

        # Inserir os dados na tabela
        for _, row in sorted_data.iterrows():
            tree.insert('', tk.END, values=(row['product_name'], row['noRatings'], row['cost'], row['category']))

        tree.pack(fill=tk.BOTH, expand=True)
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the table: {e}")

# Interface gráfica
root = tk.Tk()
root.title("Amazon Sales Analysis")

# Botões
btn_total_products = ttk.Button(root, text="Total Products", command=show_total_products)
btn_total_products.pack(pady=10)

btn_price_statistics = ttk.Button(root, text="Price Statistics", command=show_price_statistics)
btn_price_statistics.pack(pady=10)

btn_category_representation = ttk.Button(root, text="Category Representation", command=show_category_representation)
btn_category_representation.pack(pady=10)

btn_ratings_chart = ttk.Button(root, text="Ratings Chart", command=show_ratings_chart)
btn_ratings_chart.pack(pady=10)

btn_category_rank_distribution = ttk.Button(root, text="Category Rank Distribution", command=show_category_rank_distribution)
btn_category_rank_distribution.pack(pady=10)

btn_correlation = ttk.Button(root, text="Correlation - categoryRank & noRatings", command=show_correlation)
btn_correlation.pack(pady=10)

btn_category_summary = ttk.Button(root, text="Category Summary", command=show_category_summary)
btn_category_summary.pack(pady=10)

btn_low_cost_high_ratings = ttk.Button(root, text="Products with the best cost-benefit", command=show_low_cost_high_ratings)
btn_low_cost_high_ratings.pack(pady=10)

btn_avg_price_per_category = ttk.Button(root, text="Average Price per Category", command=show_average_price_per_category)
btn_avg_price_per_category.pack(pady=10)

btn_price_range_distribution = ttk.Button(root, text="Price Range Distribution", command=show_price_range_distribution)
btn_price_range_distribution.pack(pady=10)

btn_products_by_ratings = ttk.Button(root, text="Products by Number of Ratings", command=show_products_by_ratings)
btn_products_by_ratings.pack(pady=10)

# Iniciar a interface gráfica
root.mainloop()