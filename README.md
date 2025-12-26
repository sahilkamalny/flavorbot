# 🍽️ FlavorBot

**FlavorBot** is a modern, AI-powered desktop application built using **JavaFX** and integrates with the **OpenAI API** to help users generate creative and personalized recipes based on their preferences and available ingredients.

---

## 🚀 Features

- 🔍 **AI-Powered Recipe Creation**: Enter available ingredients or dietary preferences and get unique recipes instantly.
- 🧠 **OpenAI API Integration**: Uses natural language understanding to deliver structured and easy-to-follow recipes.
- 📝 **Structured Output**: Recipes include an ingredient list, preparation steps, and estimated cooking time.
- 💡 **Suggestions & Variations**: Offers creative twists and substitutions for dietary needs.
- 🖥️ **JavaFX UI**: Smooth and responsive user interface for seamless desktop experience.

---

## 📸 Screenshots

![image](https://github.com/user-attachments/assets/ec79be02-5688-4eed-83e9-a6ea81757107)
![image](https://github.com/user-attachments/assets/889c7efe-4d44-4cfc-bc8b-22894c44a297)
![image](https://github.com/user-attachments/assets/64fea5a7-4ef8-428f-a1f8-54ee9d864f62)
![image](https://github.com/user-attachments/assets/f4f757f0-a89f-49ad-b0c4-039590134ca2)
![image](https://github.com/user-attachments/assets/4fe2b7d9-ead7-4f2a-b6f4-bc063d5450ef)

---
## 🎥 Demo Video

Watch the full walkthrough and demo of the Recipe Generator App on YouTube:

[![Watch the video](https://img.youtube.com/vi/0UNjeKQFY9I/0.jpg)](https://www.youtube.com/watch?v=0UNjeKQFY9I)

This video showcases:
- How to input ingredients and preferences
- Generating recipes using the AI backend
- Viewing structured results with ingredients, steps, and cook time
- The responsive and user-friendly JavaFX interface
---

## 🛠️ Technologies Used

- **Java 17+**
- **JavaFX 17+**
- **OpenAI API (ChatGPT or GPT-4 based)**

---

## 🔧 Installation Instructions

### Prerequisites

- Java JDK 17 or higher installed
- Maven or Gradle (for dependency management)
- An OpenAI API key

### Clone the Repository

```bash
git clone https://github.com/yourusername/recipe-generator-app.git
cd recipe-generator-app
```

### Configure the API Key

1. Open the file `src/main/resources/config.properties` (or your environment config file).
2. Add your OpenAI API key:

```
openai.api.key=your_openai_api_key_here
```

Alternatively, use environment variables if preferred.

### Build & Run

#### With Maven

```bash
mvn clean install
mvn javafx:run
```

#### With Gradle

```bash
./gradlew run
```

---

## 🧪 How to Use

1. Launch the app.
2. Enter ingredients you have (e.g., "chicken, rice, garlic").
3. Add optional dietary preferences (e.g., "gluten-free", "vegan").
4. Click **Generate Recipe**.
5. View the generated recipe with ingredients, steps, and cook time.

---

## 🛡️ Security Note

Ensure your OpenAI API key is kept private and **do not** commit it to source control.

---


## 🙋‍♂️ Author

- Haris Akbar – https://github.com/ha.akbar03
- Jonatan Paulino - https://github.com/JPR420
- Andrew Escorcia - https://github.com/aescorcia65
- Sahil Kamal - https://github.com/sahilkamalny

---

## 🌐 Connect

For support or inquiries, please open an issue on GitHub.

Happy cooking! 👨‍🍳👩‍🍳
