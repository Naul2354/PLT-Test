package tests.user;

import base.BaseTest;
import models.ChapterInfo;
import pages.CourseContentPage;
import pages.LoginPage;
import utils.SeleniumHelper;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CourseExpandTest extends BaseTest {

    @Test
    public void testExpandCourseAndVerify() throws Exception {
        System.out.println("START TEST");

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        CourseContentPage courseContentPage = new CourseContentPage(driver, wait, helper);

        // Init log file
        courseContentPage.initLogFile();

        // Login as user
        loginPage.loginAsUser();
        helper.delay();

        // Open course
        courseContentPage.openCourse("//span[contains(text(),'Lập trình Web')]");

        // Collect data from website
        List<ChapterInfo> chaptersData = courseContentPage.collectChaptersAndLessons();

        // Write compare file
        courseContentPage.writeCompareFile(chaptersData);

        // Log and verify results
        courseContentPage.logAndVerifyResults(chaptersData);

        // Assert
        Assert.assertEquals(
            courseContentPage.getFailCount(),
            0,
            "There are failed validations. Please check log file."
        );
    }
}
