package User;

import java.io.FileReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AddingCourseTest {

    private WebDriver driver;
    private WebDriverWait wait;

    // Data Models
    private static class ChapterData {
        String title;
        String description;

        ChapterData(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }

    private static class LessonData {
        String title;
        String description;

        LessonData(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }

    // Locators
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

    // Utility Methods
    private void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void delay() {
        delay(800);
    }

    private void safeClick(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        delay(300);
        js.executeScript("arguments[0].click();", element);
        delay();
    }

    private void fillField(WebElement parent, By locator, String value) {
        List<WebElement> elements = parent.findElements(locator);

        if (elements.isEmpty()) {
            throw new RuntimeException("Field not found: " + locator);
        }

        // Get the last visible element (newly created field)
        WebElement element = null;
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (elements.get(i).isDisplayed()) {
                element = elements.get(i);
                break;
            }
        }

        if (element == null) {
            throw new RuntimeException("No visible field found");
        }

        // Scroll and clear
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        delay(200);
        element.clear();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        delay(100);

        // Fill value
        element.sendKeys(value);
        delay(300);
    }

    // Load data from JSON
    private List<ChapterData> loadChaptersFromJSON() throws Exception {
        List<ChapterData> chapters = new ArrayList<>();
        String filePath = System.getProperty("user.dir") + "/src/resources/chapters.json";

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filePath));

        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            chapters.add(new ChapterData(
                (String) jsonObj.get("title"),
                (String) jsonObj.get("description")
            ));
        }

        System.out.println("Loaded " + chapters.size() + " chapters from JSON");
        return chapters;
    }

    private List<LessonData> loadLessonsFromJSON() throws Exception {
        List<LessonData> lessons = new ArrayList<>();
        String filePath = System.getProperty("user.dir") + "/src/resources/lessons.json";

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filePath));

        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            lessons.add(new LessonData(
                (String) jsonObj.get("title"),
                (String) jsonObj.get("description")
            ));
        }

        System.out.println("Loaded " + lessons.size() + " lessons from JSON");
        return lessons;
    }

    // Login
    private void loginAsAdmin() {
        System.out.println("Logging in as admin...");
        driver.get("https://elearning.plt.pro.vn/dang-nhap?redirect=%2Ftrang-chu");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-10")))
            .sendKeys("test.pltsolutions@gmail.com");
        driver.findElement(By.id("input-13")).sendKeys("plt@intern_051224");
        driver.findElement(By.xpath("//span[contains(text(),'Đăng nhập')]")).click();

        wait.until(ExpectedConditions.urlContains("/trang-chu"));
        delay();
    }

    // Navigate to course management
    private void navigateToCourseManagement() {
        System.out.println("Navigating to course management...");
        driver.get("https://elearning.plt.pro.vn/quan-tri-vien/khoa-hoc");
        wait.until(ExpectedConditions.urlContains("/quan-tri-vien/khoa-hoc"));
        delay();
    }

    // Select random course
    private String selectRandomCourse() {
        System.out.println("Selecting random course...");
        delay(2000);

        List<WebElement> courseLinks = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//tbody//tr//a[contains(@href, '/quan-tri-vien/khoa-hoc/quan-ly/')]")
            )
        );

        Random random = new Random();
        WebElement selectedCourse = courseLinks.get(random.nextInt(courseLinks.size()));
        String courseName = selectedCourse.getText().trim();

        System.out.println("Selected: " + courseName);
        safeClick(selectedCourse);
        delay(1500);

        return courseName;
    }

    // Click course content tab
    private void clickNoiDungMonHocTab() {
        System.out.println("Opening course content tab...");
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(TAB_NOI_DUNG_MON_HOC));
        safeClick(tab);
        delay(1000);
    }

    // Add chapter
    private void addChapter(ChapterData chapter) {
        System.out.println("\nAdding chapter: " + chapter.title);

        // Click add chapter button
        WebElement btnThemChuong = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_CHUONG_HOC));
        safeClick(btnThemChuong);
        delay(1500);

        // Expand the new chapter panel
        List<WebElement> expansionPanels = driver.findElements(
            By.xpath("//button[contains(@class, 'v-expansion-panel-header')]")
        );

        if (!expansionPanels.isEmpty()) {
            WebElement newPanel = expansionPanels.get(expansionPanels.size() - 1);
            String ariaExpanded = newPanel.getAttribute("aria-expanded");

            if (!"true".equals(ariaExpanded)) {
                safeClick(newPanel);
                delay(1500);
            }
        }

        // Fill chapter form
        List<WebElement> expandedPanels = driver.findElements(
            By.xpath("//button[@aria-expanded='true']/following-sibling::div[contains(@class, 'v-expansion-panel-content')]")
        );

        if (!expandedPanels.isEmpty()) {
            WebElement panel = expandedPanels.get(expandedPanels.size() - 1);
            fillField(panel, By.name("title_course_item"), chapter.title);
            fillField(panel, By.name("description_course_item"), chapter.description);
            System.out.println("Chapter form filled");
        }

        delay(500);
    }

    // Add lesson
    private void addLesson(LessonData lesson, int lessonNumber) {
        System.out.println("Adding lesson " + lessonNumber + ": " + lesson.title);

        // Click add lesson button
        WebElement btnThemBaiHoc = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_BAI_HOC));
        safeClick(btnThemBaiHoc);
        delay(1500);

        // Expand lesson panel
        List<WebElement> lessonPanels = driver.findElements(
            By.xpath("//button[contains(@class, 'v-expansion-panel-header')]//strong[contains(text(), 'Bài số " + lessonNumber + "')]")
        );

        if (!lessonPanels.isEmpty()) {
            WebElement panel = lessonPanels.get(0).findElement(By.xpath("./ancestor::button"));
            String ariaExpanded = panel.getAttribute("aria-expanded");

            if (!"true".equals(ariaExpanded)) {
                safeClick(panel);
                delay(1000);
            }
        }

        delay(1000);

        // Fill lesson form
        List<WebElement> expandedPanels = driver.findElements(
            By.xpath("//button[@aria-expanded='true']/following-sibling::div[contains(@class, 'v-expansion-panel-content')]")
        );

        if (!expandedPanels.isEmpty()) {
            WebElement panel = expandedPanels.get(expandedPanels.size() - 1);
            fillField(panel, By.name("title_course_item"), lesson.title);
            fillField(panel, By.name("description_course_item"), lesson.description);
            System.out.println("Lesson form filled");
        }

        // Collapse lesson panel
        if (!lessonPanels.isEmpty()) {
            WebElement panel = lessonPanels.get(0).findElement(By.xpath("./ancestor::button"));
            safeClick(panel);
            delay(500);
        }
    }

    // Collapse all panels
    private void collapseAllPanels() {
        System.out.println("\nCollapsing all panels...");
        List<WebElement> expandedPanels = driver.findElements(
            By.xpath("//button[@aria-expanded='true']")
        );

        for (int i = expandedPanels.size() - 1; i >= 0; i--) {
            try {
                safeClick(expandedPanels.get(i));
                delay(500);
            } catch (Exception e) {
                // Ignore if panel cannot be collapsed
            }
        }
    }

    // Save chapter
    private void saveChapter() {
        System.out.println("\nSaving chapter...");
        WebElement btnLuu = wait.until(ExpectedConditions.elementToBeClickable(BTN_LUU));
        safeClick(btnLuu);
        delay(2000);

        // Check for success notification
        List<WebElement> successNotif = driver.findElements(
            By.xpath("//*[contains(text(), 'Đã lưu') and contains(text(), 'thành công')]")
        );

        if (!successNotif.isEmpty() && successNotif.get(0).isDisplayed()) {
            System.out.println("Save successful!");
        }

        // Click OK button
        try {
            WebElement btnOK = wait.until(ExpectedConditions.elementToBeClickable(BTN_OK));
            safeClick(btnOK);
        } catch (Exception e) {
            // OK button not found or already closed
        }

        delay(2000);
    }

    // Verify chapter exists
    private void verifyChapterExists(ChapterData chapter) {
        System.out.println("\nVerifying chapter exists...");
        delay(1000);

        List<WebElement> chapterElements = driver.findElements(
            By.xpath("//div[contains(@class, 'v-expansion-panel')]//div[contains(text(), '" + chapter.title + "')]")
        );

        Assert.assertFalse(chapterElements.isEmpty(), "Chapter not found: " + chapter.title);
        System.out.println("Chapter verified: " + chapter.title);
    }

    // Verify lessons exist
    private void verifyLessonsExist(List<LessonData> lessons) {
        System.out.println("Verifying lessons exist...");
        delay(1000);

        for (LessonData lesson : lessons) {
            List<WebElement> lessonElements = driver.findElements(
                By.xpath("//div[contains(@class, 'v-expansion-panel')]//div[contains(text(), '" + lesson.title + "')]")
            );

            Assert.assertFalse(lessonElements.isEmpty(), "Lesson not found: " + lesson.title);
            System.out.println("Lesson verified: " + lesson.title);
        }
    }

    // Main Test
    @Test
    public void testAddingCourseContent() throws Exception {
        System.out.println("========================================");
        System.out.println("TEST: Adding Course Content");
        System.out.println("========================================\n");

        // Load test data
        List<ChapterData> allChapters = loadChaptersFromJSON();
        List<LessonData> allLessons = loadLessonsFromJSON();

        // Select random chapter
        Random random = new Random();
        ChapterData selectedChapter = allChapters.get(random.nextInt(allChapters.size()));
        System.out.println("Selected chapter: " + selectedChapter.title);

        // Select 2 random lessons
        List<LessonData> selectedLessons = new ArrayList<>();
        List<Integer> usedIndices = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            int randomIndex;
            do {
                randomIndex = random.nextInt(allLessons.size());
            } while (usedIndices.contains(randomIndex));

            usedIndices.add(randomIndex);
            selectedLessons.add(allLessons.get(randomIndex));
        }

        System.out.println("Selected " + selectedLessons.size() + " lessons\n");

        // Initialize browser with Docker-safe options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            // Execute test steps
            loginAsAdmin();
            navigateToCourseManagement();
            String courseName = selectRandomCourse();
            clickNoiDungMonHocTab();

            addChapter(selectedChapter);

            for (int i = 0; i < selectedLessons.size(); i++) {
                addLesson(selectedLessons.get(i), i + 1);
            }

            collapseAllPanels();
            saveChapter();

            verifyChapterExists(selectedChapter);
            verifyLessonsExist(selectedLessons);

            // Test summary
            System.out.println("\n========================================");
            System.out.println("TEST PASSED");
            System.out.println("========================================");
            System.out.println("Course: " + courseName);
            System.out.println("Chapter added: " + selectedChapter.title);
            System.out.println("Lessons added: " + selectedLessons.size());
            System.out.println("========================================\n");

        } catch (Exception e) {
            System.out.println("\nTEST FAILED: " + e.getMessage());
            throw e;
        } finally {
            if (driver != null) {
                delay(3000);
                driver.quit();
            }
        }
    }
}
