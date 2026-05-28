# Architecture

This document traces how the codebase grew across all six Hyperskill stages.  
Each stage added one concern; the layers that existed before stayed unchanged.

---

## Stage 1 — Solving a simple quiz

**Goal:** a single hardcoded quiz served over HTTP.

```
src/
└── engine/
    ├── WebQuizEngine.java          ← @SpringBootApplication entry point
    ├── controller/
    │   └── QuizController.java     ← GET/POST /api/quiz (hardcoded quiz)
    └── model/
        ├── Quiz.java               ← title, text, String[] options
        └── Result.java             ← success flag + feedback string
```

The answer (`int`) was a `@RequestParam` query parameter.  
No persistence, no validation, no users — just the shape of the API.

---

## Stage 2 — Lots of quizzes

**Goal:** store multiple quizzes in memory; make the API RESTful.

```
+ engine/
    + service/
    │   └── QuizService.java        ← ConcurrentHashMap + AtomicInteger ID counter
    + exception/
    │   └── QuizNotFoundException.java  ← @ResponseStatus(404)
```

| Before | After |
|--------|-------|
| Single `Quiz` field in controller | `ConcurrentHashMap<Integer, Quiz>` in `QuizService` |
| `GET /api/quiz` | `GET /api/quizzes`, `GET /api/quizzes/{id}` |
| `POST /api/quiz` | `POST /api/quizzes` (create) + `POST /api/quizzes/{id}/solve` |

`QuizService` was injected into the controller — the first step toward separating HTTP concerns from business logic.

---

## Stage 3 — Making quizzes more interesting

**Goal:** validate quiz creation; support multiple correct answers.

```
+ engine/
    + exception/
    │   └── InvalidAnswerException.java  ← @ResponseStatus(400)
    + utils/
        └── Utils.java                   ← checkAnswerOptions(), index bounds check
```

| Before | After |
|--------|-------|
| `answer: int` (single index) | `answer: Set<Integer>` (any number of correct options) |
| No input validation | `@NotEmpty`, `@NotNull`, `@Size(min=2)` on `Quiz` fields |
| Solve via `@RequestParam` | Solve via `@RequestBody {"answer": [...]}` |

`build.gradle` gained `spring-boot-starter-validation`.  
The `answer` field was marked `@JsonProperty(WRITE_ONLY)` — it is accepted on input but never returned in responses.

---

## Stage 4 — Moving quizzes to DB

**Goal:** survive server restarts; introduce a proper layered model.

```
+ engine/
    + model/
    │   + jpa/
    │   │   ├── Quiz.java           ← @Entity, @OneToMany options
    │   │   └── Option.java         ← @Entity, text + boolean answer flag
    │   + dto/
    │       ├── QuizDto.java        ← API input/output (with validation)
    │       └── ResultDto.java      ← API output for solve
    + repository/
        └── QuizRepository.java     ← JpaRepository<Quiz, Integer>
```

The architecture split into three distinct layers:

```
HTTP ←→ DTO  ←→  Service  ←→  JPA Entity  ←→  H2 database
       (API)              (persistence)
```

`Utils` gained DTO↔entity mapping methods.  
`build.gradle` gained `spring-boot-starter-data-jpa` and `h2`.  
`application.properties` gained the H2 file-based datasource URL (`jdbc:h2:file:../quizdb`).

**Key design choice:** options are stored as `Option` entities (not a plain string collection) so each option carries an `answer` boolean, letting the service derive correct indices without storing them separately.  
`@OrderColumn` preserves insertion order so `GET /api/quizzes/{id}` always returns options in the original sequence.

---

## Stage 5 — User authorization

**Goal:** register users, protect all quiz endpoints with HTTP Basic Auth, enforce quiz ownership.

```
+ engine/
    + model/
    │   + jpa/
    │   │   └── User.java               ← @Entity (email unique, BCrypt password)
    │   + dto/
    │       └── RegisterDto.java        ← @Email(regexp) + @Size(min=5) password
    + repository/
    │   └── UserRepository.java         ← findByEmail()
    + service/
    │   └── UserService.java            ← UserDetailsService + register()
    + security/
        └── SecurityConfig.java         ← SecurityFilterChain, PasswordEncoder
```

`Quiz` entity gained a `createdBy` (email) column; `deleteQuiz()` checks ownership and throws 403 if the caller is not the creator.

The controller gained two new endpoints:

| Endpoint | Behaviour |
|----------|-----------|
| `POST /api/register` | Public; validates email format and password length |
| `DELETE /api/quizzes/{id}` | 204 for owner, 403 for others, 404 if missing |

**Tricky Spring Boot issue:** when `@Valid` rejects a registration request, Spring Boot dispatches the error to `POST /error`. That endpoint also needs `permitAll()` or the anonymous error dispatch returns 401 instead of 400.  
**Tricky circular dependency:** `SecurityConfig` defines `PasswordEncoder`; `UserService` needs `PasswordEncoder`. Resolved by keeping `UserService` out of `SecurityConfig`'s constructor — Spring Security auto-discovers any `UserDetailsService` bean in the context.

---

## Stage 6 — Advanced queries

**Goal:** paginate the quiz list; track and page completion history.

```
+ engine/
    + model/
    │   + jpa/
    │   │   └── QuizCompletion.java     ← @Entity (userEmail, quizId, completedAt)
    │   + dto/
    │       └── CompletionDto.java      ← id (quiz) + completedAt
    + repository/
        └── CompletionRepository.java   ← findByUserEmailOrderByCompletedAtDesc()
```

| Before | After |
|--------|-------|
| `getAllQuizzes()` → `List<QuizDto>` | `getAllQuizzes(page)` → `Page<QuizDto>` |
| `solveQuizById(id, answer)` | `solveQuizById(id, answer, userEmail)` — saves `QuizCompletion` on correct answer |
| No completion history | `GET /api/quizzes/completed?page=n` → `Page<CompletionDto>`, newest first |

`spring.jackson.serialization.write-dates-as-timestamps=false` makes `LocalDateTime` serialize as an ISO string instead of a numeric array.

---

## Final package map

```
engine/
├── WebQuizEngine.java
├── controller/
│   └── QuizController.java
├── exception/
│   ├── InvalidAnswerException.java
│   └── QuizNotFoundException.java
├── model/
│   ├── dto/
│   │   ├── CompletionDto.java
│   │   ├── QuizDto.java
│   │   ├── RegisterDto.java
│   │   └── ResultDto.java
│   └── jpa/
│       ├── Option.java
│       ├── Quiz.java
│       ├── QuizCompletion.java
│       └── User.java
├── repository/
│   ├── CompletionRepository.java
│   ├── QuizRepository.java
│   └── UserRepository.java
├── security/
│   └── SecurityConfig.java
├── service/
│   ├── QuizService.java
│   └── UserService.java
└── utils/
    └── Utils.java
```

---

## Recurring fixes across stages

The Hyperskill IDE re-scaffolds the `task/` directory on each stage advance and resets several files. Three bugs appeared in the IDE-generated code every time and were fixed each stage:

| Bug | Fix |
|-----|-----|
| `javax.*` imports (Spring Boot 2 namespace) | Changed to `jakarta.*` (Spring Boot 3) |
| `Arrays.equals(set.toArray(), set.toArray())` — broken set comparison | Replaced with `Set.equals()` |
| `index > options.size()` off-by-one in bounds check | Changed to `index >= options.size()` |
