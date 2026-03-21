package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
}
