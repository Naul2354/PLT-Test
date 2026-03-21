package pages;

import models.ChapterInfo;
import utils.SeleniumHelper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
