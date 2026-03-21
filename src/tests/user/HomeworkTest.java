package tests.user;

import base.BaseTest;
import models.HomeworkData;
import models.QuestionData;
import pages.HomeworkManagementPage;
import pages.LoginPage;
import utils.DataLoader;
import utils.SeleniumHelper;

import org.testng.annotations.Test;

public class HomeworkTest extends BaseTest {

    @Test
    public void testAddHomeworkWithAllQuestionTypes() throws Exception {
        System.out.println("========================================");
        System.out.println("TEST: Add Homework with Questions");
        System.out.println("========================================\n");

        // Load test data
        HomeworkData homework = DataLoader.loadHomeworkFromJSON();

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        HomeworkManagementPage homeworkPage = new HomeworkManagementPage(driver, wait, helper);

        // Login & navigate
        loginPage.loginAsAdmin();
        homeworkPage.navigateToHomeworkManagement();

        // Click Add New
        homeworkPage.clickAddNew();

        // Input homework name
        homeworkPage.inputHomeworkName(homework.homeworkName);

        // Upload thumbnail
        homeworkPage.uploadHomeworkThumbnail();

        // Add each question by type
        for (int i = 0; i < homework.questions.size(); i++) {
            QuestionData question = homework.questions.get(i);
            System.out.println("\n[Question " + (i + 1) + "/" + homework.questions.size()
                             + "] Type: " + question.type);
            homeworkPage.addQuestion(question, i);
        }

        // Save
        homeworkPage.clickSave();

        System.out.println("\n========================================");
        System.out.println("TEST PASSED");
        System.out.println("========================================");
        System.out.println("Homework: " + homework.homeworkName);
        System.out.println("Questions added: " + homework.questions.size());
        System.out.println("========================================\n");
    }
}
