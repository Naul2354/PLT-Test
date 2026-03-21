package pages;

import models.ChapterData;
import models.LessonData;
import utils.SeleniumHelper;

import org.openqa.selenium.By;
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

        WebElement btnThemBaiHoc = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_BAI_HOC));
        helper.safeClick(btnThemBaiHoc);
        helper.delay(1500);

        // Expand lesson panel
        List<WebElement> lessonPanels = driver.findElements(
            By.xpath("//button[contains(@class, 'v-expansion-panel-header')]//strong[contains(text(), 'Bài số " + lessonNumber + "')]")
        );

        if (!lessonPanels.isEmpty()) {
            WebElement panel = lessonPanels.get(0).findElement(By.xpath("./ancestor::button"));
            String ariaExpanded = panel.getAttribute("aria-expanded");

            if (!"true".equals(ariaExpanded)) {
                helper.safeClick(panel);
                helper.delay(1000);
            }
        }

        helper.delay(1000);

        // Fill lesson form
        List<WebElement> expandedPanels = driver.findElements(
            By.xpath("//button[@aria-expanded='true']/following-sibling::div[contains(@class, 'v-expansion-panel-content')]")
        );

        if (!expandedPanels.isEmpty()) {
            WebElement panel = expandedPanels.get(expandedPanels.size() - 1);
            helper.fillField(panel, By.name("title_course_item"), lesson.title);
            helper.fillField(panel, By.name("description_course_item"), lesson.description);
            System.out.println("Lesson form filled");
        }

        // Collapse lesson panel
        if (!lessonPanels.isEmpty()) {
            WebElement panel = lessonPanels.get(0).findElement(By.xpath("./ancestor::button"));
            helper.safeClick(panel);
            helper.delay(500);
        }
    }

    public void collapseAllPanels() {
        System.out.println("\nCollapsing all panels...");
        List<WebElement> expandedPanels = driver.findElements(
            By.xpath("//button[@aria-expanded='true']")
        );

        for (int i = expandedPanels.size() - 1; i >= 0; i--) {
            try {
                helper.safeClick(expandedPanels.get(i));
                helper.delay(500);
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
