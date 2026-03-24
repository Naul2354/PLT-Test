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

import java.util.ArrayList;
import java.util.List;

public class StudentManagementPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final SeleniumHelper helper;

    // Locators
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
    private static final By EDIT_BTN = By.xpath(DIALOG + "//span[contains(normalize-space(),'Sửa')]/parent::button");
    private static final By DELETE_BTN = By.xpath(DIALOG + "//span[contains(normalize-space(),'Xoá')]/parent::button");
    private static final By SEARCH_BOX = By.id("input-41");
    private static final By ADD_NEW_BTN = By.xpath("//button[.//span[contains(normalize-space(),'Thêm mới')]]");
    private static final By NAV_STUDENT_MGMT = By.xpath("//nav//a[contains(normalize-space(),'Quản lý học viên')]");
    private static final By STUDENT_LIST_HEADER = By.xpath("//*[contains(normalize-space(),'Danh sách học viên')]");

    public StudentManagementPage(WebDriver driver, WebDriverWait wait, SeleniumHelper helper) {
        this.driver = driver;
        this.wait = wait;
        this.helper = helper;
    }

    public void navigateToStudentManagement() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(NAV_STUDENT_MGMT));
        driver.findElement(NAV_STUDENT_MGMT).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(STUDENT_LIST_HEADER));
    }

    public List<String[]> getStudentListAndPrint() {
        System.out.println("\n========================================");
        System.out.println("  STUDENT LIST - Dữ liệu thực tế từ UI");
        System.out.println("========================================");
        System.out.println(String.format("%-12s %-15s %-10s %-14s %-35s %-12s %-6s %-20s %-12s",
            "Mã HV", "Họ đệm", "Tên", "SĐT", "Email", "Ngày sinh", "GT", "Địa chỉ", "Cập nhật"));
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");

        helper.delay(2000);

        List<String[]> students = new ArrayList<>();
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 8) {
                String code = cells.get(0).getText().trim();
                String middleName = cells.get(1).getText().trim();
                String firstName = cells.get(2).getText().trim();
                String phone = cells.get(3).getText().trim();
                String email = cells.get(4).getText().trim();
                String dob = cells.get(5).getText().trim();
                String gender = cells.get(6).getText().trim();
                String address = cells.get(7).getText().trim();
                String updated = cells.size() > 8 ? cells.get(8).getText().trim() : "";

                System.out.println(String.format("%-12s %-15s %-10s %-14s %-35s %-12s %-6s %-20s %-12s",
                    code, middleName, firstName, phone, email, dob, gender, address, updated));

                students.add(new String[]{code, email});
            }
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("Total students: " + students.size());
        System.out.println("========================================\n");

        return students;
    }

    public void searchStudent(String text) {
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX));
        searchBox.clear();
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", searchBox, text);
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", searchBox
        );
        helper.delay(1000);
    }

    public void addStudent(StudentInfo student) {
        System.out.println("\n=== Adding Student ===");
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(ADD_NEW_BTN));
        addBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'v-dialog')]//*[contains(normalize-space(),'Thêm học viên')]")));

        helper.fillViaJS(FULL_NAME, student.fullName);
        helper.fillViaJS(STUDENT_CODE, student.studentCode);
        helper.fillViaJS(EMAIL, student.email);
        helper.fillViaJS(PHONE, student.phone);
        helper.fillViaJS(DOB, student.dob);
        helper.fillViaJS(ADDRESS, student.address);

        selectGender(student.gender);

        WebElement submitBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(SUBMIT_BTN));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
        System.out.println("Clicked Add");

        helper.delay(500);
        helper.clickOK();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(DIALOG)));
        System.out.println("Student added");
    }

    private void selectGender(String gender) {
        By genderLocator;
        if ("Nam".equalsIgnoreCase(gender)) genderLocator = GENDER_MALE;
        else if ("Nữ".equalsIgnoreCase(gender)) genderLocator = GENDER_FEMALE;
        else genderLocator = GENDER_OTHER;

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(genderLocator));
    }

    public void editStudentAddress(String studentCode, String newAddress) {
        System.out.println("\n=== Editing Student ===");
        WebElement editRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tr[.//td[contains(.,'" + studentCode + "')]]")));
        WebElement editIcon = editRow.findElement(By.xpath(".//button[.//i[contains(@class,'mdi-pencil')]]"));
        editIcon.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(DIALOG)));
        helper.delay(1000);

        System.out.println("Changing address to: " + newAddress);
        helper.fillViaJS(ADDRESS, newAddress);

        WebElement editSaveBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_BTN));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editSaveBtn);
        System.out.println("Clicked Edit");

        helper.delay(500);
        helper.clickOK();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(DIALOG)));
        System.out.println("Student edited");
    }

    public void deleteStudent(String studentCode) {
        System.out.println("\n=== Deleting Student ===");
        WebElement deleteRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tr[.//td[contains(.,'" + studentCode + "')]]")));
        WebElement deleteIcon = deleteRow.findElement(
                By.xpath(".//button[contains(@class,'red--text')]//i[contains(@class,'mdi-close')]"));
        deleteIcon.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(DIALOG)));

        WebElement deleteConfirm = wait.until(ExpectedConditions.elementToBeClickable(DELETE_BTN));
        deleteConfirm.click();
        System.out.println("Clicked Delete");

        helper.delay(300);
        helper.clickOK();
    }

    public void verifyStudent(StudentInfo expected) {
        System.out.println("\nVerifying student data...");

        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//table//tr[.//td[contains(.,'" + expected.studentCode + "')]]")
        ));

        List<WebElement> cells = row.findElements(By.tagName("td"));
        String actualCode = cells.get(0).getText().trim();
        String actualName = cells.get(1).getText().trim() + " " + cells.get(2).getText().trim();
        String actualPhone = cells.get(3).getText().trim();
        String actualEmail = cells.get(4).getText().trim();
        String actualAddress = cells.get(7).getText().trim();

        Assert.assertEquals(actualCode, expected.studentCode, "Student code mismatch");
        Assert.assertEquals(actualName, expected.fullName, "Full name mismatch");
        Assert.assertEquals(actualEmail, expected.email, "Email mismatch");
        Assert.assertEquals(actualPhone, expected.phone, "Phone mismatch");
        Assert.assertEquals(actualAddress, expected.address, "Address mismatch");

        System.out.println("All fields verified");
    }

    public void verifyAddress(String studentCode, String expectedAddress) {
        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tr[.//td[contains(.,'" + studentCode + "')]]")));
        List<WebElement> cells = row.findElements(By.tagName("td"));
        String actualAddress = cells.get(7).getText().trim();
        Assert.assertEquals(actualAddress, expectedAddress, "Address not updated");
        System.out.println("Address verified");
    }

    public void verifyStudentDeleted(String studentCode) {
        helper.delay(1000);
        searchStudent(studentCode);
        try {
            driver.findElement(By.xpath("//table//tr[.//td[contains(.,'" + studentCode + "')]]"));
            Assert.fail("Student still exists after deletion");
        } catch (org.openqa.selenium.NoSuchElementException e) {
            System.out.println("Student deleted");
        }
    }
}
