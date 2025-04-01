import pandas as pd
import os

# Function to calculate the category factor
def calculate_category_factor(categoryRank):
    return (100 - categoryRank) / 100

# Function to clean the data
def clean_data(data):
    for column in ['categoryRank', 'noRatings', 'cost']:
        # Remove special characters and spaces
        data[column] = data[column].astype(str).str.replace(r'[^\d.,]', '', regex=True)
        # Replace commas with dots for decimal points
        data[column] = data[column].str.replace(',', '.')
    return data

# Function to calculate cost-benefit
def calculate_cost_benefit(data):
    print("Data before cleaning:")
    print(data.head())  # Initial debugging

    # Clean the data
    data = clean_data(data)

    print("Data after cleaning:")
    print(data.head())  # Debugging after cleaning

    # Convert to numeric
    data['categoryRank'] = pd.to_numeric(data['categoryRank'], errors='coerce')
    data['noRatings'] = pd.to_numeric(data['noRatings'], errors='coerce')
    data['cost'] = pd.to_numeric(data['cost'], errors='coerce')

    print("Data after numeric conversion:")
    print(data.head())  # Debugging after conversion

    # Drop rows with NaN or invalid values
    data.dropna(subset=['categoryRank', 'noRatings', 'cost'], inplace=True)

    # Ensure noRatings and cost are greater than 0
    data = data[(data['noRatings'] > 0) & (data['cost'] > 0)]

    if data.empty:
        print("All data was removed after filtering. Check the CSV.")
        return data

    data['CategoryFactor'] = calculate_category_factor(data['categoryRank'])
    data['CostBenefit'] = (data['noRatings'] * data['CategoryFactor']) / data['cost']
    
    return data

# Function to debug the data
def debug_data(data):
    print("Data summary after cost-benefit calculation:")
    print(data.describe())
    print("\nSample rows:")
    print(data.head())

# Function to generate cost-benefit CSVs by price range
def generate_cost_benefit(data, output_dir, top_n=10000):
    data = calculate_cost_benefit(data)
    debug_data(data)  # Added for debugging

    # Define cost ranges
    ranges = [
        {"name": "Products_Under_5_Euros.csv", "filter": data['cost'] <= 5},
        {"name": "Products_5_To_10_Euros.csv", "filter": (data['cost'] > 5) & (data['cost'] <= 10)},
        {"name": "Products_10_To_15_Euros.csv", "filter": (data['cost'] > 10) & (data['cost'] <= 15)},
    ]

    # Generate a CSV file for each range
    for range_ in ranges:
        filtered_data = data[range_["filter"]]
        best_products = filtered_data.sort_values(by='CostBenefit', ascending=False).head(top_n)  # Descending order

        if best_products.empty:
            print(f"No products found for the range: {range_['name']}")
        else:
            output_filepath = os.path.join(output_dir, range_["name"])
            best_products.to_csv(output_filepath, index=False)
            print(f"File successfully generated: {output_filepath}")

# Function to load the CSV
def load_data(filepath):
    if not os.path.exists(filepath):
        raise FileNotFoundError(f"File not found: {filepath}")

    # Select only relevant columns
    data = pd.read_csv(filepath, usecols=["product_name", "category", "categoryRank", "noRatings", "cost"])
    return data

# Main function
def main():
    input_filepath = input("Enter the path to the Amazon CSV: ").strip()
    output_dir = "CostBenefit_Results"

    try:
        # Create the output directory if it doesn't exist
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)

        data = load_data(input_filepath)

        expected_columns = {'product_name', 'category', 'categoryRank', 'noRatings', 'cost'}
        if not expected_columns.issubset(data.columns):
            raise ValueError("The CSV does not contain the expected columns.")

        generate_cost_benefit(data, output_dir)

    except FileNotFoundError as e:
        print(e)
    except ValueError as e:
        print(e)
    except Exception as e:
        print(f"An unexpected error occurred: {e}")

if __name__ == "__main__":
    main()