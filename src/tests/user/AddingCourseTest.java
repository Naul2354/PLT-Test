package tests.user;

import base.BaseTest;
import graphql.org.antlr.v4.runtime.atn.SemanticContext.AND;
import models.ChapterData;
import models.CourseData;
import models.LessonData;
import pages.CourseManagementPage;
import pages.LoginPage;
import pages.StudentManagementPage;
import utils.DataLoader;
import utils.SeleniumHelper;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddingCourseTest extends BaseTest {

    @Test
    public void testAddNewCourseWithLearnersAndContent() throws Exception {
        System.out.println("========================================");
        System.out.println("TEST: Add New Course Full Workflow");
        System.out.println("========================================\n");

        // Load test data
        List<CourseData> allCourses = DataLoader.loadCoursesFromJSON();

        // Select random course data
        CourseData selectedCourse = allCourses.get((int)(Math.random() * allCourses.size()));
        System.out.println("Course: " + selectedCourse.title);
        System.out.println("Chapters: " + (selectedCourse.chapters != null ? selectedCourse.chapters.size() : 0));

        SeleniumHelper helper = new SeleniumHelper(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        StudentManagementPage studentPage = new StudentManagementPage(driver, wait, helper);
        CourseManagementPage coursePage = new CourseManagementPage(driver, wait, helper);

        // ===== PART 1: Login & Get Student List =====
        System.out.println("\n===== PART 1: Get Student List =====");

        loginPage.loginAsAdmin();
        studentPage.navigateToStudentManagement();
        List<String[]> studentList = studentPage.getStudentListAndPrint();

        // Pick 3-4 random students (use email) for adding as learners later
        List<String> selectedLearners = new ArrayList<>();
        List<String> selectedLearnerCodes = new ArrayList<>();
        if (studentList.size() > 0) {
            List<String[]> shuffled = new ArrayList<>(studentList);
            Collections.shuffle(shuffled);
            int count = Math.min(4, shuffled.size());
            for (int i = 0; i < count; i++) {
                selectedLearnerCodes.add(shuffled.get(i)[0]);
                selectedLearners.add(shuffled.get(i)[1]);
            }
        }

        System.out.println("Selected " + selectedLearners.size() + " students to add as learners:");
        for (int i = 0; i < selectedLearners.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + selectedLearnerCodes.get(i) + " - " + selectedLearners.get(i));
        }

        // ===== PART 2: Create New Course =====
        System.out.println("\n===== PART 2: Create New Course =====");

        coursePage.navigateToCourseManagement();
        coursePage.clickAddNewCourse();
        coursePage.uploadCourseThumbnail();
        coursePage.fillCourseInfo(selectedCourse.title, selectedCourse.description);
        coursePage.submitNewCourse();

        // ===== PART 3: Verify Course In List =====
        System.out.println("\n===== PART 3: Verify Course In List =====");

        coursePage.navigateToCourseManagement();
        boolean courseFound = coursePage.verifyCourseInList(selectedCourse.title);
        Assert.assertTrue(courseFound, "Newly created course not found in course list!");
        coursePage.searchAndSelectCourse(selectedCourse.title);

//         ===== PART 4: Add Learners (from real student list) =====
        System.out.println("\n===== PART 4: Add Learners =====");

        for (String learnerEmail : selectedLearners) {
            coursePage.addLearner(learnerEmail);
        }

        // ===== PART 5: Add Course Content (chapters + lessons + materials) =====
        System.out.println("\n===== PART 5: Add Course Content =====");

        coursePage.clickCourseContentTab();

        if (selectedCourse.chapters != null) {
            for (ChapterData chapter : selectedCourse.chapters) {
                coursePage.addChapter(chapter);

                if (chapter.lessons != null) {
                    for (int i = 0; i < chapter.lessons.size(); i++) {
                        LessonData lesson = chapter.lessons.get(i);
                        coursePage.addLesson(lesson, i + 1);

                        if ("video".equals(lesson.materialType)) {
                            coursePage.addVideoMaterial(lesson.materialName, lesson.materialUrl);
                        } else if ("attachment".equals(lesson.materialType)) {
                            coursePage.addAttachmentMaterial(lesson.materialName);
                        }

                        coursePage.collapseLastExpandedLesson();
                    }
                }

                coursePage.collapseAllPanels();
            }

            coursePage.saveChapter();

            for (ChapterData chapter : selectedCourse.chapters) {
                coursePage.verifyChapterExists(chapter);
            }
        }

        // ===== PART 6: Add Forum (Diễn đàn thảo luận) =====
        System.out.println("\n===== PART 6: Add Forum =====");

        coursePage.clickForumTab();
        coursePage.addNewForum(selectedCourse.forumName, selectedCourse.forumDescription);
        coursePage.reloadAndVerifyForum(selectedCourse.forumName, selectedCourse.forumDescription);

        // ===== PART 7: Add Video Conference =====
        System.out.println("\n===== PART 7: Add Video Conference =====");

        coursePage.clickVideoConferenceTab();
        coursePage.addVideoConference(
            selectedCourse.videoConferenceYoutubeLink,
            selectedCourse.videoConferenceDescription,
            selectedCourse.videoConferenceMeetingLink
        );
        coursePage.reloadAndVerifyVideoConference(
            selectedCourse.videoConferenceYoutubeLink,
            selectedCourse.videoConferenceDescription,
            selectedCourse.videoConferenceMeetingLink
        );
        
        // ==== PART 8.1 : 
        access to : Đến trang người dùng 
        
        verify course in homepage 
        click course  will see " Nội dung môn học " 
        Display 2 chapter : 
        Chương 1 : ...
        Expand to see 2 lessons
        each chapter include chapter AND description , material
        first lessons have video ytb : verify title of video ytb
        -> click button ytb will see video ytb -> then click close 
        second lessons will have link install material 
        -> check tittle with Json file that right material we up in part 5
 
        verify and compare with JSON file
        Chương 2 : same with chương 1 .
        
        
        
       
        	

        // ===== PART 8: Search & Update Course =====
        System.out.println("\n===== PART 8: Search & Update Course =====");

        coursePage.navigateToCourseManagement();
        coursePage.searchCourse(selectedCourse.title);
        coursePage.clickCourseInfoTab();
        coursePage.updateCourseInfo(selectedCourse.updatedTitle, selectedCourse.updatedDescription);

        coursePage.navigateToCourseManagement();
        coursePage.verifyUpdatedCourseInList(selectedCourse.updatedTitle, selectedCourse.updatedDescription);

        // ===== PART 9: Search & Delete Course =====
//        System.out.println("\n===== PART 9: Search & Delete Course =====");

        // Search and open the updated course
        coursePage.navigateToCourseManagement();
        coursePage.searchCourse(selectedCourse.updatedTitle);

        // List learners and verify
        coursePage.listLearnersAndPrint();

        // Delete all learners
        coursePage.deleteAllLearners();

        // Reload and verify learners deleted
        coursePage.verifyLearnersDeleted();

        // Go back to course management and delete the course
        coursePage.navigateToCourseManagement();
        coursePage.deleteCourseFromList(selectedCourse.updatedTitle);

        // Verify course deleted
        coursePage.verifyCourseDeleted(selectedCourse.updatedTitle);

        // ===== SUMMARY =====
        System.out.println("\n========================================");
        System.out.println("ALL TESTS PASSED");
        System.out.println("========================================");
        System.out.println("Course created : " + selectedCourse.title);
        System.out.println("Learners added : " + selectedLearners.size());
        for (int i = 0; i < selectedLearners.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + selectedLearnerCodes.get(i) + " - " + selectedLearners.get(i));
        }
        if (selectedCourse.chapters != null) {
            System.out.println("Chapters added : " + selectedCourse.chapters.size());
            for (ChapterData ch : selectedCourse.chapters) {
                System.out.println("  - " + ch.title + " (" + (ch.lessons != null ? ch.lessons.size() : 0) + " lessons)");
            }
        }
        System.out.println("Forum added    : " + selectedCourse.forumName);
        System.out.println("Video Conf     : " + selectedCourse.videoConferenceDescription);
        System.out.println("Course updated : " + selectedCourse.title + " -> " + selectedCourse.updatedTitle);
        System.out.println("========================================\n");
    }
}
