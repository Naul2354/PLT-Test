package tests.admin;

import base.BaseTest;
import models.StudentInfo;
import pages.LoginPage;
import pages.StudentManagementPage;
import utils.DataLoader;
import utils.SeleniumHelper;

import org.testng.annotations.Test;

public class StudentManagementTest extends BaseTest {

    @Test
    public void testStudentManagementCRUDWorkflow() {
        System.out.println("========================================");
        System.out.println("TEST: Student Management CRUD");
        System.out.println("========================================\n");

        StudentInfo student = DataLoader.generateRandomStudent();

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        StudentManagementPage studentPage = new StudentManagementPage(driver, wait, helper);

        // Login & navigate
        loginPage.loginAsAdmin();
        studentPage.navigateToStudentManagement();

        // ADD
        studentPage.addStudent(student);
        helper.delay(1000);

        // VERIFY ADD
        studentPage.searchStudent(student.studentCode);
        studentPage.verifyStudent(student);

        // EDIT
        String newAddress = DataLoader.generateRandomAddress();
        studentPage.editStudentAddress(student.studentCode, newAddress);
        helper.delay(1000);

        // VERIFY EDIT
        studentPage.searchStudent(student.studentCode);
        studentPage.verifyAddress(student.studentCode, newAddress);

        // DELETE
        studentPage.deleteStudent(student.studentCode);

        // VERIFY DELETE
        studentPage.verifyStudentDeleted(student.studentCode);

        System.out.println("\n========================================");
        System.out.println("TEST PASSED");
        System.out.println("========================================");
    }
}
