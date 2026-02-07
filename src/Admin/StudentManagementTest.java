package Admin;

import java.io.FileReader;
import java.io.Reader;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StudentManagementTest {

    private WebDriver driver;
    private WebDriverWait wait;

    // CSV Data
    private static List<String> lastNames = new ArrayList<>();
    private static List<String> middleNames = new ArrayList<>();
    private static List<String> firstNames = new ArrayList<>();
    private static List<String> streets = new ArrayList<>();
    private static List<String> districts = new ArrayList<>();

    // Student Model
    private static class StudentInfo {
        String fullName, studentCode, email, phone, dob, address, gender;

        StudentInfo(String fullName, String studentCode, String email, String phone,
                    String dob, String address, String gender) {
            this.fullName = fullName;
            this.studentCode = studentCode;
            this.email = email;
            this.phone = phone;
            this.dob = dob;
            this.address = address;
            this.gender = gender;
        }
    }

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

    // Utils
    private void delay() {
        delay(500);
    }

    private void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void fill(By locator, String value) {
        if (value == null) return;

        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

        // Convert date format if needed (MM/dd/yyyy -> yyyy-MM-dd)
        if ("dob".equals(el.getAttribute("name")) && value.contains("/")) {
            String[] parts = value.split("/");
            if (parts.length == 3) {
                value = parts[2] + "-" + parts[0] + "-" + parts[1];
            }
        }

        // Fast fill with JavaScript
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1]; " +
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
            el, value
        );
    }

    private String getValue(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
                   .getAttribute("value").trim();
    }

    private void clickOK() {
        try {
            WebElement ok = wait.until(ExpectedConditions.elementToBeClickable(By.className("swal2-confirm")));
            ok.click();
            System.out.println("OK clicked");
        } catch (Exception e) {
            System.out.println("No OK button found");
        }
    }

    private void searchStudent(String text) {
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX));
        searchBox.clear();
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", searchBox, text);
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", searchBox
        );
        delay(1000);
    }

    private void verifyStudent(StudentInfo expected) {
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

        System.out.println("✓ All fields verified");
    }

    private void loadCSV() {
        try {
            System.out.println("Loading CSV data...");

            // Load names
            Reader namesReader = new FileReader("src/resources/vietnamese_names.csv");
            Iterable<CSVRecord> namesRecords = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(namesReader);

            for (CSVRecord record : namesRecords) {
                String type = record.get("type");
                String value = record.get("value");

                if ("lastName".equals(type)) lastNames.add(value);
                else if ("middleName".equals(type)) middleNames.add(value);
                else if ("firstName".equals(type)) firstNames.add(value);
            }
            namesReader.close();

            // Load locations
            Reader locationsReader = new FileReader("src/resources/vietnamese_locations.csv");
            Iterable<CSVRecord> locationRecords = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(locationsReader);

            for (CSVRecord record : locationRecords) {
                String type = record.get("type");
                String value = record.get("value");

                if ("street".equals(type)) streets.add(value);
                else if ("district".equals(type)) districts.add(value);
            }
            locationsReader.close();

            System.out.println("✓ CSV data loaded");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load CSV data", e);
        }
    }

    private StudentInfo generateRandomStudent() {
        if (lastNames.isEmpty()) loadCSV();

        Random rand = new Random();

        // Generate name
        String fullName = lastNames.get(rand.nextInt(lastNames.size())) + " " +
                          middleNames.get(rand.nextInt(middleNames.size())) + " " +
                          firstNames.get(rand.nextInt(firstNames.size()));

        // Generate student code
        String studentCode = "SV" + (System.currentTimeMillis() % 100000);

        // Generate email
        String firstName = fullName.substring(fullName.lastIndexOf(" ") + 1).toLowerCase()
                .replaceAll("[áàảãạăắằẳẵặâấầẩẫậ]", "a")
                .replaceAll("[éèẻẽẹêếềểễệ]", "e")
                .replaceAll("[íìỉĩị]", "i")
                .replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o")
                .replaceAll("[úùủũụưứừửữự]", "u")
                .replaceAll("[ýỳỷỹỵ]", "y")
                .replaceAll("đ", "d");
        String[] domains = {"@gmail.com", "@outlook.com", "@yahoo.com"};
        String email = firstName + "." + studentCode.toLowerCase() + domains[rand.nextInt(domains.length)];

        // Generate phone
        String[] prefixes = {"091", "090", "093", "094", "096", "097", "098", "032", "033"};
        String phone = prefixes[rand.nextInt(prefixes.length)] + String.format("%07d", rand.nextInt(10000000));

        // Generate DOB
        int year = LocalDate.now().getYear() - (18 + rand.nextInt(8));
        int month = 1 + rand.nextInt(12);
        int day = 1 + rand.nextInt(28);
        String dob = String.format("%02d/%02d/%d", month, day, year);

        // Generate address
        int houseNum = 1 + rand.nextInt(500);
        String address = houseNum + " " +
                         streets.get(rand.nextInt(streets.size())) + ", " +
                         districts.get(rand.nextInt(districts.size())) + ", TP.HCM";

        // Random gender
        String[] genders = {"Nam", "Nữ", "Khác"};
        String gender = genders[rand.nextInt(genders.length)];

        System.out.println("\nGenerated student:");
        System.out.println("  Name: " + fullName);
        System.out.println("  Code: " + studentCode);
        System.out.println("  Email: " + email);
        System.out.println("  Phone: " + phone);

        return new StudentInfo(fullName, studentCode, email, phone, dob, address, gender);
    }

    private void loginAsAdmin() {
        System.out.println("Logging in as admin...");
        driver.get("https://elearning.plt.pro.vn/dang-nhap?redirect=%2Ftrang-chu");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-10")))
                .sendKeys("test.pltsolutions@gmail.com");
        driver.findElement(By.id("input-13")).sendKeys("plt@intern_051224");
        driver.findElement(By.xpath("//span[contains(text(),'Đăng nhập')]")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//nav//a[contains(.,'Quản lý học viên')]")));

        driver.findElement(By.xpath("//nav//a[contains(normalize-space(),'Quản lý học viên')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(normalize-space(),'Danh sách học viên')]")));
    }

    @Test
    public void testStudentManagementCRUDWorkflow() {
        System.out.println("========================================");
        System.out.println("TEST: Student Management CRUD");
        System.out.println("========================================\n");

        StudentInfo student = generateRandomStudent();

        // Initialize browser with Docker-safe options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            loginAsAdmin();

            // ADD STUDENT
            System.out.println("\n=== Adding Student ===");
            WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[.//span[contains(normalize-space(),'Thêm mới')]]")));
            addBtn.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'v-dialog')]//*[contains(normalize-space(),'Thêm học viên')]")));

            fill(FULL_NAME, student.fullName);
            fill(STUDENT_CODE, student.studentCode);
            fill(EMAIL, student.email);
            fill(PHONE, student.phone);
            fill(DOB, student.dob);
            fill(ADDRESS, student.address);

            WebElement genderLabel;
            if ("Nam".equalsIgnoreCase(student.gender)) genderLabel = driver.findElement(GENDER_MALE);
            else if ("Nữ".equalsIgnoreCase(student.gender)) genderLabel = driver.findElement(GENDER_FEMALE);
            else genderLabel = driver.findElement(GENDER_OTHER);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", genderLabel);

            WebElement submitBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(SUBMIT_BTN));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
            System.out.println("✓ Clicked Add");

            delay();
            clickOK();

            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(DIALOG)));
            System.out.println("✓ Student added");

            delay(1000);

            // VERIFY ADDED
            searchStudent(student.studentCode);
            verifyStudent(student);

            // EDIT STUDENT
            System.out.println("\n=== Editing Student ===");
            WebElement editRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table//tr[.//td[contains(.,'" + student.studentCode + "')]]")));
            WebElement editIcon = editRow.findElement(By.xpath(".//button[.//i[contains(@class,'mdi-pencil')]]"));
            editIcon.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(DIALOG)));
            delay(1000);

            // Change address
            Random rand = new Random();
            String newAddress = (1 + rand.nextInt(500)) + " " +
                    streets.get(rand.nextInt(streets.size())) + ", " +
                    districts.get(rand.nextInt(districts.size())) + ", TP.HCM";

            System.out.println("Changing address to: " + newAddress);
            fill(ADDRESS, newAddress);

            WebElement editSaveBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_BTN));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editSaveBtn);
            System.out.println("✓ Clicked Edit");

            delay();
            clickOK();

            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(DIALOG)));
            System.out.println("✓ Student edited");

            delay(1000);

            // VERIFY EDITED
            searchStudent(student.studentCode);
            WebElement updatedRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table//tr[.//td[contains(.,'" + student.studentCode + "')]]")));
            List<WebElement> cells = updatedRow.findElements(By.tagName("td"));
            String actualAddress = cells.get(7).getText().trim();

            Assert.assertEquals(actualAddress, newAddress, "Address not updated");
            System.out.println("✓ Address verified");

            // DELETE STUDENT
            System.out.println("\n=== Deleting Student ===");
            WebElement deleteRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table//tr[.//td[contains(.,'" + student.studentCode + "')]]")));
            WebElement deleteIcon = deleteRow.findElement(
                    By.xpath(".//button[contains(@class,'red--text')]//i[contains(@class,'mdi-close')]"));
            deleteIcon.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(DIALOG)));

            WebElement deleteConfirm = wait.until(ExpectedConditions.elementToBeClickable(DELETE_BTN));
            deleteConfirm.click();
            System.out.println("✓ Clicked Delete");

            delay(300);
            clickOK();

            // VERIFY DELETED
            delay(1000);
            searchStudent(student.studentCode);

            try {
                driver.findElement(By.xpath("//table//tr[.//td[contains(.,'" + student.studentCode + "')]]"));
                Assert.fail("Student still exists after deletion");
            } catch (org.openqa.selenium.NoSuchElementException e) {
                System.out.println("✓ Student deleted");
            }

            // TEST SUMMARY
            System.out.println("\n========================================");
            System.out.println("TEST PASSED");
            System.out.println("========================================");
            System.out.println("✓ Add student");
            System.out.println("✓ Verify student");
            System.out.println("✓ Edit student");
            System.out.println("✓ Delete student");
            System.out.println("========================================\n");

        } catch (Exception e) {
            System.out.println("\nTEST FAILED: " + e.getMessage());
            delay(5000);
            throw e;
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
