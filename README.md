# 🍽️ Recipe Generator App

The **Recipe Generator App** is a modern, AI-powered desktop application built using **JavaFX** and integrates with the **OpenAI API** to help users generate creative and personalized recipes based on their preferences and available ingredients.

---

## 🚀 Features

- 🔍 **AI-Powered Recipe Creation**: Enter available ingredients or dietary preferences and get unique recipes instantly.
- 🧠 **OpenAI API Integration**: Uses natural language understanding to deliver structured and easy-to-follow recipes.
- 📝 **Structured Output**: Recipes include an ingredient list, preparation steps, and estimated cooking time.
- 💡 **Suggestions & Variations**: Offers creative twists and substitutions for dietary needs.
- 🖥️ **JavaFX UI**: Smooth and responsive user interface for seamless desktop experience.

---

## 📸 Screenshots

*(Insert screenshots of the app here)*

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
3. Optionally add preferences (e.g., "gluten-free", "vegan").
4. Click **Generate Recipe**.
5. View the output recipe with ingredients, steps, and cook time.

---

## 🛡️ Security Note

Ensure your OpenAI API key is kept private and **do not** commit it to source control.

---

## 🤝 Contributions

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

---

## 📄 License

This project is licensed under the MIT License.

---

## 🙋‍♂️ Author

- **Your Name** – [@yourgithub](https://github.com/yourgithub)

---

## 🌐 Connect

For support or inquiries, please open an issue on GitHub.

Happy cooking! 👨‍🍳👩‍🍳
