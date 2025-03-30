import pandas as pd
import os

# Função para calcular o fator de categoria
def calcular_fator_categoria(categoryRank):
    return (100 - categoryRank) / 100

# Função para calcular o custo-benefício
def calcular_custo_beneficio(data):
    data = data.copy()
    data['FatorCategoria'] = calcular_fator_categoria(data['categoryRank'])
    data['CustoBeneficio'] = (data['noRatings'] * data['FatorCategoria']) / data['cost']
    return data

# Gerar CSV com custo-benefício
def gerar_custo_beneficio(input_filepath, output_filepath):
    # Carregar dados
    if not os.path.exists(input_filepath):
        raise FileNotFoundError(f"Arquivo não encontrado: {input_filepath}")
    
    try:
        # Tentar carregar o arquivo CSV com tratamento de erros
        data = pd.read_csv(input_filepath, on_bad_lines='skip')  # Ignorar linhas problemáticas
    except pd.errors.ParserError as e:
        print(f"Erro ao processar o arquivo CSV: {e}")
        return
    
    # Depuração: Exibir colunas e primeiros registros
    print("Colunas do arquivo CSV:", data.columns.tolist())
    print("Primeiros registros do arquivo CSV:")
    print(data.head())
    
    # Verificar se as colunas necessárias existem
    colunas_necessarias = ['categoryRank', 'noRatings', 'cost']
    colunas_faltando = [col for col in colunas_necessarias if col not in data.columns]
    if colunas_faltando:
        raise KeyError(f"As seguintes colunas estão faltando no arquivo CSV: {', '.join(colunas_faltando)}")
    
    # Limpar e converter a coluna 'cost'
    def limpar_cost(valor):
        if isinstance(valor, str):
            valor = valor.split('-')[0]  # Pegar o menor valor em intervalos
            valor = valor.replace('$', '').strip()  # Remover o símbolo '$' e espaços
        try:
            return float(valor)
        except ValueError:
            return None  # Retornar None para valores inválidos

    data['cost'] = data['cost'].apply(limpar_cost)
    
    # Remover linhas com valores inválidos em 'cost'
    data = data.dropna(subset=['cost'])
    
    # Filtrar produtos com preço até 10 dólares
    data_below_10 = data[data['cost'] <= 10]
    
    # Calcular custo-benefício para produtos com preço até 10 dólares
    data_below_10 = calcular_custo_beneficio(data_below_10)
    
    # Salvar resultados em arquivos CSV
    data_below_10.to_csv(output_filepath, index=False)
    print(f"Arquivo '{output_filepath}' gerado com sucesso!")
    
    # Filtrar produtos com preço acima de 10 dólares
    data_above_10 = data[data['cost'] > 10]
    
    if not data_above_10.empty:
        output_filepath_above_10 = output_filepath.replace('.csv', '_Above10.csv')
        data_above_10.to_csv(output_filepath_above_10, index=False)
        print(f"Arquivo '{output_filepath_above_10}' gerado com sucesso!")

# Função principal
def main():
    input_filepath = 'AmazonSales.csv'  # Caminho do arquivo de entrada padrão
    output_filepath = 'CustoBeneficio10.csv'  # Caminho do arquivo de saída

    # Verificar se o arquivo de entrada existe
    if not os.path.exists(input_filepath):
        print(f"Arquivo padrão '{input_filepath}' não encontrado.")
        input_filepath = input("Por favor, insira o caminho completo do arquivo de entrada: ").strip()
    
    # Gerar custo-benefício
    try:
        gerar_custo_beneficio(input_filepath, output_filepath)
    except FileNotFoundError as e:
        print(e)
    except KeyError as e:
        print(e)

if __name__ == "__main__":
    main()