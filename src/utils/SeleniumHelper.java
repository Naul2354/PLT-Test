package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class SeleniumHelper {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public SeleniumHelper(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void delay() {
        delay(800);
    }

    public void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Scroll into view and click via JavaScript */
    public void safeClick(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        delay(300);
        js.executeScript("arguments[0].click();", element);
        delay();
    }

    /** Fill an input field using JavaScript (fast, triggers input event) */
    public void fillViaJS(By locator, String value) {
        if (value == null) return;

        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

        // Convert date format if needed (MM/dd/yyyy -> yyyy-MM-dd)
        if ("dob".equals(el.getAttribute("name")) && value.contains("/")) {
            String[] parts = value.split("/");
            if (parts.length == 3) {
                value = parts[2] + "-" + parts[0] + "-" + parts[1];
            }
        }

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1]; " +
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
            el, value
        );
    }

    /** Fill the last visible matching field under a parent element (for dynamic forms) */
    public void fillField(WebElement parent, By locator, String value) {
        List<WebElement> elements = parent.findElements(locator);

        if (elements.isEmpty()) {
            throw new RuntimeException("Field not found: " + locator);
        }

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

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        delay(200);
        element.clear();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        delay(100);
        element.sendKeys(value);
        delay(300);
    }

    public String getValue(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
                   .getAttribute("value").trim();
    }

    public void clickOK() {
        try {
            WebElement ok = wait.until(ExpectedConditions.elementToBeClickable(By.className("swal2-confirm")));
            ok.click();
            System.out.println("OK clicked");
        } catch (Exception e) {
            System.out.println("No OK button found");
        }
    }
}
