# AGENTS.md

Rules for any AI agent (and human) working on this repo. Read `docs/TASKS.md` first for who owns what.

## Project
Student project: employee working hours and salary management. Kotlin, Jetpack Compose (Material3), SQLite via `SQLiteOpenHelper`, SharedPreferences for the login session. Package root: `com.example.mobileappth2`.

**Priority: finish fast, keep it simple, make it work.** Score depends on completion time. Do not over-engineer.

## Scope rules
- Only edit files marked `// Owner: Member X` for your member. Shared files (`AppNavHost.kt`, `AndroidManifest.xml`, `strings.xml`, `build.gradle.kts`, `libs.versions.toml`): one-line changes only.
- Do exactly what the task asks. No extra features, no refactoring other people's files.
- Do not add libraries (no Room, Hilt, Retrofit, Navigation lib, etc.). Use what is already in the project.
- Do not touch `.idea/`, `local.properties`, or Gradle/AGP/Kotlin versions.
- No ViewModels, no coroutines/Flow, no DI framework. Plain synchronous repository calls from composables are fine at this scale.

## Folder layout
```
data/model/   data classes + constants (Account, Employee, WorkLog, Salary)
data/db/      AppDatabase (SQLiteOpenHelper)
data/repo/    one repository per table, raw SQL / ContentValues
domain/       pure Kotlin logic (calculators, validators), unit-testable, no Android imports
session/      SessionManager (SharedPreferences)
ui/<area>/    one file per screen
ui/nav/       Screen + Navigator + AppNavHost
```
Layers: `ui` -> `domain` / `data/repo` -> `data/db`. UI never touches the database directly; `domain` never imports Android or Compose.

## Naming
- Files: `PascalCase.kt`, one main class/composable per file, named after it.
- Classes/composables: `PascalCase`. Screens end in `Screen` (`LoginScreen`). Repositories end in `Repository`.
- Functions/variables: `camelCase`, descriptive but short. Verbs for functions (`checkIn`, `calculateMonth`), nouns for variables.
- Constants: `UPPER_SNAKE_CASE` (`STANDARD_DAILY_HOURS`).
- DB tables/columns match the spec exactly: `Account`, `Employee`, `WorkLog`, `Salary`; columns in `camelCase` (`hourlyRate`, `checkInTime`).
- Booleans: `isX` / `hasX`. Composable state: `var name by remember { mutableStateOf("") }`.
- No abbreviations except common ones (`id`, `db`, `nav`).

## Data conventions (do not deviate)
- Role: `"ADMIN"`, `"EMPLOYEE"`. WorkLog status: `"CHECKED_IN"`, `"COMPLETED"`. Salary status: `"PENDING"`, `"PAID"`. Use the constants in `Models.kt`, never raw strings.
- `workDate`: `"yyyy-MM-dd"` string. `checkInTime` / `checkOutTime`: epoch millis `Long?`. `totalHours`: `Double`.
- Salary is unique per `(employeeId, month, year)`; `totalHours` includes overtime.
- Overtime: hours per day above 8 are overtime, paid at 1.5x `hourlyRate`. Constants live in `SalaryCalculator`.
- Passwords are plain text (student project). Seed: `admin/admin123`, `emp01/123456`.

## Function order inside a file
1. `package`, imports (no wildcard imports).
2. Public API first (the class / main composable).
3. Private helpers below it, in the order they are used.
4. Composable previews, if any, last (optional; skip them to save time).

Inside a repository: reads (`get...`, `list...`, `search...`) first, then `insert`, `update`, `delete`, then private cursor-mapping helpers.

## UI design language: barebone
- Plain Material3 defaults: `Column`, `Row`, `LazyColumn`, `Text`, `Button`, `OutlinedTextField`, `AlertDialog`. No custom theme work, no animations, no icons or images, no custom fonts or colors.
- Layout: `Column(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp))`, spacing `Arrangement.spacedBy(8.dp)`.
- Every screen: a title `Text`, content, and a `Back` button (`nav.pop()`). Lists: one `Text` line or `Row` per item.
- Screen signature: `@Composable fun XScreen(container: AppContainer, nav: Navigator)` (extra params after these).
- Feedback: show errors/success as a `Text` on the screen (red for errors) or a `Toast`. Hardcoded English strings in the composable are fine; do not move them to `strings.xml`.
- Validate input before saving: empty fields, non-numeric rate, negative values. Show the message, do not crash.

## Code style
- Short and direct. Prefer 5-line functions over clever abstractions. No interfaces or base classes unless two implementations exist.
- Comments only for non-obvious "why" (e.g. the overtime rule). No KDoc on every function, no commented-out code.
- Use `val` over `var`, expression bodies for one-liners, `?.` / `?:` over null checks, `use {}` on Cursors.
- SQL: use parameterized queries (`?` with args), never string-concatenate user input.
- Keep a file under ~150 lines. If longer, you are doing too much.

## Tests
Only unit tests for `domain/` logic (calculators, validators) in `app/src/test/.../domain/`. Plain JUnit4, no mocking. Skip UI tests.

## Git
- Branch per member: `feat/auth-employee` (A), `feat/attendance` (B), `feat/salary` (C). Never push to `main` directly after the foundation task.
- Small commits, imperative message ("Add check-in validation"). `git pull --rebase origin main` before pushing; open a PR.
- Stage explicit paths. Never `git add -A` or `git add .`.
- Do not commit build output, `.idea/`, `local.properties`.

## Definition of done (per task)
- Project compiles (`./gradlew :app:assembleDebug`).
- The feature works on an emulator with the seed accounts.
- No leftover `TODO` in your own files, no unused imports.
