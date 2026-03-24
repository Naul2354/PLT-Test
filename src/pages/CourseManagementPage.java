package pages;

import models.ChapterData;
import models.LessonData;
import utils.SeleniumHelper;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.List;
import java.util.Random;

public class CourseManagementPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final SeleniumHelper helper;

    // File paths
    private static final String IMAGE_FILE = System.getProperty("user.dir") + "/src/image/kotlin.png";

    // Locators - Add new course
    private static final By BTN_THEM_MOI_COURSE =
        By.xpath("//a[contains(@href, '/quan-tri-vien/khoa-hoc/them-moi')] | //button[.//span[contains(normalize-space(),'Thêm mới')]]");
    private static final By INPUT_TEN_KHOA_HOC =
        By.xpath("//input[@name='name']");
    private static final By INPUT_MO_TA =
        By.xpath("//textarea[@name='summary']");
    private static final By COURSE_FILE_INPUT =
        By.xpath("//input[@type='file']");
    private static final By BTN_THEM_HOC_VIEN =
        By.xpath("//button[.//span[contains(normalize-space(),'Thêm học viên')]]");
    private static final By BTN_THEM_LEARNER =
        By.xpath("//button[contains(@class,'green--text')]//span[contains(text(),'Thêm')]/parent::button");
    private static final By BTN_SUBMIT_COURSE =
        By.xpath("//button[.//span[contains(normalize-space(),'Thêm mới')]]");

    // Locators - Search & Update course
    private static final By COURSE_SEARCH_BOX =
        By.xpath("//div[contains(@class,'v-text-field--single-line')]//input[@type='text']");
    private static final By TAB_THONG_TIN_MON_HOC =
        By.xpath("//div[@role='tab' and contains(., 'Thông tin môn học')]");
    private static final By BTN_CAP_NHAT =
        By.xpath("//button[.//span[contains(normalize-space(),'Cập nhật')]]");
    private static final String JEDI_IMAGE_FILE = System.getProperty("user.dir") + "/src/image/golang.jpg";

    // Locators - Course content
    private static final By TAB_NOI_DUNG_MON_HOC =
        By.xpath("//div[@role='tab' and contains(., 'Nội dung môn học')]");
    private static final By BTN_THEM_CHUONG_HOC =
        By.xpath("//span[contains(text(),'Thêm chương học')]/parent::button");
    private static final By BTN_THEM_BAI_HOC =
        By.xpath("//span[contains(text(),'Thêm bài học')]/parent::button");
    private static final By BTN_LUU =
        By.xpath("//span[contains(text(),'Lưu')]/parent::button");
    private static final By BTN_OK =
        By.xpath("//span[contains(text(),'OK')]/parent::button | //button[contains(text(),'OK')]");
    private static final By BTN_THEM_TAI_LIEU =
        By.xpath("//button[.//span[contains(normalize-space(),'Thêm tài liệu học')]]");

    // Random image files for attachment
    private static final String[] ATTACHMENT_FILES = {
        "/src/image/kotlin.png", "/src/image/python.jpeg", "/src/image/js.jpg",
        "/src/image/sl.png", "/src/image/golang.jpg"
    };

    // Locators - Forum (Diễn đàn thảo luận)
    private static final By TAB_DIEN_DAN =
        By.xpath("//div[@role='tab' and contains(., 'Diễn đàn thảo luận')]");
    private static final By BTN_THEM_DIEN_DAN_MOI =
        By.xpath("//button[.//span[contains(normalize-space(),'Thêm diễn đàn mới')]]");
    private static final String DIALOG = "//div[contains(@class,'v-dialog__content') and contains(@class,'active')]";
    private static final By DIALOG_INPUT_NAME =
        By.xpath(DIALOG + "//label[contains(text(),'Tên') or contains(text(),'tên')]/ancestor::div[contains(@class,'v-input')]//input");
    private static final By DIALOG_INPUT_DESCRIPTION =
        By.xpath(DIALOG + "//label[contains(text(),'Mô tả') or contains(text(),'mô tả')]/ancestor::div[contains(@class,'v-input')]//textarea | " +
                 DIALOG + "//label[contains(text(),'Mô tả') or contains(text(),'mô tả')]/ancestor::div[contains(@class,'v-input')]//input");
    private static final By DIALOG_FILE_INPUT =
        By.xpath(DIALOG + "//input[@type='file']");
    private static final By DIALOG_BTN_THEM =
        By.xpath(DIALOG + "//button[.//span[contains(normalize-space(),'Thêm')]]");
    private static final By BTN_RELOAD =
        By.xpath("//button[contains(.,'Tải lại dữ liệu')] | //button[.//i[contains(@class,'mdi-refresh')]]");

    // Locators - Video Conference
    private static final By TAB_VIDEO_CONFERENCE =
        By.xpath("//div[@role='tab' and contains(., 'Video conference')]");
    private static final By BTN_THEM_VC_MOI =
        By.xpath("//button[.//span[contains(normalize-space(),'Thêm video conference mới')]]");

    public CourseManagementPage(WebDriver driver, WebDriverWait wait, SeleniumHelper helper) {
        this.driver = driver;
        this.wait = wait;
        this.helper = helper;
    }

    public void navigateToCourseManagement() {
        System.out.println("Navigating to course management...");
        driver.get("https://elearning.plt.pro.vn/quan-tri-vien/khoa-hoc");
        wait.until(ExpectedConditions.urlContains("/quan-tri-vien/khoa-hoc"));
        helper.delay();
    }

    public void clickAddNewCourse() {
        System.out.println("Clicking Add New Course...");
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_MOI_COURSE));
        helper.safeClick(btn);
        helper.delay(2000);
    }

    public void uploadCourseThumbnail() {
        System.out.println("Uploading course thumbnail: " + IMAGE_FILE);
        WebElement fileInput = driver.findElement(COURSE_FILE_INPUT);
        fileInput.sendKeys(IMAGE_FILE);
        helper.delay(1500);
    }

    public void fillCourseInfo(String title, String description) {
        System.out.println("Filling course info...");
        System.out.println("  Title: " + title);
        System.out.println("  Description: " + description);

        WebElement titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(INPUT_TEN_KHOA_HOC));
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = ''; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", titleInput);
        helper.delay(200);
        titleInput.sendKeys(title);
        helper.delay(300);

        WebElement descInput = wait.until(ExpectedConditions.visibilityOfElementLocated(INPUT_MO_TA));
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = ''; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", descInput);
        helper.delay(200);
        descInput.sendKeys(description);
        helper.delay(300);
    }

    public void addLearner(String emailOrCode) {
        System.out.println("Adding learner: " + emailOrCode);

        // Click "Thêm học viên" button to open the dialog
        WebElement btnThemHocVien = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_HOC_VIEN));
        helper.safeClick(btnThemHocVien);
        helper.delay(1500);

        // Find input inside the dialog
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath(DIALOG + "//input")
        ));
        searchInput.clear();
        searchInput.sendKeys(emailOrCode);
        helper.delay(1000);

        // Click the "Thêm" green button inside dialog
        WebElement btnThem = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(DIALOG + "//button[contains(@class,'green--text')]//span[contains(text(),'Thêm')]/parent::button")
        ));
        helper.safeClick(btnThem);
        helper.delay(1500);

        // Check for success notification
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Thêm học viên vào khóa học thành công')]")
            ));
            System.out.println("  -> Learner added successfully");
        } catch (Exception e) {
            System.out.println("  -> WARNING: Success notification not found for " + emailOrCode);
        }

        // Click OK on popup if present
        helper.clickOK();
        helper.delay(500);
    }

    public void submitNewCourse() {
        System.out.println("\nSubmitting new course...");
        WebElement btnSubmit = wait.until(ExpectedConditions.elementToBeClickable(BTN_SUBMIT_COURSE));
        helper.safeClick(btnSubmit);
        helper.delay(2000);

        // Verify success notification
        try {
            WebElement successNotif = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Thêm mới thành công')]")
            ));
            System.out.println("Course created successfully!");
        } catch (Exception e) {
            System.out.println("WARNING: Success notification not found");
        }

        helper.clickOK();
        helper.delay(1000);
    }

    // ==================== Verify course in list ====================

    public boolean verifyCourseInList(String expectedTitle) {
        System.out.println("\n--- Verify Course In List ---");
        System.out.println("[Expected] Course title: " + expectedTitle);
        helper.delay(2000);

        List<WebElement> courseLinks = driver.findElements(
            By.xpath("//tbody//tr//a[contains(@href, '/quan-tri-vien/khoa-hoc/quan-ly/')]")
        );

        for (WebElement link : courseLinks) {
            String actualTitle = link.getText().trim();
            if (actualTitle.contains(expectedTitle)) {
                System.out.println("[Actual]   Course title: " + actualTitle);
                System.out.println("[Result]   PASS - Course found in list");
                return true;
            }
        }

        System.out.println("[Actual]   Course NOT found in list");
        System.out.println("[Result]   FAIL - Course not found");
        return false;
    }

    // ==================== Search & Update Course ====================

    public void searchCourse(String courseName) {
        System.out.println("Searching for course: " + courseName);
        helper.delay(1000);

        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(COURSE_SEARCH_BOX));
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
            searchBox, courseName);
        helper.delay(2000);

        // Click the course link in results
        List<WebElement> courseLinks = driver.findElements(
            By.xpath("//tbody//tr//a[contains(@href, '/quan-tri-vien/khoa-hoc/quan-ly/')]")
        );

        for (WebElement link : courseLinks) {
            if (link.getText().trim().contains(courseName)) {
                System.out.println("Found & clicking: " + link.getText().trim());
                helper.safeClick(link);
                helper.delay(1500);
                return;
            }
        }

        // If partial match, click first result
        if (!courseLinks.isEmpty()) {
            System.out.println("Clicking first search result: " + courseLinks.get(0).getText().trim());
            helper.safeClick(courseLinks.get(0));
            helper.delay(1500);
        }
    }

    public void clickCourseInfoTab() {
        System.out.println("Opening course info tab (Thông tin môn học)...");
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(TAB_THONG_TIN_MON_HOC));
        helper.safeClick(tab);
        helper.delay(1500);
    }

    public void updateCourseInfo(String newTitle, String newDescription) {
        System.out.println("\n--- Updating Course Info ---");
        System.out.println("  New Title      : " + newTitle);
        System.out.println("  New Description: " + newDescription);

        // Clear and fill new title via JS (Vue.js input doesn't respond to .clear())
        WebElement titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(INPUT_TEN_KHOA_HOC));
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = ''; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", titleInput);
        helper.delay(300);
        titleInput.sendKeys(newTitle);
        helper.delay(300);

        // Clear and fill new description via JS
        WebElement descInput = wait.until(ExpectedConditions.visibilityOfElementLocated(INPUT_MO_TA));
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = ''; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", descInput);
        helper.delay(300);
        descInput.sendKeys(newDescription);
        helper.delay(300);

        // Upload new thumbnail (jedi.png)
        System.out.println("  Uploading new thumbnail: " + JEDI_IMAGE_FILE);
        WebElement fileInput = driver.findElement(COURSE_FILE_INPUT);
        fileInput.sendKeys(JEDI_IMAGE_FILE);
        helper.delay(1500);

        // Click "Cập nhật"
        WebElement btnCapNhat = wait.until(ExpectedConditions.elementToBeClickable(BTN_CAP_NHAT));
        helper.safeClick(btnCapNhat);
        helper.delay(2000);

        // Verify success notification
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Cập nhật dữ liệu thành công')]")
            ));
            System.out.println("  -> Course updated successfully");
        } catch (Exception e) {
            System.out.println("  -> WARNING: Update success notification not found");
        }

        helper.clickOK();
        helper.delay(1000);
    }

    public void verifyUpdatedCourseInList(String expectedTitle, String expectedDescription) {
        System.out.println("\n--- Verify Updated Course In List ---");

        // Search by new name using JS to trigger Vue reactivity
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(COURSE_SEARCH_BOX));
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
            searchBox, expectedTitle);
        helper.delay(2000);

        // Find the row in the table
        List<WebElement> rows = driver.findElements(By.xpath("//tbody//tr"));

        System.out.println("\n========================================");
        System.out.println("  COURSE UPDATE - Expected vs Actual");
        System.out.println("========================================");

        boolean found = false;
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 2) {
                String actualTitle = cells.get(0).getText().trim();
                String actualDesc = cells.get(1).getText().trim();

                if (actualTitle.contains(expectedTitle)) {
                    found = true;
                    System.out.println("[Expected] Title       : " + expectedTitle);
                    System.out.println("[Actual]   Title       : " + actualTitle);
                    System.out.println("[Result]   Title       : " + (actualTitle.contains(expectedTitle) ? "PASS" : "FAIL"));

                    System.out.println("[Expected] Description : " + expectedDescription);
                    System.out.println("[Actual]   Description : " + actualDesc);
                    boolean descMatch = actualDesc.contains(expectedDescription.substring(0, Math.min(30, expectedDescription.length())));
                    System.out.println("[Result]   Description : " + (descMatch ? "PASS" : "FAIL"));
                    break;
                }
            }
        }

        if (!found) {
            System.out.println("[Result]   Course NOT FOUND in list - FAIL");
        }

        System.out.println("========================================\n");
    }

    // ==================== Search & Delete Course ====================

    public int listLearnersAndPrint() {
        System.out.println("\n========================================");
        System.out.println("  LEARNERS IN COURSE - Dữ liệu thực tế");
        System.out.println("========================================");

        helper.delay(2000);
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        int count = 0;

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 2) {
                String name = cells.get(0).getText().trim();
                String email = cells.size() >= 3 ? cells.get(1).getText().trim() : "";
                System.out.println("  " + (count + 1) + ". " + name + " - " + email);
                count++;
            }
        }

        System.out.println("Total learners: " + count);
        System.out.println("========================================\n");
        return count;
    }

    public void deleteAllLearners() {
        System.out.println("Deleting all learners...");
        helper.delay(1000);

        List<WebElement> deleteBtns = driver.findElements(
            By.xpath("//table//tbody//tr//button[contains(@class,'red--text')]")
        );

        int total = deleteBtns.size();
        System.out.println("Found " + total + " learners to delete");

        for (int i = 0; i < total; i++) {
            List<WebElement> btns = driver.findElements(
                By.xpath("//table//tbody//tr//button[contains(@class,'red--text')]")
            );
            if (!btns.isEmpty()) {
                // Step 1: Click red X button
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btns.get(0));
                helper.delay(200);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btns.get(0));
                helper.delay(1500);

                // Step 2: Confirm dialog appears -> click "Xoá"
                WebElement btnXoa = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath(DIALOG + "//button[.//span[contains(normalize-space(),'Xoá')]]")
                ));
                helper.safeClick(btnXoa);
                helper.delay(1500);

                // Step 3: Success notification -> click OK
                helper.clickOK();
                helper.delay(500);

                System.out.println("  Learner " + (i + 1) + " deleted");
            }
        }

        System.out.println("All learners deleted");
    }

    public void verifyLearnersDeleted() {
        System.out.println("\n--- Verify Learners Deleted ---");
        clickReloadButton();

        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        // Check if table is empty or has "no data" message
        List<WebElement> noData = driver.findElements(
            By.xpath("//td[contains(text(),'No data')] | //td[contains(text(),'Không có dữ liệu')] | //tr[contains(@class,'v-data-table__empty')]")
        );

        int learnerCount = 0;
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 2) learnerCount++;
        }

        System.out.println("========================================");
        System.out.println("  LEARNERS AFTER DELETE");
        System.out.println("========================================");
        System.out.println("[Expected] Learners count : 0");
        System.out.println("[Actual]   Learners count : " + learnerCount);
        System.out.println("[Result]   " + (learnerCount == 0 || !noData.isEmpty() ? "PASS" : "FAIL"));
        System.out.println("========================================\n");
    }

    public void deleteCourseFromList(String courseTitle) {
        System.out.println("Deleting course: " + courseTitle);

        // Search the course
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(COURSE_SEARCH_BOX));
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
            searchBox, courseTitle);
        helper.delay(2000);

        // Find the row with this course and click the delete (X) button
        List<WebElement> rows = driver.findElements(By.xpath("//tbody//tr"));
        for (WebElement row : rows) {
            if (row.getText().contains(courseTitle)) {
                WebElement deleteBtn = row.findElement(
                    By.xpath(".//button[.//i[contains(@class,'mdi-close')]]")
                );
                // Step 1: Click red X button
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", deleteBtn);
                helper.delay(300);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteBtn);
                helper.delay(1500);

                // Step 2: Confirm dialog appears -> click "Xoá"
                WebElement btnXoa = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath(DIALOG + "//button[.//span[contains(normalize-space(),'Xoá')]]")
                ));
                helper.safeClick(btnXoa);
                helper.delay(1500);

                // Step 3: Success notification -> click OK
                helper.clickOK();
                helper.delay(500);

                System.out.println("Course deleted: " + courseTitle);
                return;
            }
        }

        System.out.println("WARNING: Course not found for deletion: " + courseTitle);
    }

    public void verifyCourseDeleted(String courseTitle) {
        System.out.println("\n--- Verify Course Deleted ---");
        helper.delay(1500);

        // Search again
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(COURSE_SEARCH_BOX));
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
            searchBox, courseTitle);
        helper.delay(2000);

        List<WebElement> courseLinks = driver.findElements(
            By.xpath("//tbody//tr//a[contains(text(),'" + courseTitle + "')]")
        );

        System.out.println("========================================");
        System.out.println("  COURSE DELETE - Verify");
        System.out.println("========================================");
        System.out.println("[Expected] Course '" + courseTitle + "' : NOT FOUND");
        System.out.println("[Actual]   Course found : " + (courseLinks.isEmpty() ? "NOT FOUND" : "STILL EXISTS"));
        System.out.println("[Result]   " + (courseLinks.isEmpty() ? "PASS" : "FAIL"));
        System.out.println("========================================\n");
    }

    // ==================== Forum (Diễn đàn thảo luận) ====================

    public void clickForumTab() {
        System.out.println("\nOpening forum tab...");
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(TAB_DIEN_DAN));
        helper.safeClick(tab);
        helper.delay(1500);
    }

    public void addNewForum(String name, String description) {
        System.out.println("\n--- Adding New Forum ---");
        System.out.println("  Name: " + name);
        System.out.println("  Description: " + description);

        // Click "Thêm diễn đàn mới"
        WebElement btnThemDienDan = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_DIEN_DAN_MOI));
        helper.safeClick(btnThemDienDan);
        helper.delay(1500);

        // Fill name in dialog
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(DIALOG_INPUT_NAME));
        nameInput.clear();
        nameInput.sendKeys(name);
        helper.delay(300);

        // Fill description in dialog
        WebElement descInput = wait.until(ExpectedConditions.visibilityOfElementLocated(DIALOG_INPUT_DESCRIPTION));
        descInput.clear();
        descInput.sendKeys(description);
        helper.delay(300);

        // Upload thumbnail
        System.out.println("  Uploading thumbnail: " + IMAGE_FILE);
        WebElement fileInput = driver.findElement(DIALOG_FILE_INPUT);
        fileInput.sendKeys(IMAGE_FILE);
        helper.delay(1500);

        // Click "Thêm"
        WebElement btnThem = wait.until(ExpectedConditions.elementToBeClickable(DIALOG_BTN_THEM));
        helper.safeClick(btnThem);
        helper.delay(2000);

        // Verify success notification
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'thành công')]")
            ));
            System.out.println("  -> Forum added successfully");
        } catch (Exception e) {
            System.out.println("  -> WARNING: Success notification not found");
        }

        helper.clickOK();
        helper.delay(1000);
    }

    private void clickReloadButton() {
        System.out.println("Clicking Tải lại dữ liệu...");
        helper.delay(1000);
        List<WebElement> reloadBtns = driver.findElements(BTN_RELOAD);
        if (!reloadBtns.isEmpty()) {
            WebElement btn = reloadBtns.get(reloadBtns.size() - 1);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
            helper.delay(300);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            helper.delay(2000);
        } else {
            System.out.println("  Reload button not found, refreshing page...");
            driver.navigate().refresh();
            helper.delay(2000);
        }
    }

    public void reloadAndVerifyForum(String expectedName, String expectedDescription) {
        System.out.println("\n--- Reload & Verify Forum ---");

        clickReloadButton();

        // Compare expected vs actual
        System.out.println("\n========================================");
        System.out.println("  FORUM - Expected vs Actual");
        System.out.println("========================================");
        System.out.println("[Expected] Name       : " + expectedName);
        System.out.println("[Expected] Description : " + expectedDescription);

        // Find forum name on page
        List<WebElement> forumElements = driver.findElements(
            By.xpath("//*[contains(text(),'" + expectedName + "')]")
        );

        if (!forumElements.isEmpty()) {
            String actualName = forumElements.get(0).getText().trim();
            System.out.println("[Actual]   Name       : " + actualName);
            boolean nameMatch = actualName.contains(expectedName);
            System.out.println("[Result]   Name       : " + (nameMatch ? "PASS" : "FAIL"));
        } else {
            System.out.println("[Actual]   Name       : NOT FOUND");
            System.out.println("[Result]   Name       : FAIL");
        }

        // Find forum description on page
        List<WebElement> descElements = driver.findElements(
            By.xpath("//*[contains(text(),'" + expectedDescription.substring(0, Math.min(30, expectedDescription.length())) + "')]")
        );

        if (!descElements.isEmpty()) {
            String actualDesc = descElements.get(0).getText().trim();
            System.out.println("[Actual]   Description : " + actualDesc);
            boolean descMatch = actualDesc.contains(expectedDescription.substring(0, Math.min(30, expectedDescription.length())));
            System.out.println("[Result]   Description : " + (descMatch ? "PASS" : "FAIL"));
        } else {
            System.out.println("[Actual]   Description : NOT FOUND");
            System.out.println("[Result]   Description : FAIL");
        }
        System.out.println("========================================\n");
    }

    // ==================== Video Conference ====================

    public void clickVideoConferenceTab() {
        System.out.println("\nOpening video conference tab...");
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(TAB_VIDEO_CONFERENCE));
        helper.safeClick(tab);
        helper.delay(1500);
    }

    public void addVideoConference(String youtubeLink, String description, String meetingLink) {
        System.out.println("\n--- Adding Video Conference ---");
        System.out.println("  YouTube  : " + youtubeLink);
        System.out.println("  Description: " + description);
        System.out.println("  Meeting  : " + meetingLink);

        // Click "Thêm video conference mới" button
        WebElement btnThemVC = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_VC_MOI));
        helper.safeClick(btnThemVC);
        helper.delay(1500);

        // Fill inputs inside the dialog
        List<WebElement> dialogInputs = driver.findElements(
            By.xpath(DIALOG + "//input")
        );
        List<WebElement> dialogTextareas = driver.findElements(
            By.xpath(DIALOG + "//textarea")
        );

        // YouTube link - first input
        if (dialogInputs.size() >= 1) {
            dialogInputs.get(0).clear();
            dialogInputs.get(0).sendKeys(youtubeLink);
            helper.delay(300);
        }

        // Description - textarea or second input
        if (!dialogTextareas.isEmpty()) {
            dialogTextareas.get(0).clear();
            dialogTextareas.get(0).sendKeys(description);
            helper.delay(300);
        } else if (dialogInputs.size() >= 2) {
            dialogInputs.get(1).clear();
            dialogInputs.get(1).sendKeys(description);
            helper.delay(300);
        }

        // Meeting link - last input
        if (dialogInputs.size() >= 2) {
            WebElement meetInput = dialogInputs.get(dialogInputs.size() - 1);
            meetInput.clear();
            meetInput.sendKeys(meetingLink);
            helper.delay(300);
        }

        // Click "Thêm" inside dialog
        WebElement btnThem = wait.until(ExpectedConditions.elementToBeClickable(DIALOG_BTN_THEM));
        helper.safeClick(btnThem);
        helper.delay(2000);

        // Verify success notification
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'thành công')]")
            ));
            System.out.println("  -> Video conference added successfully");
        } catch (Exception e) {
            System.out.println("  -> WARNING: Success notification not found");
        }

        helper.clickOK();
        helper.delay(1000);
    }

    public void reloadAndVerifyVideoConference(String expectedYoutubeLink, String expectedDescription, String expectedMeetingLink) {
        System.out.println("\n--- Reload & Verify Video Conference ---");

        clickReloadButton();

        helper.delay(1500);

        System.out.println("\n========================================");
        System.out.println("  VIDEO CONFERENCE - Expected vs Actual");
        System.out.println("========================================");
        System.out.println("[Expected] YouTube Link : " + expectedYoutubeLink);
        System.out.println("[Expected] Description  : " + expectedDescription);
        System.out.println("[Expected] Meeting Link : " + expectedMeetingLink);

        // Verify YouTube link
        List<WebElement> ytElements = driver.findElements(
            By.xpath("//*[contains(@value,'" + expectedYoutubeLink + "') or contains(text(),'" + expectedYoutubeLink + "') or contains(@href,'" + expectedYoutubeLink + "')]")
        );
        if (!ytElements.isEmpty()) {
            String actual = ytElements.get(0).getAttribute("value") != null ?
                ytElements.get(0).getAttribute("value") : ytElements.get(0).getText();
            System.out.println("[Actual]   YouTube Link : " + actual.trim());
            System.out.println("[Result]   YouTube Link : PASS");
        } else {
            System.out.println("[Actual]   YouTube Link : NOT FOUND");
            System.out.println("[Result]   YouTube Link : FAIL");
        }

        // Verify description
        List<WebElement> descElements = driver.findElements(
            By.xpath("//*[contains(@value,'" + expectedDescription.substring(0, Math.min(20, expectedDescription.length())) + "') or " +
                     "contains(text(),'" + expectedDescription.substring(0, Math.min(20, expectedDescription.length())) + "')]")
        );
        if (!descElements.isEmpty()) {
            String actual = descElements.get(0).getAttribute("value") != null ?
                descElements.get(0).getAttribute("value") : descElements.get(0).getText();
            System.out.println("[Actual]   Description  : " + actual.trim());
            System.out.println("[Result]   Description  : PASS");
        } else {
            System.out.println("[Actual]   Description  : NOT FOUND");
            System.out.println("[Result]   Description  : FAIL");
        }

        // Verify meeting link
        List<WebElement> meetElements = driver.findElements(
            By.xpath("//*[contains(@value,'" + expectedMeetingLink + "') or contains(text(),'" + expectedMeetingLink + "') or contains(@href,'" + expectedMeetingLink + "')]")
        );
        if (!meetElements.isEmpty()) {
            String actual = meetElements.get(0).getAttribute("value") != null ?
                meetElements.get(0).getAttribute("value") : meetElements.get(0).getText();
            System.out.println("[Actual]   Meeting Link : " + actual.trim());
            System.out.println("[Result]   Meeting Link : PASS");
        } else {
            System.out.println("[Actual]   Meeting Link : NOT FOUND");
            System.out.println("[Result]   Meeting Link : FAIL");
        }
        System.out.println("========================================\n");
    }

    public void searchAndSelectCourse(String courseTitle) {
        System.out.println("Searching for course: " + courseTitle);
        helper.delay(2000);

        List<WebElement> courseLinks = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//tbody//tr//a[contains(@href, '/quan-tri-vien/khoa-hoc/quan-ly/')]")
            )
        );

        for (WebElement link : courseLinks) {
            if (link.getText().trim().contains(courseTitle)) {
                System.out.println("Found course: " + courseTitle);
                helper.safeClick(link);
                helper.delay(1500);
                return;
            }
        }

        // If not found by exact match, click the first one (newest)
        System.out.println("Course not found by name, selecting first course...");
        helper.safeClick(courseLinks.get(0));
        helper.delay(1500);
    }

    public String selectRandomCourse() {
        System.out.println("Selecting random course...");
        helper.delay(2000);

        List<WebElement> courseLinks = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//tbody//tr//a[contains(@href, '/quan-tri-vien/khoa-hoc/quan-ly/')]")
            )
        );

        Random random = new Random();
        WebElement selectedCourse = courseLinks.get(random.nextInt(courseLinks.size()));
        String courseName = selectedCourse.getText().trim();

        System.out.println("Selected: " + courseName);
        helper.safeClick(selectedCourse);
        helper.delay(1500);

        return courseName;
    }

    public void clickCourseContentTab() {
        System.out.println("Opening course content tab...");
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(TAB_NOI_DUNG_MON_HOC));
        helper.safeClick(tab);
        helper.delay(1000);
    }

    public void addChapter(ChapterData chapter) {
        System.out.println("\nAdding chapter: " + chapter.title);

        WebElement btnThemChuong = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_CHUONG_HOC));
        helper.safeClick(btnThemChuong);
        helper.delay(1500);

        // Expand the new chapter panel
        List<WebElement> expansionPanels = driver.findElements(
            By.xpath("//button[contains(@class, 'v-expansion-panel-header')]")
        );

        if (!expansionPanels.isEmpty()) {
            WebElement newPanel = expansionPanels.get(expansionPanels.size() - 1);
            String ariaExpanded = newPanel.getAttribute("aria-expanded");

            if (!"true".equals(ariaExpanded)) {
                helper.safeClick(newPanel);
                helper.delay(1500);
            }
        }

        // Fill chapter form
        List<WebElement> expandedPanels = driver.findElements(
            By.xpath("//button[@aria-expanded='true']/following-sibling::div[contains(@class, 'v-expansion-panel-content')]")
        );

        if (!expandedPanels.isEmpty()) {
            WebElement panel = expandedPanels.get(expandedPanels.size() - 1);
            helper.fillField(panel, By.name("title_course_item"), chapter.title);
            helper.fillField(panel, By.name("description_course_item"), chapter.description);
            System.out.println("Chapter form filled");
        }

        helper.delay(500);
    }

    public void addLesson(LessonData lesson, int lessonNumber) {
        System.out.println("Adding lesson " + lessonNumber + ": " + lesson.title);

        // Make sure the chapter panel is expanded and find "Thêm bài học" inside it
        List<WebElement> expandedContents = driver.findElements(
            By.xpath("//button[@aria-expanded='true']/following-sibling::div[contains(@class, 'v-expansion-panel-content')]")
        );

        WebElement btnThemBaiHoc = null;
        if (!expandedContents.isEmpty()) {
            // Look for "Thêm bài học" inside the last expanded panel
            WebElement expandedPanel = expandedContents.get(expandedContents.size() - 1);
            List<WebElement> btns = expandedPanel.findElements(
                By.xpath(".//button[.//span[contains(text(),'Thêm bài học')]]")
            );
            if (!btns.isEmpty()) {
                btnThemBaiHoc = btns.get(btns.size() - 1);
            }
        }

        // Fallback to global search
        if (btnThemBaiHoc == null) {
            btnThemBaiHoc = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_BAI_HOC));
        }

        helper.safeClick(btnThemBaiHoc);
        helper.delay(2000);

        // Find the last "Bài số X" header on the page (the one just created)
        List<WebElement> lessonHeaders = driver.findElements(
            By.xpath("//button[contains(@class, 'v-expansion-panel-header')]//strong[contains(text(), 'Bài số')]")
        );

        if (!lessonHeaders.isEmpty()) {
            WebElement lastHeader = lessonHeaders.get(lessonHeaders.size() - 1);
            WebElement headerBtn = lastHeader.findElement(By.xpath("./ancestor::button"));

            // Scroll to it
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", headerBtn);
            helper.delay(500);

            // Expand if not already
            String ariaExpanded = headerBtn.getAttribute("aria-expanded");
            if (!"true".equals(ariaExpanded)) {
                helper.safeClick(headerBtn);
                helper.delay(1500);
            }

            // Find the content wrap (sibling after the header button)
            WebElement lessonContent = headerBtn.findElement(
                By.xpath("./following-sibling::div[contains(@class, 'v-expansion-panel-content')]")
            );

            // Wait for title field to be visible
            wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(
                lessonContent, By.name("title_course_item")
            ));

            helper.fillField(lessonContent, By.name("title_course_item"), lesson.title);
            helper.fillField(lessonContent, By.name("description_course_item"), lesson.description);
            System.out.println("Lesson form filled");
            // Keep lesson expanded for adding materials
        }
    }

    // ==================== Learning Materials (Tài liệu học) ====================

    public void addVideoMaterial(String materialName, String youtubeUrl) {
        System.out.println("  Adding video material: " + materialName);

        // Find "Thêm tài liệu học" button inside expanded lesson panel
        List<WebElement> taiLieuBtns = driver.findElements(BTN_THEM_TAI_LIEU);
        WebElement btnThemTaiLieu;
        if (!taiLieuBtns.isEmpty()) {
            btnThemTaiLieu = taiLieuBtns.get(taiLieuBtns.size() - 1);
        } else {
            btnThemTaiLieu = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_TAI_LIEU));
        }
        helper.safeClick(btnThemTaiLieu);
        helper.delay(1500);

        // Select "Xem video" radio
        WebElement videoRadio = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(DIALOG + "//label[contains(text(),'Xem video')]")
        ));
        helper.safeClick(videoRadio);
        helper.delay(500);

        // Fill material name
        List<WebElement> inputs = driver.findElements(By.xpath(DIALOG + "//input[@type='text']"));
        if (inputs.size() >= 1) {
            inputs.get(0).clear();
            inputs.get(0).sendKeys(materialName);
            helper.delay(300);
        }

        // Fill YouTube URL
        if (inputs.size() >= 2) {
            inputs.get(1).clear();
            inputs.get(1).sendKeys(youtubeUrl);
            helper.delay(300);
        }

        // Click "Thêm"
        WebElement btnThem = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(DIALOG + "//button[contains(@class,'green--text')]//span[contains(text(),'Thêm')]/parent::button")
        ));
        helper.safeClick(btnThem);
        helper.delay(1500);

        helper.clickOK();
        helper.delay(500);

        // Verify material added in UI
        List<WebElement> materialItems = driver.findElements(
            By.xpath("//div[contains(@class,'v-list-item')]//div[contains(text(),'" + materialName + "')]")
        );
        if (!materialItems.isEmpty()) {
            System.out.println("    -> Video material verified in UI: " + materialName);
        } else {
            System.out.println("    -> WARNING: Video material not found in UI");
        }
    }

    public void addAttachmentMaterial(String materialName) {
        System.out.println("  Adding attachment material: " + materialName);

        // Find "Thêm tài liệu học" button inside expanded lesson panel
        List<WebElement> taiLieuBtns = driver.findElements(BTN_THEM_TAI_LIEU);
        WebElement btnThemTaiLieu;
        if (!taiLieuBtns.isEmpty()) {
            btnThemTaiLieu = taiLieuBtns.get(taiLieuBtns.size() - 1);
        } else {
            btnThemTaiLieu = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_TAI_LIEU));
        }
        helper.safeClick(btnThemTaiLieu);
        helper.delay(1500);

        // Select "File tải về" radio
        WebElement fileRadio = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(DIALOG + "//label[contains(text(),'File tải về')]")
        ));
        helper.safeClick(fileRadio);
        helper.delay(500);

        // Fill material name
        List<WebElement> inputs = driver.findElements(By.xpath(DIALOG + "//input[@type='text']"));
        if (!inputs.isEmpty()) {
            inputs.get(0).clear();
            inputs.get(0).sendKeys(materialName);
            helper.delay(300);
        }

        // Upload random file
        Random rand = new Random();
        String filePath = System.getProperty("user.dir") + ATTACHMENT_FILES[rand.nextInt(ATTACHMENT_FILES.length)];
        System.out.println("    Uploading file: " + filePath);
        WebElement fileInput = driver.findElement(By.xpath(DIALOG + "//input[@type='file']"));
        fileInput.sendKeys(filePath);
        helper.delay(1500);

        // Click "Thêm"
        WebElement btnThem = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(DIALOG + "//button[contains(@class,'green--text')]//span[contains(text(),'Thêm')]/parent::button")
        ));
        helper.safeClick(btnThem);
        helper.delay(1500);

        helper.clickOK();
        helper.delay(500);

        // Verify material added in UI
        List<WebElement> materialItems = driver.findElements(
            By.xpath("//div[contains(@class,'v-list-item')]//div[contains(text(),'" + materialName + "')]")
        );
        if (!materialItems.isEmpty()) {
            System.out.println("    -> Attachment material verified in UI: " + materialName);
        } else {
            System.out.println("    -> WARNING: Attachment material not found in UI");
        }
    }

    public void expandLessonPanel(int lessonNumber) {
        System.out.println("\nExpanding lesson " + lessonNumber + "...");
        List<WebElement> lessonPanels = driver.findElements(
            By.xpath("//button[contains(@class, 'v-expansion-panel-header')]//strong[contains(text(), 'Bài số " + lessonNumber + "')]")
        );

        if (!lessonPanels.isEmpty()) {
            // Use last match (for the current chapter, not a previous one)
            WebElement panel = lessonPanels.get(lessonPanels.size() - 1).findElement(By.xpath("./ancestor::button"));
            String ariaExpanded = panel.getAttribute("aria-expanded");
            if (!"true".equals(ariaExpanded)) {
                helper.safeClick(panel);
                helper.delay(1000);
            }
        }
    }

    public void collapseLastExpandedLesson() {
        List<WebElement> expandedLessons = driver.findElements(
            By.xpath("//button[contains(@class,'v-expansion-panel-header') and @aria-expanded='true' and .//strong[contains(text(),'Bài số')]]")
        );
        if (!expandedLessons.isEmpty()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", expandedLessons.get(expandedLessons.size() - 1));
            helper.delay(300);
        }
    }

    public void collapseLessonPanel(int lessonNumber) {
        System.out.println("Collapsing lesson " + lessonNumber + "...");
        List<WebElement> lessonPanels = driver.findElements(
            By.xpath("//button[contains(@class, 'v-expansion-panel-header')]//strong[contains(text(), 'Bài số " + lessonNumber + "')]")
        );

        if (!lessonPanels.isEmpty()) {
            WebElement panel = lessonPanels.get(lessonPanels.size() - 1).findElement(By.xpath("./ancestor::button"));
            String ariaExpanded = panel.getAttribute("aria-expanded");
            if ("true".equals(ariaExpanded)) {
                helper.safeClick(panel);
                helper.delay(500);
            }
        }
    }

    public void collapseAllPanels() {
        System.out.println("Collapsing all panels...");
        List<WebElement> expandedPanels = driver.findElements(
            By.xpath("//button[@aria-expanded='true']")
        );

        for (int i = expandedPanels.size() - 1; i >= 0; i--) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", expandedPanels.get(i));
                helper.delay(200);
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    public void saveChapter() {
        System.out.println("\nSaving chapter...");
        WebElement btnLuu = wait.until(ExpectedConditions.elementToBeClickable(BTN_LUU));
        helper.safeClick(btnLuu);
        helper.delay(2000);

        List<WebElement> successNotif = driver.findElements(
            By.xpath("//*[contains(text(), 'Đã lưu') and contains(text(), 'thành công')]")
        );

        if (!successNotif.isEmpty() && successNotif.get(0).isDisplayed()) {
            System.out.println("Save successful!");
        }

        try {
            WebElement btnOK = wait.until(ExpectedConditions.elementToBeClickable(BTN_OK));
            helper.safeClick(btnOK);
        } catch (Exception e) {
            // OK button not found or already closed
        }

        helper.delay(2000);
    }

    public void verifyChapterExists(ChapterData chapter) {
        System.out.println("\nVerifying chapter exists...");
        helper.delay(1000);

        List<WebElement> chapterElements = driver.findElements(
            By.xpath("//div[contains(@class, 'v-expansion-panel')]//div[contains(text(), '" + chapter.title + "')]")
        );

        Assert.assertFalse(chapterElements.isEmpty(), "Chapter not found: " + chapter.title);
        System.out.println("Chapter verified: " + chapter.title);
    }

    public void verifyLessonsExist(List<LessonData> lessons) {
        System.out.println("Verifying lessons exist...");
        helper.delay(1000);

        for (LessonData lesson : lessons) {
            List<WebElement> lessonElements = driver.findElements(
                By.xpath("//div[contains(@class, 'v-expansion-panel')]//div[contains(text(), '" + lesson.title + "')]")
            );

            Assert.assertFalse(lessonElements.isEmpty(), "Lesson not found: " + lesson.title);
            System.out.println("Lesson verified: " + lesson.title);
        }
    }
}
