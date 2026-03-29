package pages;

import models.ChapterData;
import models.ChapterInfo;
import models.CourseData;
import models.LessonData;
import utils.SeleniumHelper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseContentPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final SeleniumHelper helper;

    private AtomicInteger failCount = new AtomicInteger(0);
    private String logFilePath;

    public CourseContentPage(WebDriver driver, WebDriverWait wait, SeleniumHelper helper) {
        this.driver = driver;
        this.wait = wait;
        this.helper = helper;
    }

    public int getFailCount() {
        return failCount.get();
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void initLogFile() throws IOException {
        logFilePath = buildNextLogFilePath();
        Files.write(
                Paths.get(logFilePath),
                "=== LOG KIỂM TRA KHÓA HỌC LẬP TRÌNH WEB ===\n\n".getBytes("UTF-8"),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    public void openCourse(String courseXpath) {
        System.out.println("Opening course...");
        WebElement course = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath(courseXpath)));
        helper.safeClick(course);
    }

    public List<ChapterInfo> collectChaptersAndLessons() throws InterruptedException {
        List<ChapterInfo> chaptersData = new ArrayList<>();

        System.out.println("Getting all chapters...");
        List<WebElement> chapters = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector(".v-expansion-panel:not(.lessons-panel)")
                ));

        for (int i = 0; i < chapters.size(); i++) {
            chapters = driver.findElements(
                    By.cssSelector(".v-expansion-panel:not(.lessons-panel)")
            );

            WebElement chapter = chapters.get(i);
            WebElement chapterHeader =
                    chapter.findElement(By.cssSelector(
                            "button.v-expansion-panel-header div.white--text"));

            String chapterName = chapterHeader.getText().trim();
            System.out.println("Opening chapter " + (i + 1) + ": " + chapterName);

            ChapterInfo chapterInfo = new ChapterInfo(chapterName);
            helper.safeClick(chapterHeader);

            System.out.println("  Getting lessons...");
            List<WebElement> lessons = chapter.findElements(
                    By.cssSelector(
                            ".lessons-panel button.v-expansion-panel-header div.white--text"));

            for (WebElement lesson : lessons) {
                String lessonName = lesson.getText().trim();
                System.out.println("    Lesson: " + lessonName);
                chapterInfo.lessons.add(lessonName);
            }

            chaptersData.add(chapterInfo);
        }

        return chaptersData;
    }

    public void writeCompareFile(List<ChapterInfo> chapters) throws IOException {
        String reportsDir = getReportsDir();
        Files.createDirectories(Paths.get(reportsDir));
        String filePath = reportsDir + "/compare.txt";

        Files.deleteIfExists(Paths.get(filePath));

        StringBuilder content = new StringBuilder();
        content.append("DANH SÁCH CHƯƠNG & BÀI HỌC\n");
        content.append("\n");
        content.append("==============================\n");
        content.append("\n");

        for (int i = 0; i < chapters.size(); i++) {
            ChapterInfo chapter = chapters.get(i);

            String chapterTitle = chapter.chapterName.replaceAll("^Chương\\s*\\d+\\s*:\\s*", "");

            StringBuilder chapterLine = new StringBuilder();
            chapterLine.append("CHƯƠNG ").append(i + 1).append(" | ").append(chapterTitle);

            for (int j = 0; j < chapter.lessons.size(); j++) {
                String lessonName = chapter.lessons.get(j);
                String lessonTitle = lessonName.replaceAll("^Bài\\s*số\\s*\\d+\\s*:\\s*", "");
                chapterLine.append(" [BÀI ").append(j + 1).append("] ").append(lessonTitle);
            }

            String chapterLineStr = chapterLine.toString();

            if (chapterLineStr.length() > 100) {
                int lastSpace = chapterLineStr.lastIndexOf(" [BÀI", 100);
                if (lastSpace > 0) {
                    content.append(chapterLineStr.substring(0, lastSpace)).append("\n");
                    String remaining = chapterLineStr.substring(lastSpace + 1);
                    while (remaining.length() > 100) {
                        int nextSpace = remaining.indexOf(" [BÀI", 100);
                        if (nextSpace > 0) {
                            content.append(remaining.substring(0, nextSpace)).append("\n");
                            remaining = remaining.substring(nextSpace + 1);
                        } else {
                            break;
                        }
                    }
                    content.append(remaining);
                } else {
                    content.append(chapterLineStr);
                }
            } else {
                content.append(chapterLineStr);
            }

            content.append("\n\n");

            if (i < chapters.size() - 1) {
                content.append("------------------------------------------------------------------------\n\n");
            }
        }

        Files.write(Paths.get(filePath), content.toString().getBytes("UTF-8"));
        System.out.println("Compare file created: " + filePath);
    }

    public void logAndVerifyResults(List<ChapterInfo> chaptersData) throws Exception {
        Set<String> expectedData = readExpectedData();

        System.out.println("\n=== DETAILED COMPARISON RESULTS ===");

        for (ChapterInfo chapterInfo : chaptersData) {
            String normalizedChapter = normalize(chapterInfo.chapterName);
            boolean chapterPass = expectedData.contains(normalizedChapter);
            logResult("CHAPTER", chapterInfo.chapterName, chapterPass);

            for (String lessonName : chapterInfo.lessons) {
                String normalizedLesson = normalize(lessonName);
                boolean lessonPass = expectedData.contains(normalizedLesson);
                logResult("LESSON", lessonName, lessonPass);
            }
        }

        compareFiles();

        System.out.println("Total FAILED items = " + failCount.get());

        writeSummaryToLog();
    }

    // ==================== User View - Navigation ====================

    public void navigateToUserHomepage() {
        System.out.println("Navigating to user homepage...");
        driver.get("https://elearning.plt.pro.vn/trang-chu");
        helper.delay(2000);
    }

    public void navigateToAdminPage() {
        System.out.println("Navigating back to admin page...");
        WebElement adminLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[contains(@class,'v-list-item') and .//div[contains(@class,'v-list-item__title') and contains(text(),'Đến trang admin')]]")));
        helper.safeClick(adminLink);
        helper.delay(2000);
        System.out.println("Back to admin page");
    }

    public void findAndClickCourse(String courseTitle) {
        System.out.println("Looking for course on homepage: " + courseTitle);
        WebElement course = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[contains(text(),'" + courseTitle + "')]")));
        helper.safeClick(course);
        helper.delay(2000);

        // Verify "Nội dung môn học" is visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[contains(text(),'Nội dung môn học')]")));
        System.out.println("Course opened - 'Nội dung môn học' visible");
    }

    // ==================== User View - Verify Course Content ====================

    public void verifyCourseContent(CourseData courseData) {
        System.out.println("\n--- Verify Course Content (User View) ---");

        // Verify course title
        verifyTextOnPage(courseData.title, "Course Title");

        // Verify course description
        verifyTextOnPage(courseData.description, "Course Description");

        if (courseData.chapters == null) return;

        // Expand and verify each chapter
        List<WebElement> chapterPanels = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector(".v-expansion-panel:not(.lessons-panel)")));

        System.out.println("Found " + chapterPanels.size() + " chapter(s) on page");
        Assert.assertEquals(chapterPanels.size(), courseData.chapters.size(),
            "Chapter count mismatch!");

        for (int i = 0; i < courseData.chapters.size(); i++) {
            ChapterData expectedChapter = courseData.chapters.get(i);
            System.out.println("\n  === Chương " + (i + 1) + ": " + expectedChapter.title + " ===");

            // Re-fetch panels (DOM may change after expand)
            chapterPanels = driver.findElements(
                By.cssSelector(".v-expansion-panel:not(.lessons-panel)"));
            WebElement chapterPanel = chapterPanels.get(i);

            // Click to expand chapter first (title/description may only be visible after expand)
            WebElement chapterHeader = chapterPanel.findElement(
                By.cssSelector("button.v-expansion-panel-header"));
            helper.safeClick(chapterHeader);
            helper.delay(1500);

            // Verify chapter title
            verifyTextOnPage(expectedChapter.title, "Chapter " + (i + 1) + " Title");

            // Verify chapter description
            verifyTextOnPage(expectedChapter.description, "Chapter " + (i + 1) + " Description");

            // Verify lessons
            if (expectedChapter.lessons != null) {
                // Get lesson panels inside this chapter
                List<WebElement> lessonPanels = chapterPanel.findElements(
                    By.cssSelector(".lessons-panel"));

                for (int j = 0; j < expectedChapter.lessons.size(); j++) {
                    LessonData expectedLesson = expectedChapter.lessons.get(j);
                    System.out.println("    Bài số " + (j + 1) + ": " + expectedLesson.title);

                    // Verify lesson title (visible on header)
                    verifyTextOnPage(expectedLesson.title, "Lesson " + (j + 1) + " Title");

                    // Expand lesson to see description and material
                    if (j < lessonPanels.size()) {
                        WebElement lessonHeader = lessonPanels.get(j).findElement(
                            By.cssSelector("button.v-expansion-panel-header"));
                        helper.safeClick(lessonHeader);
                        helper.delay(1000);
                    }

                    // Verify lesson description (visible after expand)
                    verifyTextOnPage(expectedLesson.description, "Lesson " + (j + 1) + " Description");

                    // Verify material name
                    if (expectedLesson.materialName != null) {
                        verifyTextOnPage(expectedLesson.materialName, "Lesson " + (j + 1) + " Material");
                    }

                    // Collapse lesson back
                    if (j < lessonPanels.size()) {
                        WebElement lessonHeader = lessonPanels.get(j).findElement(
                            By.cssSelector("button.v-expansion-panel-header"));
                        helper.safeClick(lessonHeader);
                        helper.delay(500);
                    }
                }
            }
        }

        System.out.println("\n--- Course Content Verification Complete ---");
    }

    private void verifyTextOnPage(String expectedText, String label) {
        String snippet = expectedText.substring(0, Math.min(40, expectedText.length()));
        // Use normalize-space(.) to match text spread across child elements
        List<WebElement> elements = driver.findElements(
            By.xpath("//*[contains(normalize-space(.),'" + snippet + "')]"));
        if (!elements.isEmpty()) {
            System.out.println("[PASS] " + label + ": " + expectedText);
        } else {
            System.out.println("[FAIL] " + label + ": " + expectedText + " - NOT FOUND");
            Assert.fail(label + " not found on page: " + expectedText);
        }
    }

    // ==================== User View - Forum ====================

    public void clickForumTabUserView() {
        System.out.println("\nOpening forum tab (user view)...");
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[@role='tab' and contains(., 'Diễn đàn thảo luận')]")));
        helper.safeClick(tab);
        helper.delay(1500);
    }

    public void verifyForumUserView(String expectedName, String expectedDescription) {
        System.out.println("\n--- Verify Forum (User View) ---");
        System.out.println("[Expected] Name       : " + expectedName);
        System.out.println("[Expected] Description : " + expectedDescription);

        // Verify forum name
        List<WebElement> nameElements = driver.findElements(
            By.xpath("//*[contains(text(),'" + expectedName + "')]"));
        if (!nameElements.isEmpty()) {
            String actualName = nameElements.get(0).getText().trim();
            System.out.println("[Actual]   Name       : " + actualName);
            boolean nameMatch = actualName.contains(expectedName);
            System.out.println("[Result]   Name       : " + (nameMatch ? "PASS" : "FAIL"));
            Assert.assertTrue(nameMatch, "Forum name mismatch! Expected: " + expectedName);
        } else {
            System.out.println("[Actual]   Name       : NOT FOUND");
            System.out.println("[Result]   Name       : FAIL");
            Assert.fail("Forum name not found: " + expectedName);
        }

        // Verify description
        String descSnippet = expectedDescription.substring(0, Math.min(30, expectedDescription.length()));
        List<WebElement> descElements = driver.findElements(
            By.xpath("//*[contains(text(),'" + descSnippet + "')]"));
        if (!descElements.isEmpty()) {
            String actualDesc = descElements.get(0).getText().trim();
            System.out.println("[Actual]   Description : " + actualDesc);
            boolean descMatch = actualDesc.contains(descSnippet);
            System.out.println("[Result]   Description : " + (descMatch ? "PASS" : "FAIL"));
            Assert.assertTrue(descMatch, "Forum description mismatch!");
        } else {
            System.out.println("[Actual]   Description : NOT FOUND");
            System.out.println("[Result]   Description : FAIL");
            Assert.fail("Forum description not found: " + expectedDescription);
        }
    }

    public void addForumComment(String comment) {
        System.out.println("Adding comment: " + comment);

        // Find input for comment
        WebElement commentInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//input[@placeholder='Trả lời diễn đàn']")));
        commentInput.clear();
        commentInput.sendKeys(comment);
        helper.delay(500);

        // Click send button (mdi-send icon)
        WebElement sendBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[.//i[contains(@class,'mdi-send')]] | //i[contains(@class,'mdi-send')]")));
        helper.safeClick(sendBtn);
        helper.delay(2000);

        // Wait for comment to display in UI
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[contains(text(),'" + comment + "')]")));
        System.out.println("Comment displayed in UI: " + comment);
    }

    // ==================== User View - Video Conference ====================

    public void clickVideoConferenceTabUserView() {
        System.out.println("\nOpening video conference tab (user view)...");
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[@role='tab' and contains(., 'Video conference')]")));
        helper.safeClick(tab);
        helper.delay(1500);
    }

    public void verifyVideoConferenceUserView(String expectedYoutubeLink, String expectedDescription) {
        System.out.println("\n--- Verify Video Conference (User View) ---");
        System.out.println("[Expected] YouTube     : " + expectedYoutubeLink);
        System.out.println("[Expected] Description : " + expectedDescription);

        // Verify YouTube link
        List<WebElement> ytElements = driver.findElements(
            By.xpath("//*[contains(@href,'" + expectedYoutubeLink + "') or contains(text(),'" + expectedYoutubeLink + "') or contains(@src,'" + expectedYoutubeLink + "')]"));
        if (!ytElements.isEmpty()) {
            System.out.println("[Actual]   YouTube     : FOUND");
            System.out.println("[Result]   YouTube     : PASS");
        } else {
            System.out.println("[Actual]   YouTube     : NOT FOUND");
            System.out.println("[Result]   YouTube     : FAIL");
            Assert.fail("Video conference YouTube link not found: " + expectedYoutubeLink);
        }

        // Verify description
        String descSnippet = expectedDescription.substring(0, Math.min(20, expectedDescription.length()));
        List<WebElement> descElements = driver.findElements(
            By.xpath("//*[contains(text(),'" + descSnippet + "')]"));
        if (!descElements.isEmpty()) {
            String actualDesc = descElements.get(0).getText().trim();
            System.out.println("[Actual]   Description : " + actualDesc);
            boolean descMatch = actualDesc.contains(descSnippet);
            System.out.println("[Result]   Description : " + (descMatch ? "PASS" : "FAIL"));
            Assert.assertTrue(descMatch, "Video conference description mismatch!");
        } else {
            System.out.println("[Actual]   Description : NOT FOUND");
            System.out.println("[Result]   Description : FAIL");
            Assert.fail("Video conference description not found: " + expectedDescription);
        }
    }

    // --- Private helpers ---

    private String normalize(String text) {
        text = text.toLowerCase()
                   .replaceAll("bài\\s*số\\s*\\d+\\s*:", "")
                   .replace("chương", "chuong");

        String temp = Normalizer.normalize(text, Normalizer.Form.NFD);
        temp = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                      .matcher(temp)
                      .replaceAll("");

        temp = temp.replaceAll("[^a-z0-9]", "");
        return temp.trim();
    }

    private Set<String> readExpectedData() throws Exception {
        String projectPath = System.getProperty("user.dir");
        String filePath = projectPath + "/src/resources/data.txt";

        System.out.println("Loading expected data from: " + filePath);

        List<String> lines = Files.readAllLines(Paths.get(filePath));
        Set<String> expected = new HashSet<>();

        StringBuilder block = new StringBuilder();

        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.isEmpty()
                    || line.startsWith("===")
                    || line.startsWith("---")
                    || line.startsWith("DANH SÁCH")) {
                if (block.length() > 0) {
                    processDataBlock(block.toString(), expected);
                    block.setLength(0);
                }
                continue;
            }

            if (line.toUpperCase().startsWith("CHƯƠNG")) {
                if (block.length() > 0) {
                    processDataBlock(block.toString(), expected);
                    block.setLength(0);
                }
            }

            if (block.length() > 0) {
                block.append(' ');
            }
            block.append(line);
        }

        if (block.length() > 0) {
            processDataBlock(block.toString(), expected);
        }

        System.out.println("Total expected items = " + expected.size());
        return expected;
    }

    private void processDataBlock(String block, Set<String> expected) {
        if (block.toUpperCase().contains("CHƯƠNG")) {
            String[] parts = block.split("\\[BÀI");

            String chapterPart = parts[0].trim();
            if (!chapterPart.isEmpty()) {
                expected.add(normalize(chapterPart));
            }

            for (int i = 1; i < parts.length; i++) {
                String lesson = parts[i].replaceAll("\\d+\\]", "").trim();
                if (!lesson.isEmpty()) {
                    expected.add(normalize(lesson));
                }
            }
        } else {
            String[] parts = block.split("\\[BÀI");
            for (int i = 1; i < parts.length; i++) {
                String lesson = parts[i].replaceAll("\\d+\\]", "").trim();
                if (!lesson.isEmpty()) {
                    expected.add(normalize(lesson));
                }
            }
        }
    }

    private void logResult(String type, String name, boolean pass) {
        String line;
        if (pass) {
            line = "[PASS] " + type + " = " + name;
        } else {
            line = "[FAIL] " + type + " = " + name;
            failCount.incrementAndGet();
        }

        System.out.println(line);

        try {
            if (logFilePath != null) {
                Files.write(
                        Paths.get(logFilePath),
                        (line + System.lineSeparator()).getBytes("UTF-8"),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            }
        } catch (IOException e) {
            System.out.println("Cannot write to log file: " + e.getMessage());
        }
    }

    private void compareFiles() throws Exception {
        String reportsDir = getReportsDir();
        Files.createDirectories(Paths.get(reportsDir));
        String comparePath = reportsDir + "/compare.txt";
        String projectPath = System.getProperty("user.dir");
        String dataPath = projectPath + "/src/resources/data.txt";

        System.out.println("Comparing files:");
        System.out.println("  Actual (compare): " + comparePath);
        System.out.println("  Expected (data): " + dataPath);

        Set<String> compareData = readDataFromFile(comparePath);
        Set<String> expectedData = readDataFromFile(dataPath);

        Set<String> missingInCompare = new HashSet<>(expectedData);
        missingInCompare.removeAll(compareData);

        Set<String> extraInCompare = new HashSet<>(compareData);
        extraInCompare.removeAll(expectedData);

        StringBuilder diffLog = new StringBuilder();

        if (missingInCompare.isEmpty() && extraInCompare.isEmpty()) {
            String msg = "Files match 100%.";
            System.out.println(msg);
            diffLog.append(msg).append(System.lineSeparator());
        } else {
            if (!missingInCompare.isEmpty()) {
                String msg = "Missing in compare.txt (" + missingInCompare.size() + " items): " + missingInCompare;
                System.out.println(msg);
                diffLog.append(msg).append(System.lineSeparator());
            }
            if (!extraInCompare.isEmpty()) {
                String msg = "Extra in compare.txt (" + extraInCompare.size() + " items): " + extraInCompare;
                System.out.println(msg);
                diffLog.append(msg).append(System.lineSeparator());
            }
        }

        if (logFilePath != null && diffLog.length() > 0) {
            Files.write(
                    Paths.get(logFilePath),
                    (System.lineSeparator() + "=== FILE DIFFERENCES ===" + System.lineSeparator()
                            + diffLog).getBytes("UTF-8"),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        }
    }

    private Set<String> readDataFromFile(String filePath) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        Set<String> data = new HashSet<>();

        StringBuilder block = new StringBuilder();

        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.isEmpty()
                    || line.startsWith("===")
                    || line.startsWith("---")
                    || line.startsWith("DANH SÁCH")) {
                if (block.length() > 0) {
                    processDataBlock(block.toString(), data);
                    block.setLength(0);
                }
                continue;
            }

            if (line.toUpperCase().startsWith("CHƯƠNG")) {
                if (block.length() > 0) {
                    processDataBlock(block.toString(), data);
                    block.setLength(0);
                }
            }

            if (block.length() > 0) {
                block.append(' ');
            }
            block.append(line);
        }

        if (block.length() > 0) {
            processDataBlock(block.toString(), data);
        }

        return data;
    }

    private void writeSummaryToLog() throws IOException {
        if (logFilePath == null) return;

        StringBuilder summary = new StringBuilder();
        summary.append(System.lineSeparator())
               .append("=== SUMMARY ===").append(System.lineSeparator())
               .append("Total FAILED items = ").append(failCount.get()).append(System.lineSeparator());

        if (failCount.get() > 0) {
            summary.append(System.lineSeparator())
                   .append("java.lang.AssertionError: There are failed validations. Check console log. ")
                   .append("expected [0] but found [").append(failCount.get()).append("]")
                   .append(System.lineSeparator())
                   .append("    at tests.user.CourseExpandTest.testExpandCourseAndVerify(CourseExpandTest.java)")
                   .append(System.lineSeparator());
        }

        Files.write(
                Paths.get(logFilePath),
                summary.toString().getBytes("UTF-8"),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    private String getReportsDir() {
        return System.getProperty("user.dir") + "/test-reports";
    }

    private String buildNextLogFilePath() throws IOException {
        String reportsDir = getReportsDir();
        Files.createDirectories(Paths.get(reportsDir));

        String className = "CourseExpandTest";
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));

        Pattern p = Pattern.compile(Pattern.quote(className) + "_" + Pattern.quote(dateStr) + "_(\\d{2})\\.txt");
        int maxSeq = 0;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(reportsDir), className + "_" + dateStr + "_*.txt")) {
            for (Path f : stream) {
                String fileName = f.getFileName().toString();
                Matcher m = p.matcher(fileName);
                if (m.matches()) {
                    int seq = Integer.parseInt(m.group(1));
                    if (seq > maxSeq) {
                        maxSeq = seq;
                    }
                }
            }
        }

        int nextSeq = maxSeq + 1;
        String seqStr = String.format("%02d", nextSeq);
        return reportsDir + "/" + className + "_" + dateStr + "_" + seqStr + ".txt";
    }
}
