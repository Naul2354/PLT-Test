package pages;

import models.HomeworkFailCase;
import utils.SeleniumHelper;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.List;

/**
 * Negative-test page for adding a homework with missing required fields:
 * - Missing homework name
 * - Missing thumbnail
 * - Question without content
 * - Question with incomplete answers
 *
 * Verifies the SweetAlert2 popup "Thông tin nhập thiếu" appears.
 */
public class HomeworkAddFailPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final SeleniumHelper helper;

    private static final String IMAGE_FILE = System.getProperty("user.dir") + "/src/image/js.jpg";

    // Locators (mirror HomeworkManagementPage)
    private static final By BTN_THEM_MOI =
        By.cssSelector("#v-main-app > div > div > div > div.v-data-table > header > div > a");
    private static final By INPUT_TEN_BAI_TAP =
        By.xpath("//label[contains(text(),'Tên bài tập')]/ancestor::div[contains(@class,'v-input')]//input");
    private static final By FORM_FILE_INPUT =
        By.cssSelector("#formUpdateAddNew input[type='file']");
    private static final By BTN_THEM_CAU_HOI =
        By.xpath("//button[.//span[contains(normalize-space(),'Thêm câu hỏi')]]");
    private static final By BTN_SAVE =
        By.xpath("//button[.//i[contains(@class,'mdi-content-save')]]");

    // SweetAlert2 error popup locators
    private static final By ERROR_POPUP =
        By.xpath("//div[contains(@class,'swal2-popup') and contains(@class,'swal2-icon-error')]");
    private static final By POPUP_TITLE =
        By.xpath("//div[contains(@class,'swal2-popup')]//h2[contains(@class,'swal2-title')]");
    private static final By POPUP_BODY =
        By.xpath("//div[contains(@class,'swal2-popup')]//div[contains(@class,'swal2-html-container')]");

    public HomeworkAddFailPage(WebDriver driver, WebDriverWait wait, SeleniumHelper helper) {
        this.driver = driver;
        this.wait = wait;
        this.helper = helper;
    }

    public void navigateToHomeworkManagement() {
        System.out.println("Navigating to homework management...");
        driver.get("https://elearning.plt.pro.vn/quan-tri-vien/bai-tap");
        wait.until(ExpectedConditions.urlContains("/quan-tri-vien/bai-tap"));
        helper.delay(1500);
    }

    /**
     * Open the new-homework form, fill only the fields specified by the case,
     * click save, and verify the failure popup.
     */
    public void addHomeworkExpectFailure(HomeworkFailCase failCase) {
        System.out.println("\n=== Add Homework (Expect Fail): " + failCase.testCase + " ===");

        // Click "Thêm mới"
        WebElement addBtn = wait.until(ExpectedConditions.presenceOfElementLocated(BTN_THEM_MOI));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addBtn);
        helper.delay(2000);

        // Fill name if not null
        if (failCase.homeworkName != null) {
            WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(INPUT_TEN_BAI_TAP));
            nameInput.sendKeys(failCase.homeworkName);
            helper.delay(300);
            System.out.println("  Filled name: " + failCase.homeworkName);
        } else {
            System.out.println("  Skipped name");
        }

        // Upload thumbnail if required
        if (failCase.uploadThumbnail) {
            try {
                WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(FORM_FILE_INPUT));
                fileInput.sendKeys(IMAGE_FILE);
                helper.delay(1000);
                System.out.println("  Uploaded thumbnail");
            } catch (Exception e) {
                System.out.println("  WARNING: Could not upload thumbnail: " + e.getMessage());
            }
        } else {
            System.out.println("  Skipped thumbnail");
        }

        // Add a question if specified
        if (failCase.addQuestion) {
            WebElement btnAddQ = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_CAU_HOI));
            helper.safeClick(btnAddQ);
            helper.delay(1500);
            System.out.println("  Added a new question");

            // Expand the question panel
            expandLastQuestionPanel();

            // Fill question content if not null
            if (failCase.questionContent != null) {
                fillQuestionContent(failCase.questionContent);
                System.out.println("  Filled question content: " + failCase.questionContent);
            } else {
                System.out.println("  Skipped question content");
            }

            // Fill answers (only as many as provided — leaves remaining empty)
            if (failCase.answers != null && !failCase.answers.isEmpty()) {
                fillAnswers(failCase.answers);
                System.out.println("  Filled " + failCase.answers.size() + " answer(s)");
            } else {
                System.out.println("  Skipped all answers");
            }
        }

        // Click Save
        WebElement btnSave = wait.until(ExpectedConditions.elementToBeClickable(BTN_SAVE));
        helper.safeClick(btnSave);
        helper.delay(1500);
        System.out.println("  Clicked Save (expecting failure)");

        // Verify SweetAlert2 error popup appears (accept any error title — homework
        // uses several different validation messages: "Thông tin nhập thiếu",
        // "Tên đề thi còn thiếu", etc.)
        boolean popupVerified = false;
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_POPUP));
            String titleText = driver.findElement(POPUP_TITLE).getText().trim();
            String bodyText = driver.findElement(POPUP_BODY).getText().trim();
            System.out.println("  [PASS] Error popup visible:");
            System.out.println("    Title: " + titleText);
            System.out.println("    Body : " + bodyText);
            popupVerified = true;
        } catch (Exception e) {
            System.out.println("  [FAIL] Error popup did not appear for case: " + failCase.testCase);
        }

        // Always click OK to dismiss popup before moving on
        try {
            helper.clickOK();
            helper.delay(800);
            System.out.println("  Clicked OK to dismiss popup");
        } catch (Exception ex) {
            System.out.println("  WARNING: Could not click OK");
        }

        // Now fail the test if popup wasn't verified
        if (!popupVerified) {
            Assert.fail("Error popup did not appear for case: " + failCase.testCase);
        }
    }

    // ==================== Private helpers ====================

    private void expandLastQuestionPanel() {
        List<WebElement> panels = driver.findElements(
            By.xpath("//button[contains(@class, 'v-expansion-panel-header')]"));
        if (!panels.isEmpty()) {
            WebElement lastPanel = panels.get(panels.size() - 1);
            String expanded = lastPanel.getAttribute("aria-expanded");
            if (!"true".equals(expanded)) {
                helper.safeClick(lastPanel);
                helper.delay(1000);
            }
        }
    }

    private void fillQuestionContent(String content) {
        // Try textarea first (mirrors HomeworkManagementPage's working logic)
        List<WebElement> textareas = driver.findElements(
            By.xpath("//div[contains(@class, 'v-expansion-panel-content')]//div[contains(@class, 'v-expansion-panel--active')]//textarea | " +
                     "//div[contains(@class, 'v-expansion-panel--active')]//label[contains(text(),'Nội dung câu hỏi')]/ancestor::div[contains(@class,'v-input')]//textarea"));

        if (!textareas.isEmpty()) {
            WebElement textarea = textareas.get(textareas.size() - 1);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", textarea);
            helper.delay(200);
            textarea.clear();
            textarea.sendKeys(content);
            helper.delay(300);
            System.out.println("    -> question content filled via textarea");
            return;
        }

        // Fallback: try input field
        List<WebElement> inputs = driver.findElements(
            By.xpath("//div[contains(@class, 'v-expansion-panel--active')]//label[contains(text(),'Nội dung câu hỏi')]/ancestor::div[contains(@class,'v-input')]//input"));

        if (!inputs.isEmpty()) {
            WebElement input = inputs.get(inputs.size() - 1);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", input);
            helper.delay(200);
            input.clear();
            input.sendKeys(content);
            helper.delay(300);
            System.out.println("    -> question content filled via input");
            return;
        }

        System.out.println("    WARNING: Could not find textarea or input for 'Nội dung câu hỏi'");
    }

    private void fillAnswers(List<String> answers) {
        List<WebElement> answerInputs = driver.findElements(
            By.xpath("//div[contains(@class, 'v-expansion-panel--active')]//label[contains(text(),'Câu trả lời')]/ancestor::div[contains(@class,'v-input')]//input"));

        // Fill only as many answers as provided in the list (leaves remaining empty)
        int startIndex = Math.max(0, answerInputs.size() - 4);

        for (int i = 0; i < answers.size() && (startIndex + i) < answerInputs.size(); i++) {
            WebElement input = answerInputs.get(startIndex + i);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", input);
            helper.delay(200);
            input.sendKeys(answers.get(i));
            helper.delay(200);
        }
    }
}
