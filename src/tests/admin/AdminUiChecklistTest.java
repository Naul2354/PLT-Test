package tests.admin;

import base.BaseTest;
import org.testng.annotations.Test;
import pages.AdminUiChecklistPage;
import pages.LoginPage;

import java.util.Arrays;

public class AdminUiChecklistTest extends BaseTest {

	@Test
	public void testAdminUiAndListChecklist() {

	    LoginPage loginPage = new LoginPage(driver, wait);
	    AdminUiChecklistPage checklistPage = new AdminUiChecklistPage(driver, wait);

	    loginPage.loginAsAdmin();

	    // =========================
	    // 2.1 LEFT MENU
	    // =========================
	    System.out.println("===== 2.1 LEFT MENU =====");
	    checklistPage.assertLeftMenuOrder();


	    // =========================
	    // 2.2 STUDENT TABLE
	    // =========================
	    System.out.println("===== 2.2 STUDENT TABLE =====");

	    checklistPage.goToStudentManagement();

	    System.out.println("Expected: [Mã học viên, Tên đệm, Tên, SĐT, Email, Ngày sinh, Giới tính, Địa chỉ, Ngày cập nhật]");

	    checklistPage.assertTableHeaders(Arrays.asList(
	            "Mã học viên", "Tên đệm", "Tên", "SĐT", "Email",
	            "Ngày sinh", "Giới tính", "Địa chỉ", "Ngày cập nhật"
	    ));


	    // =========================
	    // 2.2 COURSE TABLE
	    // =========================
	    System.out.println("===== 2.2 COURSE TABLE =====");

	    checklistPage.goToCourseManagement();

	    System.out.println("Expected: [Tiêu đề, Mô tả, Ngày cập nhật]");

	    checklistPage.assertTableHeaders(Arrays.asList(
	            "Tiêu đề", "Mô tả", "Ngày cập nhật"
	    ));


	    // =========================
	    // 2.2 HOMEWORK TABLE
	    // =========================
	    System.out.println("===== 2.2 HOMEWORK TABLE =====");

	    checklistPage.goToHomeworkManagement();

	    System.out.println("Expected: [Tiêu đề, Số câu hỏi, Người tạo, Ngày được tạo, Ngày cập nhật]");

	    checklistPage.assertTableHeaders(Arrays.asList(
	            "Tiêu đề", "Số câu hỏi", "Người tạo",
	            "Ngày được tạo", "Ngày cập nhật"
	    ));


	    // =========================
	    // 2.3 SORTING
	    // =========================
	    System.out.println("===== 2.3 SORTING =====");

	    // STUDENT
	    System.out.println("---- STUDENT SORTING ----");

	    checklistPage.goToStudentManagement();

	    checklistPage.assertSortingByColumnSafe("Mã học viên", 0);
	    checklistPage.assertSortingByColumnSafe("Tên đệm", 1);
	    checklistPage.assertSortingByColumnSafe("Tên", 2);
	    checklistPage.assertSortingByColumnSafe("SĐT", 3);
	    checklistPage.assertSortingByColumnSafe("Email", 4);
	    checklistPage.assertSortingByColumnSafe("Ngày sinh", 5);
	    checklistPage.assertSortingByColumnSafe("Giới tính", 6);
	    checklistPage.assertSortingByColumnSafe("Địa chỉ", 7);
	    checklistPage.assertSortingByColumnSafe("Ngày cập nhật", 8);


	    // COURSE
	    System.out.println("---- COURSE SORTING ----");

	    checklistPage.goToCourseManagement();

	    // bỏ cột icon (index 0)
	    checklistPage.assertSortingByColumnSafe("Tiêu đề", 1);
	    checklistPage.assertSortingByColumnSafe("Mô tả", 2);
	    checklistPage.assertSortingByColumnSafe("Ngày cập nhật", 3);


	    // HOMEWORK
	    System.out.println("---- HOMEWORK SORTING ----");

	    checklistPage.goToHomeworkManagement();

	    checklistPage.assertSortingByColumnSafe("Tiêu đề", 0);
	    checklistPage.assertSortingByColumnSafe("Số câu hỏi", 1);
	    checklistPage.assertSortingByColumnSafe("Người tạo", 2);
	    checklistPage.assertSortingByColumnSafe("Ngày được tạo", 3);
	    checklistPage.assertSortingByColumnSafe("Ngày cập nhật", 4);


	    // =========================
	    // 3. IMAGE CHECK - LOGO
	    // =========================
	    System.out.println("===== 3. LOGO IMAGE COMPARE =====");
	    System.out.println("Expected: Logo hiển thị đúng với ảnh chuẩn trong src/resources");

	    checklistPage.assertSystemLogoMatches("src/resources/logo_app.png");


	    // =========================
	    // BUTTON COLOR
	    // =========================
	    checklistPage.goToStudentManagement();
	    checklistPage.assertPrimaryButtonColor("rgba(12, 79, 121, 1)");


	    // =========================
	    // RESPONSIVE
	    // =========================
	    System.out.println("\n==================================================");
	    System.out.println("[TEST] Admin UI Checklist - Responsive Validation");
	    System.out.println("==================================================");

	    checklistPage.checkResponsiveAllPages();

	    System.out.println("==================================================");
	    System.out.println("[TEST END] Admin UI Checklist completed");
	    System.out.println("==================================================\n");
	}}