package tests.admin;

import base.BaseTest;
import models.StudentInfo;
import pages.LoginPage;
import pages.StudentManagementPage;
import utils.DataLoader;
import utils.SeleniumHelper;

import org.testng.annotations.Test;

import java.util.List;

public class StudentManagementTest extends BaseTest {

    @Test
    public void testStudentManagementCRUDWorkflow() throws Exception {
        System.out.println("========================================");
        System.out.println("TEST: Student Management CRUD");
        System.out.println("========================================\n");

        // Load student data from JSON
        List<StudentInfo> allStudents = DataLoader.loadStudentsFromJSON();
        StudentInfo student = allStudents.get((int)(Math.random() * allStudents.size()));

        System.out.println("Selected student from JSON:");
        System.out.println("  Name   : " + student.fullName);
        System.out.println("  Code   : " + student.studentCode);
        System.out.println("  Email  : " + student.email);
        System.out.println("  Phone  : " + student.phone);
        System.out.println("  DOB    : " + student.dob);
        System.out.println("  Address: " + student.address);
        System.out.println("  Gender : " + student.gender);
        System.out.println("  New Adr: " + student.newAddress);

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        StudentManagementPage studentPage = new StudentManagementPage(driver, wait, helper);

        // Login & navigate
        loginPage.loginAsAdmin();
        studentPage.navigateToStudentManagement();

        // ADD
        studentPage.addStudent(student);
        helper.delay(1000);

        // PRINT STUDENT LIST (verify student just added)
        System.out.println("\n--- Student list after ADD ---");
        studentPage.getStudentListAndPrint();

        // VERIFY ADD
        studentPage.searchStudent(student.studentCode);
        studentPage.verifyStudent(student);

        // EDIT (address from JSON)
        studentPage.editStudentAddress(student.studentCode, student.newAddress);
        helper.delay(1000);

        // VERIFY EDIT
        studentPage.searchStudent(student.studentCode);
        studentPage.verifyAddress(student.studentCode, student.newAddress);

        // DELETE
        studentPage.deleteStudent(student.studentCode);

        // VERIFY DELETE
        studentPage.verifyStudentDeleted(student.studentCode);

        System.out.println("\n========================================");
        System.out.println("TEST PASSED");
        System.out.println("========================================");
        System.out.println("Student added  : " + student.fullName + " (" + student.studentCode + ")");
        System.out.println("Address edited : " + student.newAddress);
        System.out.println("Student deleted: " + student.studentCode);
        System.out.println("========================================");
    }
}
