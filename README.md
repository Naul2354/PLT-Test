# PLT-Test

Selenium WebDriver + TestNG automation tests for the PLT e-learning platform, built with the **Page Object Model (POM)** design pattern.

## Project Structure

```
POL_Test/
├── pom.xml                          # Maven config & dependencies
├── testng.xml                       # TestNG suite configuration
├── Jenkinsfile                      # Jenkins CI/CD pipeline
├── Jenkinsfile.docker               # Docker-based pipeline
├── install-chrome.sh                # Chrome installation for CI
│
├── src/
│   ├── base/
│   │   └── BaseTest.java            # WebDriver setup/teardown (@BeforeMethod/@AfterMethod)
│   │
│   ├── pages/
│   │   ├── LoginPage.java              # Login — admin & user authentication
│   │   ├── StudentManagementPage.java  # Admin — student CRUD operations
│   │   ├── CourseManagementPage.java   # Admin — course, forum, video conference management
│   │   ├── CourseContentPage.java      # User view — course content, forum & VC verification
│   │   └── HomeworkManagementPage.java # Admin — homework & question management
│   │
│   ├── models/
│   │   ├── StudentInfo.java          # Student data model
│   │   ├── CourseData.java           # Course data model (title, chapters, forum, VC)
│   │   ├── ChapterData.java          # Chapter data model
│   │   ├── ChapterInfo.java          # Chapter + lessons (for verification)
│   │   ├── LessonData.java           # Lesson data model
│   │   ├── HomeworkData.java         # Homework data model
│   │   └── QuestionData.java         # Question data model (multi-choice, audio, video, essay)
│   │
│   ├── utils/
│   │   ├── SeleniumHelper.java       # Shared helpers (safeClick, fill, delay...)
│   │   └── DataLoader.java           # CSV/JSON test data loading & random generation
│   │
│   ├── tests/
│   │   ├── admin/
│   │   │   └── StudentManagementTest.java     # Student CRUD workflow test
│   │   └── user/
│   │       ├── AddingCourseTest.java          # Full course lifecycle (10 parts)
│   │       ├── CourseExpandTest.java          # Expand & verify course content test
│   │       ├── HomeworkTest.java              # Homework creation & question types test
│   │       └── UserViewForumVCTest.java       # User view — forum comment & VC verification
│   │
│   ├── resources/
│   │   ├── courses.json              # Course test data (6 courses with chapters, forum, VC)
│   │   ├── chapters.json             # Chapter test data
│   │   ├── lessons.json              # Lesson test data
│   │   ├── homework.json             # Homework & question test data
│   │   ├── student_test_data.csv     # Student test data
│   │   ├── vietnamese_names.csv      # Random Vietnamese name generation
│   │   ├── vietnamese_locations.csv  # Random address generation
│   │   └── data.txt                  # Expected course content for verification
│   │
│   └── image/                        # Test images for upload (thumbnails, attachments)
│
└── test-reports/                     # Generated test logs & comparison files
```

## Architecture (Page Object Model)

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Tests      │────>│    Pages     │────>│   Models     │
│ (test flow   │     │ (locators +  │     │ (data POJOs) │
│  & asserts)  │     │  actions)    │     │              │
└──────┬───────┘     └──────────────┘     └──────────────┘
       │
       ▼
┌──────────────┐     ┌──────────────┐
│  BaseTest    │     │    Utils     │
│ (driver      │     │ (helpers +   │
│  lifecycle)  │     │  data loader)│
└──────────────┘     └──────────────┘
```

- **Tests** contain only test flow and assertions — no locators, no raw Selenium calls
- **Pages** own all locators and page-specific actions for a single page/section
- **Models** are plain data objects passed between layers
- **BaseTest** manages ChromeDriver creation and cleanup for every test method
- **Utils** provide shared Selenium helpers and test data loading

## Prerequisites

- Java 17+
- Maven 3.8+
- Google Chrome browser

## How to Run

Run all tests:
```bash
mvn test
```

Run a specific test class:
```bash
mvn test -Dtest=tests.admin.StudentManagementTest
mvn test -Dtest=tests.user.AddingCourseTest
mvn test -Dtest=tests.user.CourseExpandTest
mvn test -Dtest=tests.user.HomeworkTest
mvn test -Dtest=tests.user.UserViewForumVCTest
```

## Test Descriptions

| Test | Description |
|------|-------------|
| `StudentManagementTest` | Full CRUD — add student with random data, verify, edit address, verify, delete, verify gone |
| `AddingCourseTest` | Full course lifecycle (10 parts) — create course, add learners (random + hardcoded), add chapters/lessons/materials, add forum & video conference, user view verification (content, forum comment, VC), update course, delete course |
| `CourseExpandTest` | Expand all chapters/lessons, compare against expected data file, log results |
| `HomeworkTest` | Create homework with multiple question types (multi-choice, audio, image, video, essay) |
| `UserViewForumVCTest` | Standalone user view test — verify course content, forum (add comment & send), video conference |

### AddingCourseTest Workflow (10 Parts)

| Part | Description |
|------|-------------|
| 1 | Login as admin, get student list |
| 2 | Create new course with thumbnail |
| 3 | Verify course appears in list |
| 4 | Add learners (random from student list + hardcoded `test.pltsolutions@gmail.com`) |
| 5 | Add course content (chapters, lessons, video/attachment materials) |
| 6 | Add forum (name, description, thumbnail) |
| 7 | Add video conference (YouTube link, description, meeting link) |
| 8 | User view — verify course content, forum (add comment), video conference |
| 9 | Search & update course (title, description, thumbnail) |
| 10 | Delete learners, delete course, verify cleanup |

## Test Reports

- TestNG HTML report: `test-output/index.html`
- Surefire reports: `target/surefire-reports/`
- CourseExpandTest logs: `test-reports/CourseExpandTest_ddMMyy_XX.txt`

