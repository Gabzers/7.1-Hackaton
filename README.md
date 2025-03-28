# Application Installation Guide

Follow the steps below to properly install and configure the application:

## 1. Install Extensions in VS Code
- Install the **Extension Pack for Java** and **Spring Boot Extension Pack** in VS Code.

## 2. Install Java
- Install **Java 23.1.0** and add it to the environment variables until:
  - In **CMD**, running `java --version` shows the correct version.
  - In **CMD**, running `%JAVA_HOME%` shows the correct Java directory (e.g., `C:\Program Files\Java\jdk-23`).

## 3. Open the Project in VS Code
- Open the project in **VS Code** and select **Maven**.

## 4. Install MySQL
- Install **MySQL** from the [official link](https://dev.mysql.com/downloads/installer/).

## 5. Configure MySQL in VS Code
- In VS Code, install the **MySQL** extension and create a user (default **root** with password **1234**, no need to modify the `application.properties` file).

## 6. Add MySQL to Environment Variables
- Add **MySQL** correctly to the environment variables.

## 7. Create the Database
- In **MySQL Command Line**, create a database named **hackatondb**.

## 8. Connect VS Code to MySQL
- Connect **VS Code** to **MySQL**, creating a connection to the database.

## 9. Verify and Run the Application
- Verify if everything is correct and try to run the application.

## 10. Access the Application
- Open the site at **http://localhost:8080/**.