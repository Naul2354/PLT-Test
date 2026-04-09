package tests.user;

import base.BaseTest;
import models.HomeworkData;
import models.QuestionData;
import org.testng.Assert;
import pages.HomeworkManagementPage;
import pages.LoginPage;
import utils.DataLoader;
import utils.SeleniumHelper;

import org.testng.annotations.Test;

import java.util.List;

public class HomeworkTest extends BaseTest {

    @Test
    public void testAddUpdateAndDeleteHomework() throws Exception {
        System.out.println("========================================");
        System.out.println("TEST: Add, Update & Delete Homework");
        System.out.println("========================================\n");

        // Load test data
        HomeworkData homework = DataLoader.loadHomeworkFromJSON();

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        HomeworkManagementPage homeworkPage = new HomeworkManagementPage(driver, wait, helper);

        // Login & navigate
        loginPage.loginAsAdmin();
        homeworkPage.navigateToHomeworkManagement();

        // Capture initial homework list (for compare after delete)
        List<String> listBefore = homeworkPage.getHomeworkListAndPrint("BEFORE ADD");

        // ===== PART 1: Add Homework =====
        System.out.println("\n===== PART 1: Add Homework =====");

        homeworkPage.clickAddNew();
        homeworkPage.inputHomeworkName(homework.homeworkName);
        homeworkPage.uploadHomeworkThumbnail();

        for (int i = 0; i < homework.questions.size(); i++) {
            QuestionData question = homework.questions.get(i);
            System.out.println("\n[Question " + (i + 1) + "/" + homework.questions.size()
                             + "] Type: " + question.type);
            homeworkPage.addQuestion(question, i);
        }

        homeworkPage.clickSave();

        // ===== PART 2: Navigate Back, Reload & Verify =====
        System.out.println("\n===== PART 2: Reload & Verify Homework =====");

        homeworkPage.navigateToHomeworkManagement();
        homeworkPage.clickReload();
        homeworkPage.verifyHomeworkInList(homework.homeworkName);

        // ===== PART 3: Update Homework =====
        System.out.println("\n===== PART 3: Update Homework =====");

        homeworkPage.clickEditHomework(homework.homeworkName);

        // Update name and thumbnail
        homeworkPage.updateHomeworkName(homework.updatedHomeworkName);
        homeworkPage.updateHomeworkThumbnail();

        // Update each question (expand, replace content & answers, collapse)
        if (homework.updatedQuestions != null) {
            for (int i = 0; i < homework.updatedQuestions.size(); i++) {
                QuestionData updatedQ = homework.updatedQuestions.get(i);
                System.out.println("\n[Updating Question " + (i + 1) + "/" + homework.updatedQuestions.size()
                                 + "] Type: " + updatedQ.type);

                homeworkPage.expandQuestionPanel(i);
                homeworkPage.updateQuestionContent(updatedQ.content);
                homeworkPage.updateAnswers(updatedQ.answers);
                homeworkPage.collapseQuestionPanel(i);
            }
        }

        // Save updated homework
        homeworkPage.clickSave();

        // ===== PART 4: Verify Updated Homework =====
        System.out.println("\n===== PART 4: Verify Updated Homework =====");

        homeworkPage.navigateToHomeworkManagement();
        homeworkPage.clickReload();
        homeworkPage.verifyHomeworkInList(homework.updatedHomeworkName);

        // ===== PART 5: Delete Homework =====
        System.out.println("\n===== PART 5: Delete Homework =====");

        homeworkPage.deleteHomework(homework.updatedHomeworkName);

        // Reload and capture list after delete
        homeworkPage.clickReload();
        List<String> listAfter = homeworkPage.getHomeworkListAndPrint("AFTER DELETE");

        // Compare: listAfter should NOT contain the updated homework name
        Assert.assertFalse(listAfter.contains(homework.updatedHomeworkName),
            "Homework still exists after delete: " + homework.updatedHomeworkName);

        // Compare: listAfter size should match listBefore size (we added 1 then deleted 1)
        System.out.println("\nList size BEFORE add : " + listBefore.size());
        System.out.println("List size AFTER delete: " + listAfter.size());
        Assert.assertEquals(listAfter.size(), listBefore.size(),
            "List size mismatch! Before=" + listBefore.size() + ", After=" + listAfter.size());

        System.out.println("[PASS] Homework successfully deleted");

        // ===== SUMMARY =====
        System.out.println("\n========================================");
        System.out.println("ALL TESTS PASSED");
        System.out.println("========================================");
        System.out.println("Homework added   : " + homework.homeworkName);
        System.out.println("Questions added  : " + homework.questions.size());
        System.out.println("Homework updated : " + homework.updatedHomeworkName);
        System.out.println("Questions updated: " + (homework.updatedQuestions != null ? homework.updatedQuestions.size() : 0));
        System.out.println("Homework deleted : " + homework.updatedHomeworkName);
        System.out.println("List size match  : " + listBefore.size() + " == " + listAfter.size());
        System.out.println("========================================\n");
    }
}
