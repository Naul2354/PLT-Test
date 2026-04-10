package tests.admin;

import base.BaseTest;
import models.StudentInfo;
import pages.LoginPage;
import pages.StudentAddFailPage;
import pages.StudentManagementPage;
import utils.DataLoader;
import utils.SeleniumHelper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

public class AddStudentFailTest extends BaseTest {

    @Test
    public void testAddStudentFailMissingFields() throws Exception {
        System.out.println("========================================");
        System.out.println("TEST: Add Student Fail (Missing Required Fields)");
        System.out.println("========================================\n");

        // Load failure-test data from JSON
        List<StudentInfo> failCases = DataLoader.loadStudentsFailFromJSON();

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        StudentManagementPage studentPage = new StudentManagementPage(driver, wait, helper);
        StudentAddFailPage failPage = new StudentAddFailPage(driver, wait, helper);

        // Login & navigate
        loginPage.loginAsAdmin();
        studentPage.navigateToStudentManagement();

        // Run each failure case
        System.out.println("\nRunning " + failCases.size() + " failure case(s)...");
        for (StudentInfo student : failCases) {
            // Case label is stored in newAddress field
            String testCase = student.newAddress != null ? student.newAddress : "Unknown case";
            failPage.addStudentExpectFailure(student, testCase);
        }

        // ===== SUMMARY =====
        System.out.println("\n========================================");
        System.out.println("ALL TESTS PASSED");
        System.out.println("========================================");
        System.out.println("Failure cases tested: " + failCases.size());
        for (StudentInfo s : failCases) {
            System.out.println("  - " + s.newAddress + " -> [PASS] popup appeared");
        }
        System.out.println("========================================\n");
    }

    @Test
    public void testSearchRandomStudentNotFound() {
        System.out.println("========================================");
        System.out.println("TEST: Search Student With Random Code (Expect Not Found)");
        System.out.println("========================================\n");

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        StudentManagementPage studentPage = new StudentManagementPage(driver, wait, helper);

        // Login & navigate
        loginPage.loginAsAdmin();
        studentPage.navigateToStudentManagement();

        // Generate a random student code that almost certainly doesn't exist
        String randomCode = "SV" + (System.currentTimeMillis() % 100000) + new Random().nextInt(999);
        System.out.println("Searching for random student code: " + randomCode);

        // Search
        studentPage.searchStudent(randomCode);
        helper.delay(1500);

        // Verify no rows match — look for data rows in the table
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr[.//td[contains(.,'" + randomCode + "')]]"));
        System.out.println("Matching rows found: " + rows.size());
        Assert.assertEquals(rows.size(), 0, "Expected 0 results for random student code but found " + rows.size());

        System.out.println("\n[PASS] No student found for random code: " + randomCode);
        System.out.println("========================================\n");
    }
}
