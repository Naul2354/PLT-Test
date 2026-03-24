# PROJECT REQUIREMENTS: AUTOMATED TESTING PROGRAM FOR POL WEBSITE

Students are required to build an automated testing program for the POL Website using:

- Selenium + TestNG + Java
- Apply the Page Object Model (POM) design pattern
- Configure test scripts to run sequentially based on each criterion
- Each criterion is converted into a score

> The more testing criteria, well-structured, logical, and creative test scenarios you build, the higher your score.

---

## 1. Basic Functions (CRUD)

- [x] Successful login
- [x] Successfully add a new student
- [x] Successfully search and update student information
- [x] Successfully delete a student
- [x] Successfully add a new course
- [x] Successfully search and update course
- [x] Successfully search and delete course
- [x] Access a course and add learners
- [x] Access a course and add course content (videos, documents, exercises)
- [x] Access a course and add a forum
- [x] Access a course and add a video conference
- [x] Successfully add a new assignment
- [ ] Successfully update an assignment
- [ ] Successfully delete an assignment

**General Requirements:** Each function must include:
- Expected Result
- Actual Result
- Conclusion: Pass / Fail

---

## 2. UI and List Validation

### 2.1. Left Menu Validation

- [ ] The left menu displays all items in the correct order:
  - Home
  - Student Management
  - Course Management
  - Assignment Management
  - Logout
  - Go to User Page
- [ ] Menu list retrieved from the actual HTML interface
- [ ] Conclusion: Correct and complete → Pass / Missing or incorrect order → Fail

### 2.2. List and Data Column Validation

- [ ] **Student List** — Expected columns: Student ID, Middle Name, First Name, Phone Number, Email, Date of Birth, Gender, Address, Last Updated Date
- [ ] **Course List** — Expected columns: Title, Description, Last Updated Date
- [ ] **Assignment List** — Expected columns: Title, Number of Questions, Creator, Created Date, Last Updated Date
- [ ] Retrieve all `<th>` tags from the HTML table for each list

### 2.3. Sorting Validation

- [ ] Test sorting in Student Management
- [ ] Test sorting in Course Management
- [ ] Test sorting in Assignment Management
- [ ] Compare the data order before and after sorting → Pass / Fail

---

## 3. Image and Display Testing

- [ ] System logo validation (logo matches standard image, use image comparison library)
- [ ] Button icon validation (add, delete, edit) — icons display correctly, no missing or hidden icons
- [ ] Validate button color according to standard (dark blue)
- [ ] Validate zoom in / zoom out
- [ ] Validate responsive layout
- [ ] Validate font style consistency across the website
