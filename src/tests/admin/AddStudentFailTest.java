package tests.admin;

import base.BaseTest;
import models.StudentInfo;
import pages.LoginPage;
import pages.StudentAddFailPage;
import pages.StudentManagementPage;
import utils.DataLoader;
import utils.SeleniumHelper;

import org.testng.annotations.Test;

import java.util.List;

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
}
