package tests.admin;

import base.BaseTest;
import pages.LoginPage;
import pages.LogoutPage;
import utils.SeleniumHelper;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LogoutTest extends BaseTest {

    @Test
    public void testAdminLogoutCancelAndConfirm() {
        System.out.println("========================================");
        System.out.println("TEST: Admin Logout — Cancel & Confirm");
        System.out.println("========================================\n");

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        LogoutPage logoutPage = new LogoutPage(driver, wait, helper);

        loginPage.loginAsAdmin();

        // ===== CASE 1: Cancel =====
        System.out.println("\n===== Admin — CASE 1: Cancel Logout =====");
        logoutPage.clickLogout();
        logoutPage.clickCancelLogout();

        String urlAfterCancel = driver.getCurrentUrl();
        System.out.println("URL after cancel: " + urlAfterCancel);
        Assert.assertTrue(urlAfterCancel.contains("/trang-chu"),
            "Expected to remain on /trang-chu after cancel, but URL is: " + urlAfterCancel);
        System.out.println("[PASS] Still on homepage after cancel");

        // ===== CASE 2: Confirm =====
        System.out.println("\n===== Admin — CASE 2: Confirm Logout =====");
        logoutPage.clickLogout();
        logoutPage.clickConfirmLogout();

        helper.delay(2000);
        String urlAfterConfirm = driver.getCurrentUrl();
        System.out.println("URL after confirm: " + urlAfterConfirm);
        Assert.assertTrue(urlAfterConfirm.contains("/dang-nhap"),
            "Expected to be redirected to /dang-nhap after confirm, but URL is: " + urlAfterConfirm);
        System.out.println("[PASS] Redirected to login page after confirm");

        System.out.println("\n========================================");
        System.out.println("ADMIN LOGOUT TESTS PASSED");
        System.out.println("========================================\n");
    }

    @Test
    public void testUserLogoutCancelAndConfirm() {
        System.out.println("========================================");
        System.out.println("TEST: User Logout — Cancel & Confirm");
        System.out.println("========================================\n");

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        LogoutPage logoutPage = new LogoutPage(driver, wait, helper);

        // Login as admin, then navigate to user page
        loginPage.loginAsAdmin();
        logoutPage.navigateToUserPage();

        // ===== CASE 1: Cancel =====
        System.out.println("\n===== User — CASE 1: Cancel Logout =====");
        logoutPage.openAvatarDropdown();
        logoutPage.clickLogout();
        logoutPage.clickCancelLogout();

        String urlAfterCancel = driver.getCurrentUrl();
        System.out.println("URL after cancel: " + urlAfterCancel);
        Assert.assertTrue(urlAfterCancel.contains("/trang-chu"),
            "Expected to remain on /trang-chu after cancel, but URL is: " + urlAfterCancel);
        System.out.println("[PASS] Still on user homepage after cancel");

        // ===== CASE 2: Confirm =====
        System.out.println("\n===== User — CASE 2: Confirm Logout =====");
        logoutPage.openAvatarDropdown();
        logoutPage.clickLogout();
        logoutPage.clickConfirmLogout();

        helper.delay(2000);
        String urlAfterConfirm = driver.getCurrentUrl();
        System.out.println("URL after confirm: " + urlAfterConfirm);
        Assert.assertTrue(urlAfterConfirm.contains("/dang-nhap"),
            "Expected to be redirected to /dang-nhap after confirm, but URL is: " + urlAfterConfirm);
        System.out.println("[PASS] Redirected to login page after confirm");

        System.out.println("\n========================================");
        System.out.println("USER LOGOUT TESTS PASSED");
        System.out.println("========================================\n");
    }
}
