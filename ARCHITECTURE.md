# System Architecture

Detailed technical documentation of FlavorBot's architecture, design patterns, and system integrations.

---

## High-Level Architecture

```mermaid
flowchart TB
    subgraph User["User Layer"]
        Desktop[JavaFX Desktop App]
    end
    
    subgraph UI["Presentation Layer (JavaFX)"]
        FXML[FXML Views]
        CSS[Frosted Glass CSS]
        Controllers[Controllers]
    end
    
    subgraph Services["Service Layer"]
        Session[SessionManager]
        OpenAI[OpenAI Service]
        Email[Jakarta Mail]
    end
    
    subgraph Data["Data Layer"]
        DBConnector[AzureDBConnector]
        Models[User & Item Models]
    end
    
    subgraph External["External Services"]
        GPT[OpenAI GPT-3.5 Turbo]
        Azure[(Azure MySQL)]
        SMTP[SMTP Server]
    end
    
    Desktop --> FXML
    FXML --> CSS
    FXML --> Controllers
    
    Controllers --> Session
    Controllers --> OpenAI
    Controllers --> Email
    Controllers --> DBConnector
    
    Session --> Models
    DBConnector --> Models
    
    OpenAI --> GPT
    DBConnector --> Azure
    Email --> SMTP
```

---

## Application Flow

```mermaid
stateDiagram-v2
    [*] --> Login
    Login --> SignUp: New User
    SignUp --> Login: Account Created
    Login --> Preferences: Authenticated
    
    Preferences --> Dashboard: Preferences Saved
    Dashboard --> Fridge: Manage Ingredients
    Fridge --> Dashboard: Close Fridge
    
    Dashboard --> Dashboard: Generate Recipe
    Dashboard --> Dashboard: Export PDF
    Dashboard --> Dashboard: Share via Email
    Dashboard --> Dashboard: Rate Recipe
    
    Dashboard --> Login: Sign Out
    Dashboard --> [*]: Close App
```

---

## MVC Component Hierarchy

```mermaid
graph TD
    App[HelloApplication.java]
    App --> LoginView[login.fxml]
    App --> SignUpView[sign-up.fxml]
    
    LoginView --> LoginCtrl[LoginController]
    SignUpView --> SignUpCtrl[SignUpController]
    
    LoginCtrl --> PrefsView[preferences.fxml]
    PrefsView --> PrefsCtrl[PreferencesController]
    
    PrefsCtrl --> MainView[hello-view.fxml]
    MainView --> MainCtrl[MainController]
    
    MainCtrl --> FridgeView[fridge.fxml]
    FridgeView --> FridgeCtrl[FridgeController]
    
    MainCtrl --> AboutView[about.fxml]
    AboutView --> AboutCtrl[AboutController]
    
    subgraph Services["Shared Services"]
        SessionMgr[SessionManager]
        AzureDB[AzureDBConnector]
        AI[OpenAI]
    end
    
    LoginCtrl --> Services
    SignUpCtrl --> Services
    PrefsCtrl --> Services
    MainCtrl --> Services
    FridgeCtrl --> Services
```

---

## State Management Strategy

### Session Management
The `SessionManager` class implements the Singleton pattern to maintain user state across the application:

```java
// Singleton Session Manager
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    
    public static SessionManager getInstance();
    public User getCurrentUser();
    public void setCurrentUser(User user);
}
```

### State Distribution

| State Category | Location | Purpose |
|----------------|----------|---------|
| **User Session** | `SessionManager` | Current authenticated user |
| **User Data** | `User` model | ID, username, email, preferences |
| **Fridge Items** | `AzureDBConnector` | Per-user ingredient storage |
| **Preferences** | JSON in DB | Dietary restrictions, skill level |
| **UI State** | Controllers | Form inputs, selections, dialogs |

---

## AI Integration Architecture

### Recipe Generation Flow

```mermaid
sequenceDiagram
    participant User
    participant MainCtrl as MainController
    participant OpenAI as OpenAI Service
    participant GPT as GPT-3.5 Turbo

    User->>MainCtrl: Click "Generate Recipe"
    MainCtrl->>MainCtrl: Collect ingredients from list
    MainCtrl->>MainCtrl: Load user preferences from DB
    MainCtrl->>OpenAI: getTextResponse(ingredients, preferences)
    
    OpenAI->>OpenAI: Build structured prompt
    OpenAI->>GPT: POST /v1/chat/completions
    GPT-->>OpenAI: JSON response with recipe
    OpenAI->>OpenAI: Extract content from choices
    OpenAI-->>MainCtrl: Formatted recipe text
    
    MainCtrl->>MainCtrl: Display in recipeTextArea
```

### Prompt Engineering

The AI prompt is structured for professional chef-quality output:

```
System: "You are a professional chef with expertise in creating 
        detailed and structured recipes."

User: "Based on the preferences: {dietary restrictions, skill level}, 
      and the following ingredients: {ingredient list}, 
      generate a detailed recipe with clear instructions.
      
      Include sections:
      - Ingredients
      - Preparation
      - Cooking Steps
      - Tips
      - Serving Suggestions"
```

---

## Database Schema

### Azure MySQL Tables

```mermaid
erDiagram
    users {
        int id PK
        varchar username UK
        varchar email
        varchar password_hash
        json preferences
        timestamp created_at
    }
    
    fridge_items {
        int id PK
        int user_id FK
        varchar name
        timestamp created_at
    }
    
    fridge_configs {
        int id PK
        int user_id FK
        varchar name
        json items
        timestamp created_at
    }
    
    users ||--o{ fridge_items : has
    users ||--o{ fridge_configs : saves
```

### User Preferences JSON Structure

```json
{
  "dietaryRestrictions": ["vegetarian", "gluten-free"],
  "skillLevel": "intermediate",
  "mealType": ["dinner", "lunch"],
  "spiceLevel": "medium",
  "cuisinePreferences": ["Italian", "Asian"],
  "allergies": ["peanuts"]
}
```

---

## File Organization

```
FlavorBot/
├── src/main/java/
│   ├── module-info.java
│   └── edu/farmingdale/recipegenerator/
│       ├── HelloApplication.java    # App entry point
│       ├── LoginController.java     # Authentication
│       ├── SignUpController.java    # User registration
│       ├── PreferencesController.java # User preferences
│       ├── MainController.java      # Main dashboard (560 lines)
│       ├── FridgeController.java    # Ingredient management
│       ├── AboutController.java     # About window
│       ├── OpenAI.java              # GPT API integration
│       ├── AzureDBConnector.java    # Database operations
│       ├── SessionManager.java      # Singleton session
│       ├── User.java                # User model
│       └── Item.java                # Fridge item model
├── src/main/resources/
│   ├── Styling/
│   │   ├── style.css                # Main styles
│   │   ├── frosted-glass.css        # Glassmorphic effects
│   │   ├── preference.css           # Preferences page
│   │   └── fridge.css               # Fridge window
│   ├── edu/farmingdale/recipegenerator/
│   │   ├── hello-view.fxml          # Main dashboard
│   │   ├── login.fxml               # Login screen
│   │   ├── sign-up.fxml             # Registration
│   │   ├── preferences.fxml         # Preferences form
│   │   ├── fridge.fxml              # Fridge manager
│   │   └── about.fxml               # About dialog
│   └── images/                       # Icons and assets
└── pom.xml                           # Maven configuration
```

---

## Security Considerations

| Concern | Mitigation |
|---------|------------|
| **API Key Exposure** | Environment variable `key` for OpenAI API |
| **Password Storage** | Hashed passwords in database |
| **SQL Injection** | PreparedStatement for all queries |
| **Session Security** | Singleton pattern with in-memory state |
| **Email Credentials** | Environment variables for SMTP auth |

---

## Design Patterns Used

| Pattern | Implementation |
|---------|----------------|
| **MVC** | FXML views, Controllers, Models |
| **Singleton** | `SessionManager` for user state |
| **DAO** | `AzureDBConnector` for data access |
| **Observer** | JavaFX property bindings |
| **Factory** | `FXMLLoader` for view creation |
