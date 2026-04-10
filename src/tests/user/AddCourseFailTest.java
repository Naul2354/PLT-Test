package tests.user;

import base.BaseTest;
import models.CourseFailCase;
import pages.CourseAddFailPage;
import pages.LoginPage;
import utils.DataLoader;
import utils.SeleniumHelper;

import org.testng.annotations.Test;

import java.util.List;

public class AddCourseFailTest extends BaseTest {

    @Test
    public void testAddCourseFailMissingFields() throws Exception {
        System.out.println("========================================");
        System.out.println("TEST: Add Course Fail (Missing Required Fields)");
        System.out.println("========================================\n");

        // Load failure-test data from JSON
        List<CourseFailCase> failCases = DataLoader.loadCoursesFailFromJSON();

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        CourseAddFailPage failPage = new CourseAddFailPage(driver, wait, helper);

        // Login
        loginPage.loginAsAdmin();

        // Run each failure case (navigate back to course list between cases)
        System.out.println("\nRunning " + failCases.size() + " failure case(s)...");
        for (CourseFailCase failCase : failCases) {
            failPage.navigateToCourseManagement();
            failPage.addCourseExpectFailure(failCase);
        }

        // ===== SUMMARY =====
        System.out.println("\n========================================");
        System.out.println("ALL TESTS PASSED");
        System.out.println("========================================");
        System.out.println("Failure cases tested: " + failCases.size());
        for (CourseFailCase c : failCases) {
            System.out.println("  - " + c.testCase + " -> [PASS] popup appeared");
        }
        System.out.println("========================================\n");
    }
}
