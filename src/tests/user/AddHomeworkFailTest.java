package tests.user;

import base.BaseTest;
import models.HomeworkFailCase;
import pages.HomeworkAddFailPage;
import pages.LoginPage;
import utils.DataLoader;
import utils.SeleniumHelper;

import org.testng.annotations.Test;

import java.util.List;

public class AddHomeworkFailTest extends BaseTest {

    @Test
    public void testAddHomeworkFailMissingFields() throws Exception {
        System.out.println("========================================");
        System.out.println("TEST: Add Homework Fail (Missing Required Fields)");
        System.out.println("========================================\n");

        // Load failure-test data from JSON
        List<HomeworkFailCase> failCases = DataLoader.loadHomeworksFailFromJSON();

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        HomeworkAddFailPage failPage = new HomeworkAddFailPage(driver, wait, helper);

        // Login
        loginPage.loginAsAdmin();

        // Run each failure case (navigate back to homework list between cases)
        System.out.println("\nRunning " + failCases.size() + " failure case(s)...");
        for (HomeworkFailCase failCase : failCases) {
            failPage.navigateToHomeworkManagement();
            failPage.addHomeworkExpectFailure(failCase);
        }

        // ===== SUMMARY =====
        System.out.println("\n========================================");
        System.out.println("ALL TESTS PASSED");
        System.out.println("========================================");
        System.out.println("Failure cases tested: " + failCases.size());
        for (HomeworkFailCase c : failCases) {
            System.out.println("  - " + c.testCase + " -> [PASS] popup appeared");
        }
        System.out.println("========================================\n");
    }
}
