package pages;

import models.CourseFailCase;
import utils.SeleniumHelper;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

/**
 * Negative-test page for adding a course with missing required fields
 * (thumbnail, name, description). Verifies the SweetAlert2 popup
 * "Thông tin nhập thiếu" appears.
 */
public class CourseAddFailPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final SeleniumHelper helper;

    private static final String IMAGE_FILE = System.getProperty("user.dir") + "/src/image/kotlin.png";

    // Locators (mirror CourseManagementPage)
    private static final By BTN_THEM_MOI_COURSE =
        By.xpath("//a[contains(@href, '/quan-tri-vien/khoa-hoc/them-moi')] | //button[.//span[contains(normalize-space(),'Thêm mới')]]");
    private static final By INPUT_TEN_KHOA_HOC =
        By.xpath("//input[@name='name']");
    private static final By INPUT_MO_TA =
        By.xpath("//textarea[@name='summary']");
    private static final By COURSE_FILE_INPUT =
        By.xpath("//input[@type='file']");
    private static final By BTN_SUBMIT_COURSE =
        By.xpath("//button[.//span[contains(normalize-space(),'Thêm mới')]]");

    // SweetAlert2 error popup locators
    private static final By ERROR_POPUP =
        By.xpath("//div[contains(@class,'swal2-popup') and contains(@class,'swal2-icon-error')]");
    private static final By POPUP_TITLE =
        By.xpath("//div[contains(@class,'swal2-popup')]//h2[contains(@class,'swal2-title')]");
    private static final By POPUP_BODY =
        By.xpath("//div[contains(@class,'swal2-popup')]//div[contains(@class,'swal2-html-container')]");

    public CourseAddFailPage(WebDriver driver, WebDriverWait wait, SeleniumHelper helper) {
        this.driver = driver;
        this.wait = wait;
        this.helper = helper;
    }

    public void navigateToCourseManagement() {
        System.out.println("Navigating to course management...");
        driver.get("https://elearning.plt.pro.vn/quan-tri-vien/khoa-hoc");
        wait.until(ExpectedConditions.urlContains("/quan-tri-vien/khoa-hoc"));
        helper.delay(1500);
    }

    /**
     * Open the new-course form, fill only the fields specified by the case,
     * click submit, and verify the failure popup.
     */
    public void addCourseExpectFailure(CourseFailCase failCase) {
        System.out.println("\n=== Add Course (Expect Fail): " + failCase.testCase + " ===");

        // Click "Thêm mới" to open the new-course form
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_MOI_COURSE));
        helper.safeClick(addBtn);
        helper.delay(2000);

        // Upload thumbnail if required
        if (failCase.uploadThumbnail) {
            try {
                WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(COURSE_FILE_INPUT));
                fileInput.sendKeys(IMAGE_FILE);
                helper.delay(1500);
                System.out.println("  Uploaded thumbnail");
            } catch (Exception e) {
                System.out.println("  WARNING: Could not upload thumbnail: " + e.getMessage());
            }
        } else {
            System.out.println("  Skipped thumbnail");
        }

        // Fill title if not null
        if (failCase.title != null) {
            WebElement titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(INPUT_TEN_KHOA_HOC));
            titleInput.sendKeys(failCase.title);
            helper.delay(300);
            System.out.println("  Filled title: " + failCase.title);
        } else {
            System.out.println("  Skipped title");
        }

        // Fill description if not null
        if (failCase.description != null) {
            WebElement descInput = wait.until(ExpectedConditions.visibilityOfElementLocated(INPUT_MO_TA));
            descInput.sendKeys(failCase.description);
            helper.delay(300);
            System.out.println("  Filled description: " + failCase.description);
        } else {
            System.out.println("  Skipped description");
        }

        // Click submit
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(BTN_SUBMIT_COURSE));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
        System.out.println("  Clicked Thêm mới (expecting failure)");

        helper.delay(1000);

        // Verify SweetAlert2 error popup
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_POPUP));
            String titleText = driver.findElement(POPUP_TITLE).getText().trim();
            String bodyText = driver.findElement(POPUP_BODY).getText().trim();
            System.out.println("  [PASS] Error popup visible:");
            System.out.println("    Title: " + titleText);
            System.out.println("    Body : " + bodyText);
            Assert.assertTrue(titleText.contains("Thông tin nhập thiếu"),
                "Expected popup title 'Thông tin nhập thiếu' but got: " + titleText);
        } catch (org.testng.TestException e) {
            throw e;
        } catch (Exception e) {
            Assert.fail("Error popup did not appear for case: " + failCase.testCase);
        }

        // Dismiss popup
        helper.clickOK();
        helper.delay(500);
    }
}
