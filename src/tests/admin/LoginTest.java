package tests.admin;

import base.BaseTest;
import models.LoginData;
import pages.LoginPage;
import utils.DataLoader;

import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void testLoginFailAndSuccess() throws Exception {
        System.out.println("========================================");
        System.out.println("TEST: Login Fail & Success");
        System.out.println("========================================\n");

        // Load login data from JSON
        LoginData loginData = DataLoader.loadLoginFromJSON();

        LoginPage loginPage = new LoginPage(driver, wait);

        // ===== PART 1: Test invalid logins (should all fail) =====
        System.out.println("\n===== PART 1: Login Fail Tests =====");
        System.out.println("Testing " + loginData.invalidLogins.size() + " invalid credential set(s)");

        for (LoginData.InvalidLogin invalid : loginData.invalidLogins) {
            loginPage.loginExpectFailure(invalid.email, invalid.password, invalid.testCase);
        }

        // ===== PART 2: Test valid login (should succeed) =====
        System.out.println("\n===== PART 2: Login Success Test =====");
        loginPage.loginExpectSuccess(loginData.validEmail, loginData.validPassword);

        // ===== SUMMARY =====
        System.out.println("\n========================================");
        System.out.println("ALL TESTS PASSED");
        System.out.println("========================================");
        System.out.println("Invalid logins tested: " + loginData.invalidLogins.size() + " (all rejected)");
        System.out.println("Valid login tested  : 1 (success)");
        System.out.println("========================================\n");
    }
}
