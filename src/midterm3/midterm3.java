package midterm3;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.testng.TestNG;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;

public class midterm3 {

    WebDriver driver;
    WebDriverWait wait;

    public static void main(String[] args) {
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{midterm3.class});
        testng.run();
    }

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @DataProvider(name = "userData")
    public Object[][] getUserData() throws IOException {
        // Read JSON file
        String content = new String(Files.readAllBytes(Paths.get("src/midterm3/users.json")));
        JSONArray jsonArray = new JSONArray(content);

        Object[][] data = new Object[jsonArray.length()][3];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            data[i][0] = obj.getString("name");
            data[i][1] = obj.getString("email");
            data[i][2] = obj.getString("password");
        }
        return data;
    }

    @Test(dataProvider = "userData")
    public void createAccount(String name, String email, String password) throws InterruptedException {
        // Step 1: Open the website
        driver.get("https://jqueryui.com/dialog/#modal-form");

        // Step 2: Switch to iframe (demo content is inside iframe)
        WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("demo-frame")));
        driver.switchTo().frame(iframe);

        // Step 3: Click "Create new user" button
        WebElement createUserBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("create-user")));
        createUserBtn.click();

        // Step 4: Wait for dialog to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dialog-form")));

        // Step 5: Fill in the form
        WebElement nameField = driver.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys(name);

        WebElement emailField = driver.findElement(By.id("email"));
        emailField.clear();
        emailField.sendKeys(email);

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.clear();
        passwordField.sendKeys(password);

        // Step 6: Click "Create an account" button
        WebElement createAccountBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[2]/div[3]/div/button[1]")));
        createAccountBtn.click();

        Thread.sleep(1000);

        // Step 7: Verify user was added by reading the Existing Users table
        System.out.println("--------------------------------------------------");
        System.out.println("Created account: Name=" + name + " | Email=" + email + " | Password=" + password);

        java.util.List<WebElement> rows = driver.findElements(By.cssSelector("#users tbody tr"));
        System.out.println("Existing Users in table (" + rows.size() + " total):");
        System.out.println(String.format("%-25s %-35s %-15s", "Name", "Email", "Password"));
        for (WebElement row : rows) {
            java.util.List<WebElement> cols = row.findElements(By.tagName("td"));
            if (cols.size() >= 3) {
                System.out.println(String.format("%-25s %-35s %-15s",
                        cols.get(0).getText(), cols.get(1).getText(), cols.get(2).getText()));
            }
        }
        System.out.println("--------------------------------------------------");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
