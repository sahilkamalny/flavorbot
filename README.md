Recipe Generator App

Table of Contents
	•	Overview
	•	Features
	•	Architecture
	•	Prerequisites
	•	Installation
	•	Configuration
	•	Database Setup
	•	Running the Application
	•	Usage
	•	Customization
	•	Contributing
	•	License

⸻

Overview

The Recipe Generator App is a desktop application built with JavaFX for the user interface and leverages the OpenAI API to generate creative, personalized recipes based on user preferences and available ingredients. Users can input ingredients they have on hand or specify dietary preferences, and the app returns a structured recipe with ingredient lists, step-by-step instructions, and estimated cook times.

Features
	•	Ingredient-Based Generation: Enter one or multiple ingredients; receive a full recipe.
	•	Dietary Preferences: Support for filters such as vegetarian, vegan, gluten-free, etc.
	•	User Profiles: Save and load user preferences from a MySQL database.
	•	Recipe History: View previously generated recipes stored locally.
	•	Interactive UI: Clean, intuitive JavaFX-based interface with validation.
	•	OpenAI Integration: Uses GPT models to craft detailed and creative recipes.

Architecture

flowchart LR
    A[JavaFX UI] --> B[Controller Layer]
    B --> C[OpenAI Service]
    B --> D[Database Service]
    C --> E[OpenAI API]
    D --> F[MySQL Database]
    E --> B
    F --> B

	•	JavaFX UI: Handles user input, rendering views, and client-side validation.
	•	Controller Layer: Coordinates between UI events, OpenAI calls, and database operations.
	•	OpenAI Service: Wraps HTTP communication with the OpenAI API for prompt submission.
	•	Database Service: Manages JDBC connections, CRUD operations for user profiles and recipe logs.

Prerequisites
	•	Java 11 or higher
	•	Maven or Gradle for dependency management
	•	MySQL (or compatible) server
	•	An OpenAI API Key with access to GPT-3.5/GPT-4 models

Installation
	1.	Clone the repository

git clone https://github.com/yourusername/recipe-generator-javafx.git
cd recipe-generator-javafx


	2.	Build the project
	•	Maven

mvn clean install


	•	Gradle

./gradlew build


	3.	Set up database (see Database Setup).

Configuration

Create a file named application.properties in the src/main/resources directory with the following entries:

# OpenAI API configuration
openai.api.key=YOUR_OPENAI_API_KEY
openai.api.url=https://api.openai.com/v1/chat/completions
openai.model=gpt-3.5-turbo

# Database configuration
db.url=jdbc:mysql://localhost:3306/recipe_app
db.username=your_db_user
db.password=your_db_password

Note: Do not commit application.properties to version control. Use environment variables or a secrets manager in production.

Database Setup
	1.	Create the database:

CREATE DATABASE recipe_app;


	2.	Run the schema located in src/main/resources/schema.sql:

-- Users table
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  preferences JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Recipes table
CREATE TABLE recipes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  prompt TEXT NOT NULL,
  response TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);


	3.	Seed initial data (optional):

INSERT INTO users (username, preferences)
VALUES ('chefAlice', '{"diet":"vegetarian","maxTime":30}');



Running the Application

After configuring your environment and database:

# If using Maven
mvn javafx:run

# If using Gradle
./gradlew run

The main window should launch, prompting you to log in or create a new profile.

Usage
	1.	Login / Register: Enter a username to load or create a profile.
	2.	Set Preferences: Adjust dietary filters or maximum cook time.
	3.	Input Ingredients: Enter one or more ingredients (comma-separated).
	4.	Generate Recipe: Click the Generate button to send a prompt to OpenAI.
	5.	Review & Save: View the generated recipe. Optionally save it to your history.

Customization
	•	Prompt Engineering: Modify PromptBuilder.java to adjust how prompts are sent to OpenAI for different styles or levels of detail.
	•	UI Theme: Update styles.css in the resources folder to change the look and feel.
	•	Model Selection: Change openai.model in application.properties to experiment with different GPT models.

Contributing

Contributions are welcome! Please follow these steps:
	1.	Fork the repository.
	2.	Create a feature branch: git checkout -b feature/YourFeature.
	3.	Commit your changes: git commit -m "Add your feature".
	4.	Push to the branch: git push origin feature/YourFeature.
	5.	Open a Pull Request.

Please make sure to update tests and documentation as needed.

License

This project is licensed under the MIT License.
