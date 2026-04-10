package pages;

import models.StudentInfo;
import utils.SeleniumHelper;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

/**
 * Negative-test page for adding students with missing required fields.
 * Reuses the same locators as StudentManagementPage but kept separate for isolation.
 */
public class StudentAddFailPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final SeleniumHelper helper;

    // Locators (mirror StudentManagementPage)
    private static final String DIALOG = "//div[contains(@class,'v-dialog__content') and contains(@class,'active')]";
    private static final By FULL_NAME = By.xpath(DIALOG + "//input[@name='full_name']");
    private static final By STUDENT_CODE = By.xpath(DIALOG + "//input[@name='student_code']");
    private static final By EMAIL = By.xpath(DIALOG + "//input[@name='email']");
    private static final By DOB = By.xpath(DIALOG + "//input[@name='dob']");
    private static final By PHONE = By.xpath(DIALOG + "//input[@name='phone']");
    private static final By ADDRESS = By.xpath(DIALOG + "//input[@name='address']");
    private static final By GENDER_MALE = By.xpath(DIALOG + "//label[contains(normalize-space(),'Nam')]");
    private static final By GENDER_FEMALE = By.xpath(DIALOG + "//label[contains(normalize-space(),'Nữ')]");
    private static final By GENDER_OTHER = By.xpath(DIALOG + "//label[contains(normalize-space(),'Khác')]");
    private static final By SUBMIT_BTN = By.xpath(DIALOG + "//span[contains(normalize-space(),'Thêm')]/parent::button");
    private static final By ADD_NEW_BTN = By.xpath("//button[.//span[contains(normalize-space(),'Thêm mới')]]");
    private static final By CANCEL_BTN = By.xpath(DIALOG + "//button[.//span[contains(normalize-space(),'Huỷ')]]");

    // SweetAlert2 error popup locators
    private static final By ERROR_POPUP = By.xpath("//div[contains(@class,'swal2-popup') and contains(@class,'swal2-icon-error')]");
    private static final By POPUP_TITLE = By.xpath("//div[contains(@class,'swal2-popup')]//h2[contains(@class,'swal2-title')]");
    private static final By POPUP_BODY = By.xpath("//div[contains(@class,'swal2-popup')]//div[contains(@class,'swal2-html-container')]");

    public StudentAddFailPage(WebDriver driver, WebDriverWait wait, SeleniumHelper helper) {
        this.driver = driver;
        this.wait = wait;
        this.helper = helper;
    }

    /**
     * Try to add a student with missing required fields.
     * Pass null in StudentInfo for fields you want to skip.
     * Verifies the SweetAlert2 "Thông tin nhập thiếu" popup appears.
     */
    public void addStudentExpectFailure(StudentInfo student, String testCase) {
        System.out.println("\n=== Add Student (Expect Fail): " + testCase + " ===");

        // Click Add New
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(ADD_NEW_BTN));
        addBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'v-dialog')]//*[contains(normalize-space(),'Thêm học viên')]")));
        helper.delay(800);

        // Fill only non-null fields
        if (student.fullName != null) helper.fillViaJS(FULL_NAME, student.fullName);
        if (student.studentCode != null) helper.fillViaJS(STUDENT_CODE, student.studentCode);
        if (student.email != null) helper.fillViaJS(EMAIL, student.email);
        if (student.phone != null) helper.fillViaJS(PHONE, student.phone);
        if (student.dob != null) helper.fillViaJS(DOB, student.dob);
        if (student.address != null) helper.fillViaJS(ADDRESS, student.address);
        if (student.gender != null) selectGender(student.gender);

        // Print what was filled
        System.out.println("  Filled: "
                + (student.fullName != null ? "fullName " : "")
                + (student.studentCode != null ? "studentCode " : "")
                + (student.email != null ? "email " : "")
                + (student.phone != null ? "phone " : "")
                + (student.dob != null ? "dob " : "")
                + (student.address != null ? "address " : "")
                + (student.gender != null ? "gender" : ""));

        // Click Submit
        WebElement submitBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(SUBMIT_BTN));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
        System.out.println("  Clicked Submit (expecting failure)");

        helper.delay(800);

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
            Assert.fail("Error popup did not appear for case: " + testCase);
        }

        // Click OK to dismiss the popup
        helper.clickOK();
        helper.delay(800);

        // Close the add dialog by clicking "Huỷ" (Cancel) button
        try {
            WebElement cancelBtn = wait.until(ExpectedConditions.elementToBeClickable(CANCEL_BTN));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cancelBtn);
            System.out.println("  Clicked Huỷ to close dialog");
        } catch (Exception ex) {
            System.out.println("  WARNING: Could not click Huỷ button");
        }

        // Wait for dialog overlay to disappear
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[contains(@class,'v-overlay__scrim')]")));
        } catch (Exception ex) {
            // ignore
        }
        helper.delay(800);
    }

    private void selectGender(String gender) {
        By genderLocator;
        if ("Nam".equalsIgnoreCase(gender)) genderLocator = GENDER_MALE;
        else if ("Nữ".equalsIgnoreCase(gender)) genderLocator = GENDER_FEMALE;
        else genderLocator = GENDER_OTHER;

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(genderLocator));
    }
}
