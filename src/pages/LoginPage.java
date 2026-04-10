package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final String LOGIN_URL = "https://elearning.plt.pro.vn/dang-nhap?redirect=%2Ftrang-chu";

    private static final By EMAIL_INPUT = By.id("input-10");
    private static final By PASSWORD_INPUT = By.id("input-13");
    private static final By LOGIN_BTN = By.xpath("//span[contains(text(),'Đăng nhập')]");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void login(String email, String password) {
        System.out.println("Logging in as " + email + "...");
        driver.get(LOGIN_URL);

        wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT))
                .sendKeys(email);
        driver.findElement(PASSWORD_INPUT).sendKeys(password);
        driver.findElement(LOGIN_BTN).click();

        wait.until(ExpectedConditions.urlContains("/trang-chu"));
        System.out.println("Login successful");
    }

    /** Login as admin and wait for student management nav link */
    public void loginAsAdmin() {
        login("test.pltsolutions@gmail.com", "plt@intern_051224");
    }

    /** Login as user (test1 account) */
    public void loginAsUser() {
        login("test1.pltsolutions@gmail.com", "plt@intern_051224");
    }

    /** Attempt login with invalid credentials and verify failure popup */
    public void loginExpectFailure(String email, String password, String testCase) {
        System.out.println("\n--- Login Fail Test: " + testCase + " ---");
        System.out.println("  Email   : " + email);
        System.out.println("  Password: " + password);

        driver.get(LOGIN_URL);

        // Clear & fill email
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT));
        emailInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        emailInput.sendKeys(email);

        // Clear & fill password
        WebElement passwordInput = driver.findElement(PASSWORD_INPUT);
        passwordInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        passwordInput.sendKeys(password);

        // Click login
        driver.findElement(LOGIN_BTN).click();

        // Verify failure popup "Đăng nhập không thành công"
        try {
            WebElement failPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Đăng nhập không thành công')]")));
            String popupText = failPopup.getText().trim();
            System.out.println("[PASS] Failure popup visible: " + popupText);
        } catch (Exception e) {
            Assert.fail("Failure popup 'Đăng nhập không thành công' did not appear for case: " + testCase);
        }

        // Verify we are still on login page (NOT redirected to /trang-chu)
        String currentUrl = driver.getCurrentUrl();
        System.out.println("  Current URL: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("/dang-nhap"),
            "Expected to remain on login page, but URL is: " + currentUrl);
        System.out.println("[PASS] Still on login page (not redirected)");
    }

    /** Attempt login with valid credentials and verify success */
    public void loginExpectSuccess(String email, String password) {
        System.out.println("\n--- Login Success Test ---");
        System.out.println("  Email   : " + email);
        System.out.println("  Password: " + password);

        driver.get(LOGIN_URL);

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT));
        emailInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        emailInput.sendKeys(email);

        WebElement passwordInput = driver.findElement(PASSWORD_INPUT);
        passwordInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        passwordInput.sendKeys(password);

        driver.findElement(LOGIN_BTN).click();

        // Verify redirect to /trang-chu
        wait.until(ExpectedConditions.urlContains("/trang-chu"));
        System.out.println("[PASS] Login successful — redirected to: " + driver.getCurrentUrl());
    }
}
