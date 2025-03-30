import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import os  # Import os for file existence check
import tkinter as tk
from tkinter import ttk
from tkinter import messagebox
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from scipy.stats import f_oneway

# Load dataset
def load_data(filepath):
    if not os.path.exists(filepath):
        raise FileNotFoundError(f"The file '{filepath}' does not exist. Please provide a valid path.")
    try:
        data = pd.read_csv(filepath)
        print(f"File '{filepath}' loaded successfully.")
        return data
    except Exception as e:
        raise Exception(f"Error loading the file '{filepath}': {e}")

# Load dataset
filepath = 'DataAnalysis/AmazonSales_Final.csv'
data = load_data(filepath)

# Analysis functions
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
        mean_cost = round(data['cost'].mean(), 2)  # Rounded to two decimal places
        median_cost = round(data['cost'].median(), 2)  # Rounded to two decimal places
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
        # Convert the 'noRatings' column to numeric, ignoring errors
        data['noRatings'] = pd.to_numeric(data['noRatings'], errors='coerce')
        data.dropna(subset=['noRatings'], inplace=True)  # Remove rows with invalid values in 'noRatings'

        # Select the top 15 products with the most reviews
        top15 = data.nlargest(15, 'noRatings')[['product_name', 'noRatings']]

        # Create the chart
        plt.figure(figsize=(12, 8))  # Increase chart size
        sns.barplot(x='noRatings', y='product_name', data=top15, palette='viridis')
        plt.xlabel('Number of Ratings', fontsize=12)
        plt.ylabel('Product Name', fontsize=8)
        plt.title('Top 15 Products by Number of Ratings', fontsize=14)
        plt.xticks(fontsize=10)
        plt.yticks(fontsize=5)  # Reduce font size for product names
        plt.tight_layout()  # Automatically adjust to avoid cuts
        plt.show()
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the chart: {e}")

def show_category_rank_distribution():
    if 'categoryRank' not in data.columns or 'category' not in data.columns:
        messagebox.showerror("Error", "The required columns ('categoryRank' or 'category') do not exist in the dataset.")
        return
    try:
        # Convert the 'categoryRank' column to numeric, ignoring errors
        data['categoryRank'] = pd.to_numeric(data['categoryRank'], errors='coerce')
        data.dropna(subset=['categoryRank'], inplace=True)  # Remove rows with invalid values in 'categoryRank'

        # Create the chart
        plt.figure(figsize=(12, 8))
        sns.boxplot(x='category', y='categoryRank', data=data, palette='Set3')
        plt.xlabel('Category', fontsize=12)
        plt.ylabel('Category Rank', fontsize=12)
        plt.title('Distribution of Category Rank by Category', fontsize=14)
        plt.xticks(rotation=45, fontsize=10)  # Rotate category labels for better visualization
        plt.tight_layout()  # Automatically adjust to avoid cuts
        plt.show()
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the chart: {e}")

def show_correlation():
    if 'categoryRank' not in data.columns or 'noRatings' not in data.columns:
        messagebox.showerror("Error", "The required columns ('categoryRank' or 'noRatings') do not exist in the dataset.")
        return
    try:
        # Convert columns to numeric, ignoring errors
        data['categoryRank'] = pd.to_numeric(data['categoryRank'], errors='coerce')
        data['noRatings'] = pd.to_numeric(data['noRatings'], errors='coerce')
        data.dropna(subset=['categoryRank', 'noRatings'], inplace=True)  # Remove invalid rows

        # Calculate the correlation
        correlation = data['categoryRank'].corr(data['noRatings'])

        # Create the scatter plot
        plt.figure(figsize=(10, 6))
        sns.scatterplot(x='categoryRank', y='noRatings', data=data, alpha=0.6, color='blue')
        plt.xlabel('Category Rank', fontsize=12)
        plt.ylabel('Number of Ratings', fontsize=12)
        plt.title(f'Correlation between Category Rank and Number of Ratings\nCorrelation: {correlation:.2f}', fontsize=14)
        plt.tight_layout()
        plt.show()
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the chart: {e}")

def show_correlation_matrix():
    if not {'categoryRank', 'noRatings'}.issubset(data.columns):
        messagebox.showerror("Error", "The required columns ('categoryRank', 'noRatings') do not exist in the dataset.")
        return
    try:
        # Convert columns to numeric, ignoring errors
        data['categoryRank'] = pd.to_numeric(data['categoryRank'], errors='coerce')
        data['noRatings'] = pd.to_numeric(data['noRatings'], errors='coerce')
        data.dropna(subset=['categoryRank', 'noRatings'], inplace=True)  # Remove invalid rows

        # Create the correlation matrix
        correlation_matrix = data[['categoryRank', 'noRatings']].corr()

        # Create the heatmap
        plt.figure(figsize=(8, 6))
        sns.heatmap(correlation_matrix, annot=True, cmap='coolwarm', fmt=".2f", cbar=True)
        plt.title('Correlation Matrix: Category Rank vs Number of Ratings', fontsize=14)
        plt.xticks(fontsize=10)
        plt.yticks(fontsize=10)
        plt.tight_layout()
        plt.show()
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the correlation matrix: {e}")

def show_cost_ratings_correlation_matrix():
    if not {'cost', 'noRatings'}.issubset(data.columns):
        messagebox.showerror("Error", "The required columns ('cost', 'noRatings') do not exist in the dataset.")
        return
    try:
        # Convert columns to numeric, ignoring errors
        data['cost'] = pd.to_numeric(data['cost'], errors='coerce')
        data['noRatings'] = pd.to_numeric(data['noRatings'], errors='coerce')
        data.dropna(subset=['cost', 'noRatings'], inplace=True)  # Remove invalid rows

        # Create the correlation matrix
        correlation_matrix = data[['cost', 'noRatings']].corr()

        # Create the heatmap
        plt.figure(figsize=(8, 6))
        sns.heatmap(correlation_matrix, annot=True, cmap='coolwarm', fmt=".2f", cbar=True)
        plt.title('Correlation Matrix: Cost vs Number of Ratings', fontsize=14)
        plt.xticks(fontsize=10)
        plt.yticks(fontsize=10)
        plt.tight_layout()
        plt.show()
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the correlation matrix: {e}")

def show_category_summary():
    if 'category' not in data.columns or 'cost' not in data.columns or 'noRatings' not in data.columns:
        messagebox.showerror("Error", "The required columns ('category', 'cost', 'noRatings') do not exist in the dataset.")
        return
    try:
        # Convert columns to numeric, ignoring errors
        data['cost'] = pd.to_numeric(data['cost'], errors='coerce')
        data['noRatings'] = pd.to_numeric(data['noRatings'], errors='coerce')

        # Group by category and calculate averages
        summary = data.groupby('category').agg(
            avg_cost=('cost', 'mean'),
            avg_noRatings=('noRatings', 'mean')
        ).reset_index()

        # Round values to two decimal places
        summary['avg_cost'] = summary['avg_cost'].round(2)
        summary['avg_noRatings'] = summary['avg_noRatings'].round(2)

        # Create a new window to display the table
        summary_window = tk.Toplevel(root)
        summary_window.title("Category Summary")

        # Create a table using Treeview
        tree = ttk.Treeview(summary_window, columns=('Category', 'Average Cost', 'Average NoRatings'), show='headings')
        tree.heading('Category', text='Category')
        tree.heading('Average Cost', text='Average Cost')
        tree.heading('Average NoRatings', text='Average NoRatings')

        # Center values in columns
        tree.column('Category', anchor='center')
        tree.column('Average Cost', anchor='center')
        tree.column('Average NoRatings', anchor='center')

        # Insert data into the table
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
        # Convert columns to numeric, ignoring errors
        data['cost'] = pd.to_numeric(data['cost'], errors='coerce')
        data['noRatings'] = pd.to_numeric(data['noRatings'], errors='coerce')

        # Filter products with low cost, high number of reviews (minimum 10,000), and limit to 100 products
        filtered_data = data[(data['noRatings'] >= 10000)].nsmallest(100, 'cost')[['product_name', 'cost', 'noRatings', 'category']]

        # Create a new window to display the table
        table_window = tk.Toplevel(root)
        table_window.title("Low Cost, High Ratings")

        # Create a table using Treeview
        tree = ttk.Treeview(table_window, columns=('Product Name', 'Cost', 'NoRatings', 'Category'), show='headings')
        tree.heading('Product Name', text='Product Name')
        tree.heading('Cost', text='Cost')
        tree.heading('NoRatings', text='NoRatings')
        tree.heading('Category', text='Category')

        # Center values in columns
        tree.column('Product Name', anchor='center')
        tree.column('Cost', anchor='center')
        tree.column('NoRatings', anchor='center')
        tree.column('Category', anchor='center')

        # Insert data into the table
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
        # Convert the 'cost' column to numeric, ignoring errors
        data['cost'] = pd.to_numeric(data['cost'], errors='coerce')
        data.dropna(subset=['cost'], inplace=True)  # Remove rows with invalid values in 'cost'

        # Calculate average prices per category
        avg_price_per_category = data.groupby('category')['cost'].mean().sort_values(ascending=False)

        # Create the chart
        plt.figure(figsize=(12, 8))
        sns.barplot(x=avg_price_per_category.values, y=avg_price_per_category.index, palette='coolwarm')
        plt.xlabel('Average Price', fontsize=12)
        plt.ylabel('Category', fontsize=12)
        plt.title('Average Price per Category', fontsize=14)
        plt.tight_layout()  # Automatically adjust to avoid cuts
        plt.show()
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the chart: {e}")

def show_price_range_distribution():
    if 'cost' not in data.columns:
        messagebox.showerror("Error", "The column 'cost' does not exist in the dataset.")
        return
    try:
        # Convert the 'cost' column to numeric, ignoring errors
        data['cost'] = pd.to_numeric(data['cost'], errors='coerce')
        data.dropna(subset=['cost'], inplace=True)  # Remove rows with invalid values in 'cost'

        # Define price ranges
        bins = [0, 50, 100, 200, 500, 1000, 5000, 10000, np.inf]
        labels = ['0-50', '51-100', '101-200', '201-500', '501-1000', '1001-5000', '5001-10000', '10000+']
        data['price_range'] = pd.cut(data['cost'], bins=bins, labels=labels, right=False)

        # Count the number of products in each price range
        price_range_counts = data['price_range'].value_counts().sort_index()

        # Create the chart
        plt.figure(figsize=(12, 8))
        sns.barplot(x=price_range_counts.index, y=price_range_counts.values, palette='Blues_d')
        plt.xlabel('Price Range', fontsize=12)
        plt.ylabel('Number of Products', fontsize=12)
        plt.title('Number of Products per Price Range', fontsize=14)
        plt.tight_layout()  # Automatically adjust to avoid cuts
        plt.show()
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the chart: {e}")

def show_products_by_ratings():
    if 'noRatings' not in data.columns or 'product_name' not in data.columns:
        messagebox.showerror("Error", "The required columns ('noRatings' or 'product_name') do not exist in the dataset.")
        return
    try:
        # Sort products by number of reviews in descending order
        sorted_data = data.sort_values(by='noRatings', ascending=False)[['product_name', 'noRatings', 'cost', 'category']]

        # Create a new window to display the table
        table_window = tk.Toplevel(root)
        table_window.title("Products by Number of Ratings")

        # Create a table using Treeview
        tree = ttk.Treeview(table_window, columns=('Product Name', 'NoRatings', 'Cost', 'Category'), show='headings')
        tree.heading('Product Name', text='Product Name')
        tree.heading('NoRatings', text='Number of Ratings')
        tree.heading('Cost', text='Cost')
        tree.heading('Category', text='Category')

        # Center values in columns
        tree.column('Product Name', anchor='center')
        tree.column('NoRatings', anchor='center')
        tree.column('Cost', anchor='center')
        tree.column('Category', anchor='center')

        # Insert data into the table
        for _, row in sorted_data.iterrows():
            tree.insert('', tk.END, values=(row['product_name'], row['noRatings'], row['cost'], row['category']))

        tree.pack(fill=tk.BOTH, expand=True)
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the table: {e}")

def show_statistics_summary():
    if not {'cost', 'noRatings', 'categoryRank'}.issubset(data.columns):
        messagebox.showerror("Error", "The required columns ('cost', 'noRatings', 'categoryRank') do not exist in the dataset.")
        return
    try:
        # Convert columns to numeric, ignoring errors
        data['cost'] = pd.to_numeric(data['cost'], errors='coerce')
        data['noRatings'] = pd.to_numeric(data['noRatings'], errors='coerce')
        data['categoryRank'] = pd.to_numeric(data['categoryRank'], errors='coerce')

        # Calculate statistics
        stats = {
            'Metric': ['Cost', 'NoRatings', 'CategoryRank'],
            'Mean': [
                round(data['cost'].mean(), 2), round(data['noRatings'].mean(), 2), round(data['categoryRank'].mean(), 2)
            ],
            'Median': [
                round(data['cost'].median(), 2), round(data['noRatings'].median(), 2), round(data['categoryRank'].median(), 2)
            ],
            'Std Dev': [
                round(data['cost'].std(), 2), round(data['noRatings'].std(), 2), round(data['categoryRank'].std(), 2)
            ],
            'Min': [
                round(data['cost'].min(), 2), round(data['noRatings'].min(), 2), round(data['categoryRank'].min(), 2)
            ],
            'Max': [
                round(data['cost'].max(), 2), round(data['noRatings'].max(), 2), round(data['categoryRank'].max(), 2)
            ]
        }

        stats_df = pd.DataFrame(stats)

        # Create a new window to display the table
        stats_window = tk.Toplevel(root)
        stats_window.title("Statistics Summary")

        # Create a table using Treeview
        tree = ttk.Treeview(stats_window, columns=('Metric', 'Mean', 'Median', 'Std Dev', 'Min', 'Max'), show='headings')
        tree.heading('Metric', text='Metric')
        tree.heading('Mean', text='Mean')
        tree.heading('Median', text='Median')
        tree.heading('Std Dev', text='Std Dev')
        tree.heading('Min', text='Min')
        tree.heading('Max', text='Max')

        # Center values in columns
        tree.column('Metric', anchor='center')
        tree.column('Mean', anchor='center')
        tree.column('Median', anchor='center')
        tree.column('Std Dev', anchor='center')
        tree.column('Min', anchor='center')
        tree.column('Max', anchor='center')

        # Insert data into the table
        for _, row in stats_df.iterrows():
            tree.insert('', tk.END, values=(row['Metric'], row['Mean'], row['Median'], row['Std Dev'], row['Min'], row['Max']))

        tree.pack(fill=tk.BOTH, expand=True)
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while generating the statistics summary: {e}")

def perform_pearson_test():
    if not {'cost', 'noRatings', 'categoryRank'}.issubset(data.columns):
        messagebox.showerror("Error", "The required columns ('cost', 'noRatings', 'categoryRank') do not exist in the dataset.")
        return
    try:
        # Convert columns to numeric, ignoring errors
        data['cost'] = pd.to_numeric(data['cost'], errors='coerce')
        data['noRatings'] = pd.to_numeric(data['noRatings'], errors='coerce')
        data['categoryRank'] = pd.to_numeric(data['categoryRank'], errors='coerce')
        data.dropna(subset=['cost', 'noRatings', 'categoryRank'], inplace=True)  # Remove invalid rows

        # Calculate Pearson correlation coefficients
        pearson_results = data[['cost', 'noRatings', 'categoryRank']].corr(method='pearson')

        # Display results in a new window
        result_window = tk.Toplevel(root)
        result_window.title("Pearson Correlation Test Results")

        # Create a table using Treeview
        tree = ttk.Treeview(result_window, columns=('Variable 1', 'Variable 2', 'Correlation Coefficient'), show='headings')
        tree.heading('Variable 1', text='Variable 1')
        tree.heading('Variable 2', text='Variable 2')
        tree.heading('Correlation Coefficient', text='Correlation Coefficient')

        # Center values in columns
        tree.column('Variable 1', anchor='center')
        tree.column('Variable 2', anchor='center')
        tree.column('Correlation Coefficient', anchor='center')

        # Insert data into the table
        for i, col1 in enumerate(pearson_results.columns):
            for j, col2 in enumerate(pearson_results.columns):
                if i < j:  # Avoid duplicate pairs
                    tree.insert('', tk.END, values=(col1, col2, round(pearson_results.loc[col1, col2], 2)))

        tree.pack(fill=tk.BOTH, expand=True)
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while performing the Pearson test: {e}")

# Graphical interface
root = tk.Tk()
root.title("Amazon Sales Analysis")

# Buttons
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

btn_correlation_matrix = ttk.Button(root, text="Correlation Matrix: CategoryRank & NoRatings", command=show_correlation_matrix)
btn_correlation_matrix.pack(pady=10)

btn_cost_ratings_correlation_matrix = ttk.Button(root, text="Correlation Matrix: Cost & NoRatings", command=show_cost_ratings_correlation_matrix)
btn_cost_ratings_correlation_matrix.pack(pady=10)

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

btn_statistics_summary = ttk.Button(root, text="Statistics Summary", command=show_statistics_summary)
btn_statistics_summary.pack(pady=10)

btn_pearson_test = ttk.Button(root, text="Pearson Correlation Test", command=perform_pearson_test)
btn_pearson_test.pack(pady=10)

# Start the graphical interface
root.mainloop()