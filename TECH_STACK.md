# Technology Stack

Detailed documentation of technology choices, rationale, and implementation decisions.

---

## Core Technologies

### Java 23

**Choice**: Latest Java LTS version with modern features

**Rationale**:
- Latest language features (pattern matching, records)
- Improved performance and memory management
- Strong type safety
- Extensive ecosystem and libraries
- Cross-platform compatibility

**Key Features Used**:
- Lambda expressions for event handlers
- Stream API for data processing
- HttpClient for API calls
- Optional for null safety

---

### JavaFX 23

**Choice**: JavaFX 23.0.1 for desktop UI

**Rationale**:
- Modern, hardware-accelerated UI rendering
- Rich set of UI components
- FXML for declarative layouts
- CSS styling support
- Scene graph architecture
- Active development and updates

**Components Used**:
| Component | Purpose |
|-----------|---------|
| `ListView` | Fridge items, ingredients list |
| `TextArea` | Recipe display |
| `TextField` | User input fields |
| `Button` | Actions and navigation |
| `ComboBox` | Dropdown selections |
| `CheckBox` | Preference toggles |
| `Slider` | Spice level selection |
| `ImageView` | Icons and images |

---

## Build System

### Maven

**Choice**: Apache Maven 3.x with javafx-maven-plugin

**Rationale**:
- Industry standard for Java projects
- Declarative dependency management
- Reproducible builds
- Easy plugin integration
- IDE compatibility

**Key Plugins**:
```xml
<!-- Compiler Plugin -->
<plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.13.0</version>
    <configuration>
        <source>23</source>
        <target>23</target>
    </configuration>
</plugin>

<!-- JavaFX Plugin -->
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.8</version>
</plugin>
```

---

## Database

### MySQL 8.0 (Azure Hosted)

**Choice**: MySQL 8.0.33 on Azure Database for MySQL

**Rationale**:
- Reliable, proven relational database
- Excellent JDBC support
- Azure provides managed hosting
- Automatic backups and scaling
- JSON column support for preferences

**JDBC Connector**:
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

**Connection Pattern**:
```java
// Secure connection with SSL
String url = "jdbc:mysql://host.mysql.database.azure.com:3306/db"
           + "?useSSL=true&requireSSL=true";
```

---

## AI Integration

### OpenAI GPT-3.5 Turbo

**Choice**: OpenAI Chat Completions API

**Rationale**:
- High-quality natural language generation
- Structured output capabilities
- Fast response times
- Cost-effective for recipe generation
- Easy integration via HTTP

**API Configuration**:
```java
body.put("model", "gpt-3.5-turbo");
body.put("max_tokens", 2000);
```

**Request Format**:
- System message: Chef persona
- User message: Ingredients + preferences
- Response: Structured recipe text

---

## Email Integration

### Jakarta Mail 2.0.1

**Choice**: Jakarta Mail (formerly JavaMail)

**Rationale**:
- Standard Java email API
- SMTP protocol support
- Attachment capabilities
- Well-documented API
- Active maintenance

**Configuration**:
```xml
<dependency>
    <groupId>com.sun.mail</groupId>
    <artifactId>jakarta.mail</artifactId>
    <version>2.0.1</version>
</dependency>
```

---

## JSON Processing

### org.json

**Choice**: org.json library for JSON handling

**Rationale**:
- Simple, lightweight API
- Easy JSONObject/JSONArray creation
- No annotations required
- Perfect for API responses

**Usage**:
```xml
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20210307</version>
</dependency>
```

---

## Testing

### JUnit Jupiter 5.10.2

**Choice**: JUnit 5 for unit testing

**Rationale**:
- Modern testing framework
- Parameterized tests
- Better assertions
- IDE integration

**Configuration**:
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>
```

---

## Styling

### CSS with Glassmorphism

**Choice**: Custom CSS with frosted glass effects

**Rationale**:
- Modern, premium aesthetic
- Consistent with current design trends
- JavaFX CSS support
- Easy theme customization

**Stylesheets**:
| File | Purpose |
|------|---------|
| `style.css` | Main dashboard styles |
| `frosted-glass.css` | Glassmorphic effects |
| `preference.css` | Preferences page |
| `fridge.css` | Fridge window |

**Glass Effect**:
```css
.glass-panel {
    -fx-background-color: rgba(255, 255, 255, 0.15);
    -fx-background-radius: 15;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);
}
```

---

## Architecture Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| **UI Framework** | JavaFX | Rich desktop experience, CSS styling |
| **Architecture** | MVC | Separation of concerns, testability |
| **Database** | Cloud MySQL | Persistence across devices, reliability |
| **AI Provider** | OpenAI | Quality output, easy integration |
| **Build Tool** | Maven | Standard, reproducible builds |
| **Session** | Singleton | Simple state management |

---

## Development Tools

| Tool | Purpose |
|------|---------|
| **IntelliJ IDEA** | Primary IDE |
| **Scene Builder** | FXML visual editor |
| **MySQL Workbench** | Database management |
| **Git** | Version control |

---

## Dependency Summary

| Library | Version | Purpose |
|---------|---------|---------|
| JavaFX Controls | 23.0.1 | UI components |
| JavaFX FXML | 23.0.1 | Declarative layouts |
| MySQL Connector/J | 8.0.33 | Database connectivity |
| Jakarta Mail | 2.0.1 | Email sending |
| org.json | 20210307 | JSON processing |
| JUnit Jupiter | 5.10.2 | Unit testing |

---

## Security Stack

| Concern | Solution |
|---------|----------|
| **Secrets** | Environment variables |
| **Passwords** | Hashing before storage |
| **SQL Injection** | PreparedStatements |
| **API Security** | Bearer token auth |
