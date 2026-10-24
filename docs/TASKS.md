# Task split (3 members)

App: Kotlin + Jetpack Compose, SQLite (`SQLiteOpenHelper`), SharedPreferences. Package root: `com.example.mobileappth2`.

Rule: **each member only edits the files they own** (header `// Owner: Member X` in each file). That keeps merge conflicts near zero.

## Task 0 — Foundation (Member A, do first, ~30 min, merge to `main` ASAP)
B and C can start on UI/logic with the contract below while A finishes it.

- `data/model/Models.kt`, `data/db/AppDatabase.kt`, `AppContainer.kt`, `ui/nav/AppNavHost.kt`, `MainActivity.kt` wiring.

### Agreed contract (so nobody waits)
- `Role`: `"ADMIN"`, `"EMPLOYEE"`. WorkLog status: `"CHECKED_IN"`, `"COMPLETED"`. Salary status: `"PENDING"`, `"PAID"`.
- `WorkLog.workDate` = `"yyyy-MM-dd"`; `checkInTime`/`checkOutTime` = epoch millis (`Long?`); `totalHours` = `Double`.
- `Salary` unique per `(employeeId, month, year)`; `totalHours` includes overtime.
- Overtime rule: hours/day above 8 are overtime, paid at 1.5x `hourlyRate`.
- Screens are `@Composable fun XScreen(container: AppContainer, nav: Navigator)`.
- Seed accounts: `admin/admin123` (ADMIN), `emp01/123456` (EMPLOYEE, linked Employee row).

## Member A — Auth, session, employee management
| File | Work |
|---|---|
| `session/SessionManager.kt` | Login account in SharedPreferences |
| `data/repo/AccountRepository.kt` | `login()` |
| `data/repo/EmployeeRepository.kt` | add / update / delete / search / get (add also creates Account) |
| `ui/auth/LoginScreen.kt` | Login, route by role |
| `ui/home/AdminHomeScreen.kt` | Admin menu + logout |
| `ui/admin/employee/*` | List + search + delete, add/update form with hourlyRate |

## Member B — Attendance
| File | Work |
|---|---|
| `data/repo/WorkLogRepository.kt` | Queries by employee / date / month, insert, update |
| `domain/HoursCalculator.kt` | totalHours from check-in/out |
| `domain/AttendanceValidator.kt` | No duplicate check-in, check-out after check-in, flag incomplete logs |
| `ui/home/EmployeeHomeScreen.kt` | Employee menu + logout |
| `ui/employee/attendance/AttendanceScreen.kt` | Check in / check out |
| `ui/employee/attendance/AttendanceHistoryScreen.kt` | Daily records + monthly summary |
| `src/test/.../domain/` | Unit tests for calculator/validator |

## Member C — Salary
| File | Work |
|---|---|
| `data/repo/SalaryRepository.kt` | Query, upsert, update status |
| `domain/SalaryCalculator.kt` | Monthly total/overtime/salary from COMPLETED WorkLogs |
| `ui/employee/salary/MySalaryScreen.kt` | Employee views own monthly salary |
| `ui/admin/salary/SalaryManagementScreen.kt` | Admin: calculate month, list, mark PAID, delete |
| `src/test/.../domain/` | Unit tests for calculator |

C depends on B only via `WorkLogRepository.listByEmployeeAndMonth(...)`; until B pushes it, code against that signature.

## Shared files (edit by agreement, one-line changes only)
`ui/nav/AppNavHost.kt`, `AndroidManifest.xml`, `res/values/strings.xml`, `gradle/libs.versions.toml`, `app/build.gradle.kts`.

## Git workflow
1. `main` is protected by convention: no direct pushes after Task 0.
2. Branches: `feat/auth-employee` (A), `feat/attendance` (B), `feat/salary` (C).
3. Commit small, `git pull --rebase origin main` before pushing, open a PR, another member approves, squash-merge.
4. Never commit `local.properties` or `.idea/` changes.
