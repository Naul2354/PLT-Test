package pages;

import utils.SeleniumHelper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page object for the logout flow. Supports two scenarios:
 *  - Admin logout: click sidebar "Đăng xuất" directly → confirmation dialog.
 *  - User logout: navigate to user page → open avatar dropdown → click
 *    "Đăng xuất" menu item → confirmation dialog.
 *  Both end with Huỷ (cancel) or OK (confirm).
 */
public class LogoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final SeleniumHelper helper;

    // "Đến trang người dùng" nav item (admin → user view)
    private static final By NAV_USER_PAGE =
        By.xpath("//div[contains(@class,'v-list-item') and .//div[contains(@class,'v-list-item__title') and contains(text(),'Đến trang người dùng')]]");

    // Avatar dropdown button (user view, top right)
    private static final By AVATAR_BUTTON =
        By.xpath("//button[@aria-haspopup='true' and .//div[contains(@class,'v-avatar')] and .//i[contains(@class,'mdi-menu-down')]]");

    // Generic "Đăng xuất" link — matches both admin sidebar item and user avatar dropdown menu item
    private static final By LOGOUT_LINK =
        By.xpath("//div[contains(@class,'v-list-item') and .//i[contains(@class,'mdi-logout')] and .//div[contains(text(),'Đăng xuất')]]");

    // Confirmation dialog buttons
    private static final By BTN_HUY =
        By.xpath("//button[.//span[contains(normalize-space(),'Huỷ')]]");
    private static final By BTN_OK =
        By.xpath("//button[.//span[contains(normalize-space(),'OK')]]");

    public LogoutPage(WebDriver driver, WebDriverWait wait, SeleniumHelper helper) {
        this.driver = driver;
        this.wait = wait;
        this.helper = helper;
    }

    /** Click "Đến trang người dùng" to navigate from admin view to user homepage */
    public void navigateToUserPage() {
        System.out.println("Navigating to user page...");
        WebElement navItem = wait.until(ExpectedConditions.elementToBeClickable(NAV_USER_PAGE));
        helper.safeClick(navItem);
        helper.delay(2000);
        System.out.println("Now on user page");
    }

    /** Click the avatar dropdown button in the top-right corner (user view only) */
    public void openAvatarDropdown() {
        System.out.println("Opening avatar dropdown...");
        WebElement avatar = wait.until(ExpectedConditions.elementToBeClickable(AVATAR_BUTTON));
        helper.safeClick(avatar);
        helper.delay(800);
    }

    /** Click the "Đăng xuất" link (works for both admin sidebar and user avatar dropdown) */
    public void clickLogout() {
        System.out.println("Clicking Đăng xuất...");
        WebElement logoutItem = wait.until(ExpectedConditions.elementToBeClickable(LOGOUT_LINK));
        helper.safeClick(logoutItem);
        helper.delay(1000);
    }

    /** Click Huỷ in the confirmation dialog to cancel logout */
    public void clickCancelLogout() {
        System.out.println("Clicking Huỷ (cancel logout)...");
        WebElement cancelBtn = wait.until(ExpectedConditions.elementToBeClickable(BTN_HUY));
        helper.safeClick(cancelBtn);
        helper.delay(1000);
    }

    /** Click OK in the confirmation dialog to confirm logout */
    public void clickConfirmLogout() {
        System.out.println("Clicking OK (confirm logout)...");
        WebElement okBtn = wait.until(ExpectedConditions.elementToBeClickable(BTN_OK));
        helper.safeClick(okBtn);
        helper.delay(2000);
    }
}
