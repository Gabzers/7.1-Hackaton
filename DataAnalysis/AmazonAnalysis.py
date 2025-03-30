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