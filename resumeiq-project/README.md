# ResumeIQ — Full Stack Setup Guide

## Project Structure

```
resumeiq/
├── index.html                  ← Frontend (single-file, open in browser)
├── resumeiq-backend/           ← Spring Boot backend
│   ├── pom.xml
│   ├── schema.sql              ← Run this in MySQL first
│   └── src/main/
│       ├── resources/
│       │   └── application.properties   ← Set DB password + Anthropic key here
│       └── java/com/resumeiq/
│           ├── controller/     ← AuthController, ChatController, AnalysisController, UserController
│           ├── service/        ← AuthService, ChatService, AnalysisService, UserService
│           ├── model/          ← User, ChatMessage, ResumeAnalysis
│           ├── dto/            ← Request/Response DTOs
│           ├── repository/     ← Spring Data JPA repos
│           ├── security/       ← JwtFilter, JwtUtil, UserDetailsServiceImpl
│           └── config/         ← SecurityConfig, GlobalExceptionHandler
```

---

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Java | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| MySQL | 8.0+ | Running locally on port 3306 |
| Anthropic API Key | — | https://console.anthropic.com |

---

## Step 1 — MySQL Setup

```sql
-- In your MySQL client or Workbench:
mysql -u root -p < schema.sql

-- Verify:
USE resumeiq_db;
SHOW TABLES;
-- Should show: users, chat_messages, resume_analyses
```

---

## Step 2 — Configure application.properties

Edit `src/main/resources/application.properties`:

```properties
# Required: Your MySQL root password
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Required: Your Anthropic API key (get from https://console.anthropic.com)
app.anthropic.api-key=sk-ant-api03-...

# Optional: Change this to a strong random string in production
app.jwt.secret=ResumeIQ_SuperSecret_JWT_Key_2024_ChangeThis!
```

---

## Step 3 — Start the Backend

```bash
cd resumeiq-backend
mvn spring-boot:run
```

Backend starts on **http://localhost:8080**

Verify it's running:
```bash
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"wrong"}' 
# Should return 401 or an error message (not a network error)
```

---

## Step 4 — Open the Frontend

Simply open `index.html` in your browser (double-click or drag into Chrome/Firefox).

The frontend connects to `http://localhost:8080` automatically.

> **Note:** `null` is included in CORS allowed origins so the file can be opened directly from the filesystem without a local server.

---

## API Endpoints Reference

### Auth (public)
| Method | URL | Body | Returns |
|--------|-----|------|---------|
| POST | `/api/auth/register` | `{name, email, password}` | `{token, name, email}` |
| POST | `/api/auth/login` | `{email, password}` | `{token, name, email}` |

### Users (JWT required)
| Method | URL | Body | Returns |
|--------|-----|------|---------|
| GET | `/api/users/me` | — | UserDto |
| PUT | `/api/users/me` | `{name}` | UserDto |
| POST | `/api/users/upgrade` | — | UserDto (plan=PREMIUM) |

### Chat (JWT required)
| Method | URL | Body | Returns |
|--------|-----|------|---------|
| POST | `/api/chat/send` | `{message, provider}` | `{content, role, createdAt}` |
| GET | `/api/chat/history?provider=CLAUDE` | — | List of messages |
| DELETE | `/api/chat/history?provider=CLAUDE` | — | `{message}` |

### Analysis (JWT required)
| Method | URL | Body | Returns |
|--------|-----|------|---------|
| POST | `/api/analysis/save` | `{atsScore, jobTitle, skillsJson, ...}` | AnalysisDto |
| GET | `/api/analysis/history` | — | List of analyses |
| GET | `/api/analysis/{id}` | — | AnalysisDto |
| DELETE | `/api/analysis/{id}` | — | `{message}` |

---

## How the Frontend Connects

```
User Action          Frontend                    Backend
───────────          ────────                    ───────
Sign Up/In     ──►  POST /api/auth/register  ──► MySQL: INSERT user
                    POST /api/auth/login      ──► Returns JWT token

Analyze Resume ──►  POST /api/chat/send      ──► Calls Anthropic Claude
                    (sends resume as prompt)  ◄── Returns AI JSON analysis
                    POST /api/analysis/save  ──► MySQL: saves result

AI Chat        ──►  POST /api/chat/send      ──► Calls Anthropic Claude
                                             ◄── Returns AI reply + saves to DB

Edit Profile   ──►  PUT /api/users/me        ──► MySQL: UPDATE user
Upgrade        ──►  POST /api/users/upgrade  ──► MySQL: plan = PREMIUM
```

---

## Troubleshooting

**"Cannot reach server" on login**
→ Backend isn't running. Run `mvn spring-boot:run` in the `resumeiq-backend/` folder.

**"Access denied" / CORS error in browser console**
→ Check `app.cors.allowed-origins` in application.properties includes `null` (for file:// origin).

**Analysis returns demo data instead of AI results**
→ Check `app.anthropic.api-key` is set correctly in application.properties.

**MySQL connection refused**
→ Ensure MySQL is running: `sudo systemctl start mysql` or start via MySQL Workbench.

**"Email already registered"**
→ That email exists in the DB. Use a different email or DELETE from users WHERE email='...';
