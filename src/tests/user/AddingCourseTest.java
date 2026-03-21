package tests.user;

import base.BaseTest;
import models.ChapterData;
import models.LessonData;
import pages.CourseManagementPage;
import pages.LoginPage;
import utils.DataLoader;
import utils.SeleniumHelper;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AddingCourseTest extends BaseTest {

    @Test
    public void testAddingCourseContent() throws Exception {
        System.out.println("========================================");
        System.out.println("TEST: Adding Course Content");
        System.out.println("========================================\n");

        // Load test data
        List<ChapterData> allChapters = DataLoader.loadChaptersFromJSON();
        List<LessonData> allLessons = DataLoader.loadLessonsFromJSON();

        // Select random chapter
        Random random = new Random();
        ChapterData selectedChapter = allChapters.get(random.nextInt(allChapters.size()));
        System.out.println("Selected chapter: " + selectedChapter.title);

        // Select 2 random lessons
        List<LessonData> selectedLessons = new ArrayList<>();
        List<Integer> usedIndices = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            int randomIndex;
            do {
                randomIndex = random.nextInt(allLessons.size());
            } while (usedIndices.contains(randomIndex));

            usedIndices.add(randomIndex);
            selectedLessons.add(allLessons.get(randomIndex));
        }

        System.out.println("Selected " + selectedLessons.size() + " lessons\n");

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        CourseManagementPage coursePage = new CourseManagementPage(driver, wait, helper);

        // Execute test
        loginPage.loginAsAdmin();
        coursePage.navigateToCourseManagement();
        String courseName = coursePage.selectRandomCourse();
        coursePage.clickCourseContentTab();

        coursePage.addChapter(selectedChapter);

        for (int i = 0; i < selectedLessons.size(); i++) {
            coursePage.addLesson(selectedLessons.get(i), i + 1);
        }

        coursePage.collapseAllPanels();
        coursePage.saveChapter();

        coursePage.verifyChapterExists(selectedChapter);
        coursePage.verifyLessonsExist(selectedLessons);

        System.out.println("\n========================================");
        System.out.println("TEST PASSED");
        System.out.println("========================================");
        System.out.println("Course: " + courseName);
        System.out.println("Chapter added: " + selectedChapter.title);
        System.out.println("Lessons added: " + selectedLessons.size());
        System.out.println("========================================\n");
    }
}
