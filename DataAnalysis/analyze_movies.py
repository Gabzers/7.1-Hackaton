import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Carregar o ficheiro CSV
df = pd.read_csv("movies.csv")

# Exibir as primeiras linhas do dataset
print("Primeiras linhas do dataset:")
print(movies.head())

# Informações gerais sobre o dataset
print("\nInformações gerais:")
print(movies.info())

# Estatísticas descritivas
print("\nEstatísticas descritivas:")
print(movies.describe())

# Verificar valores nulos
print("\nValores nulos por coluna:")
print(movies.isnull().sum())

# Análise de correlação (se aplicável)
if movies.select_dtypes(include=['float64', 'int64']).shape[1] > 1:
    print("\nMatriz de correlação:")
    print(movies.corr())

    # Gerar um heatmap de correlação
    plt.figure(figsize=(10, 6))
    sns.heatmap(movies.corr(), annot=True, cmap="coolwarm")
    plt.title("Heatmap de Correlação")
    plt.show()

# Análise de colunas categóricas
categorical_columns = movies.select_dtypes(include=['object']).columns
for col in categorical_columns:
    print(f"\nDistribuição de valores na coluna '{col}':")
    print(movies[col].value_counts())

    # Gráfico de barras para colunas categóricas
    plt.figure(figsize=(10, 6))
    sns.countplot(data=movies, y=col, order=movies[col].value_counts().index)
    plt.title(f"Distribuição de '{col}'")
    plt.show()

# Análise de colunas numéricas
numerical_columns = movies.select_dtypes(include=['float64', 'int64']).columns
for col in numerical_columns:
    print(f"\nAnálise da coluna numérica '{col}':")
    print(movies[col].describe())

    # Histograma para colunas numéricas
    plt.figure(figsize=(10, 6))
    sns.histplot(movies[col], kde=True, bins=30)
    plt.title(f"Distribuição de '{col}'")
    plt.show()

# Identificar outliers (se aplicável)
for col in numerical_columns:
    plt.figure(figsize=(10, 6))
    sns.boxplot(x=movies[col])
    plt.title(f"Outliers em '{col}'")
    plt.show()
