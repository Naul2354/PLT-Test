package tests.user;

import base.BaseTest;
import models.CourseData;
import pages.CourseContentPage;
import pages.LoginPage;
import utils.DataLoader;
import utils.SeleniumHelper;

import org.testng.annotations.Test;

import java.util.List;

public class UserViewForumVCTest extends BaseTest {

    @Test
    public void testUserViewForumAndVideoConference() throws Exception {
        System.out.println("========================================");
        System.out.println("TEST: User View - Verify Forum & Video Conference");
        System.out.println("========================================\n");

        // Load test data - hardcode "Swift iOS Development" for testing
        List<CourseData> allCourses = DataLoader.loadCoursesFromJSON();
        CourseData selectedCourse = allCourses.stream()
            .filter(c -> "Swift iOS Development".equals(c.title))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Course 'Swift iOS Development' not found in JSON!"));
        System.out.println("Course: " + selectedCourse.title);
        System.out.println("Forum : " + selectedCourse.forumName);
        System.out.println("VC    : " + selectedCourse.videoConferenceDescription);

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        CourseContentPage courseContentPage = new CourseContentPage(driver, wait, helper);

        // ===== Login as admin (who is also a learner) =====
        System.out.println("\n===== Login =====");
        loginPage.loginAsAdmin();

        // ===== Navigate to user homepage =====
        System.out.println("\n===== Navigate to User Homepage =====");
        courseContentPage.navigateToUserHomepage();

        // ===== Find and click the course =====
        System.out.println("\n===== Open Course =====");
        courseContentPage.findAndClickCourse(selectedCourse.title);

        // ===== Verify Course Content (chapters, lessons, materials) =====
        System.out.println("\n===== Verify Course Content =====");
        courseContentPage.verifyCourseContent(selectedCourse);

        // ===== Verify Forum =====
        System.out.println("\n===== Verify Forum =====");
        courseContentPage.clickForumTabUserView();
        courseContentPage.verifyForumUserView(selectedCourse.forumName, selectedCourse.forumDescription);

        // Add a random comment and send
        String randomComment = "Test comment " + System.currentTimeMillis();
        courseContentPage.addForumComment(randomComment);

        // ===== Verify Video Conference =====
        System.out.println("\n===== Verify Video Conference =====");
        courseContentPage.clickVideoConferenceTabUserView();
        courseContentPage.verifyVideoConferenceUserView(
            selectedCourse.videoConferenceYoutubeLink,
            selectedCourse.videoConferenceDescription
        );

        // ===== Navigate back to admin =====
        courseContentPage.navigateToAdminPage();

        // ===== SUMMARY =====
        System.out.println("\n========================================");
        System.out.println("ALL TESTS PASSED");
        System.out.println("========================================");
        System.out.println("Course         : " + selectedCourse.title);
        System.out.println("Forum verified : " + selectedCourse.forumName);
        System.out.println("Comment sent   : " + randomComment);
        System.out.println("VC verified    : " + selectedCourse.videoConferenceDescription);
        System.out.println("========================================\n");
    }
}
