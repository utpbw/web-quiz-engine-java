# Web Quiz Engine

A RESTful quiz service built with Spring Boot 3, Spring Security, Spring Data JPA and H2.  
Users register, create quizzes, solve them, and browse their completion history — all over HTTP Basic Auth.

Built as part of the [Hyperskill "Web Quiz Engine with Java"](https://hyperskill.org/projects/91) project.

---

## Tech stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.5 |
| Persistence | Spring Data JPA · Hibernate 6 · H2 (file-based) |
| Security | Spring Security 6 · HTTP Basic Auth · BCrypt |
| Validation | Jakarta Bean Validation (Hibernate Validator) |
| Build | Gradle (task module under `Web Quiz Engine with Java/task/`) |
| Java | 17+ |

---

## Running the project

```bash
cd "Web Quiz Engine with Java/task"
../gradlew bootRun
```

The server starts on **port 8889**.  
The H2 database is stored at `../quizdb.mv.db` (next to the task directory).  
The H2 console is available at `http://localhost:8889/h2-console`.

---

## API reference

All endpoints except `POST /api/register` require **HTTP Basic Auth** with a registered email and password.

### Auth

| Method | URL | Auth | Description |
|--------|-----|------|-------------|
| `POST` | `/api/register` | None | Register a new user |

**Register body:**
```json
{ "email": "user@example.com", "password": "secret" }
```
Returns `200 OK` on success, `400` if the email is taken or invalid.

---

### Quizzes

| Method | URL | Auth | Description |
|--------|-----|------|-------------|
| `POST` | `/api/quizzes` | Required | Create a quiz |
| `GET` | `/api/quizzes?page=0` | Required | Get all quizzes (paginated, 10/page) |
| `GET` | `/api/quizzes/{id}` | Required | Get one quiz by ID |
| `DELETE` | `/api/quizzes/{id}` | Required (owner) | Delete your quiz |
| `POST` | `/api/quizzes/{id}/solve` | Required | Submit an answer |
| `GET` | `/api/quizzes/completed?page=0` | Required | Your completion history |

**Create quiz body:**
```json
{
  "title": "The Java Logo",
  "text": "What is depicted on the Java logo?",
  "options": ["Robot", "Tea leaf", "Cup of coffee", "Bug"],
  "answer": [2]
}
```
`answer` is a list of zero-based indices of all correct options. It may be empty (`[]`) if no option is correct. It is never returned in responses.

**Solve body:**
```json
{ "answer": [2] }
```

**Solve response:**
```json
{ "success": true, "feedback": "Congratulations, you're right!" }
```

**Paginated quiz list (`GET /api/quizzes?page=0`):**
```json
{
  "content": [
    { "id": 1, "title": "...", "text": "...", "options": ["..."] }
  ],
  "totalPages": 1,
  "totalElements": 3,
  "number": 0,
  "size": 10
}
```

**Completion history (`GET /api/quizzes/completed?page=0`):**
```json
{
  "content": [
    { "id": 1, "completedAt": "2024-05-01T10:30:00.123456" }
  ],
  "totalPages": 1,
  "totalElements": 5
}
```
Sorted newest-first. A quiz may appear multiple times if solved more than once.

---

## Validation rules

| Field | Rule |
|-------|------|
| `email` | Valid format — must contain `@` and a `.` after it |
| `password` | At least 5 characters |
| `title` | Required, not blank |
| `text` | Required, not blank |
| `options` | At least 2 entries |
| `answer` indices | Must be within `[0, options.length)` |

---

## Running the tests

```bash
cd "Web Quiz Engine with Java/task"
../gradlew test
```

The Hyperskill test harness starts a real Spring Boot server, exercises all endpoints end-to-end (including server restarts for persistence checks), and shuts it down automatically.
