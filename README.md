# Hackathon Website Application

This repository contains the **Hackathon Website** project, a web application that combines **data analysis** and a **basic streaming platform**. The project leverages datasets from **IMDB** (movies) and **Amazon Best Sellers** to provide insights and features such as personalized recommendations, a shop, and a rewards system.

## Overview

The Hackathon Website is designed to showcase the integration of data analysis and a streaming platform. It includes the following features:

- **Data Analysis**:
  - Insights derived from IMDB and Amazon datasets, such as movie ratings, popularity trends, and genre statistics.
  - Visualizations and statistical analyses to understand user preferences and trends in the entertainment industry.

- **Streaming Platform**:
  - A basic streaming platform with a **home page** featuring personalized movie recommendations based on user preferences.
  - A **shop** where users can redeem movies and Amazon products using points.
  - A **rewards system** where users earn points for watching ads or engaging with the platform.
  - A **battle pass** system that unlocks additional rewards and features as users progress.
  - **Registration and Login System** to manage user accounts securely.

## Technologies Used

The project leverages the following technologies:

- **Java**: Backend development for the streaming platform.
- **Spring Boot**: Framework for building production-ready applications.
- **MySQL**: Relational database for storing user data, movie information, and rewards.
- **Python**: Data analysis and visualization using libraries like **Pandas**, **NumPy**, **Matplotlib**, and **Seaborn**.
- **Maven**: Build automation tool for managing dependencies and project lifecycle.
- **Visual Studio Code (VS Code)**: Recommended IDE for development.

## Prerequisites

Ensure the following software is installed and properly configured:

1. **Java Development Kit (JDK)**:
   - Version: **23.1.0**
   - Verify installation:
     - Run `java --version` in the terminal to check the version.
     - Run `%JAVA_HOME%` in the terminal to ensure it points to the JDK directory (e.g., `C:\Program Files\Java\jdk-23`).

2. **MySQL**:
   - Install MySQL from the [official website](https://dev.mysql.com/downloads/installer/).
   - Add MySQL to the system's environment variables.

3. **Python**:
   - Install Python (version 3.8 or higher) and the required libraries:
     ```bash
     pip install pandas numpy matplotlib seaborn
     ```

4. **Visual Studio Code (VS Code)**:
   - Required extensions:
     - **Extension Pack for Java**
     - **Spring Boot Extension Pack**
     - **MySQL**

## Project Setup

Follow these steps to set up and run the project:

### 1. Clone the Repository
Clone this repository to your local environment:
```bash
git clone <REPOSITORY_URL>
```

### 2. Open the Project in VS Code
- Open the project directory in **VS Code**.
- Ensure **Maven** is selected as the dependency manager.

### 3. Configure the Database
1. Open the **MySQL Command Line** and create the database:
   ```sql
   CREATE DATABASE hackatondb;
   ```
2. In **VS Code**, connect to the database using the **MySQL** extension:
   - Username: `root`
   - Password: `1234`

### 4. Run the Data Analysis
Navigate to the `DataAnalysis` folder and execute the Python script:
```bash
python DataAnalysis.py
python AmazonAnalysis.py
```
This script performs statistical analysis and generates visualizations based on the IMDB and Amazon datasets.

### 5. Run the Application
- Open the terminal in VS Code and execute:
  ```bash
  mvn spring-boot:run
  ```
- The application will be available at: [http://localhost:8080/](http://localhost:8080/).

## Features

### Data Analysis
- **Movie Insights**: Analyze movie ratings, popularity trends, and genre statistics.
- **Amazon Products**: Explore trends in best-selling products.
- **Visualizations**: Generate charts and graphs to understand user preferences.

### Streaming Platform
- **Home Page**: Displays personalized movie recommendations based on user preferences.
- **Shop**: Users can redeem movies and Amazon products using points earned on the platform.
- **Rewards System**: Earn points by watching ads or engaging with the platform.
- **Battle Pass**: Unlock additional rewards and features as you progress.
- **Registration and Login System**:
  - Users can create accounts with secure credentials.
  - Login functionality ensures personalized experiences and secure access to features.

## Project Structure

- **`src/main/java/com/hackaton/website`**:
  - Contains the main class `WebsiteApplication.java`, which initializes the application.
- **`DataAnalysis/DataAnalysis.py`**:
  - Python script for performing data analysis and generating visualizations.

## Testing the Application

1. Access the application at [http://localhost:8080/](http://localhost:8080/).
2. Verify that all features, including the shop, rewards system, and battle pass, are functioning as expected.
3. Test the **registration and login system**:
   - Create a new user account.
   - Log in with the created credentials.
   - Verify that personalized recommendations and rewards are displayed.

## License

This project is licensed under the **MIT License**.