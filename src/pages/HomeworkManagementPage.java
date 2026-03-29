package pages;

import models.QuestionData;
import utils.SeleniumHelper;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.List;

public class HomeworkManagementPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final SeleniumHelper helper;

    // File paths for uploads
    private static final String IMAGE_FILE = System.getProperty("user.dir") + "/src/image/js.jpg";
    private static final String AUDIO_FILE = System.getProperty("user.dir") + "/src/image/file_test_selenium_test.mp4";

    // Locators
    private static final By BTN_THEM_MOI =
        By.cssSelector("#v-main-app > div > div > div > div.v-data-table > header > div > a");
    private static final By INPUT_TEN_BAI_TAP =
        By.xpath("//label[contains(text(),'Tên bài tập')]/ancestor::div[contains(@class,'v-input')]//input");
    private static final By FORM_FILE_INPUT =
        By.cssSelector("#formUpdateAddNew input[type='file']");
    private static final By BTN_THEM_CAU_HOI =
        By.xpath("//button[.//span[contains(normalize-space(),'Thêm câu hỏi')]]");
    private static final By BTN_SAVE =
        By.xpath("//button[.//i[contains(@class,'mdi-content-save')]]");

    // Dropdown menu button (arrow down) for question types
    private static final String DROPDOWN_ARROW =
        "//button[contains(@class,'primary--text')]//i[contains(@class,'mdi-menu-down')]";

    // Dropdown menu items
    private static final String MENU_ITEM_AM_THANH = "//div[@role='menuitem']//div[contains(text(),'Âm thanh')]";
    private static final String MENU_ITEM_HINH_ANH = "//div[@role='menuitem']//div[contains(text(),'Hình ảnh')]";
    private static final String MENU_ITEM_VIDEO = "//div[@role='menuitem']//div[contains(text(),'Video')]";
    private static final String MENU_ITEM_TU_LUAN = "//div[@role='menuitem']//div[contains(text(),'Tự luận')]";

    public HomeworkManagementPage(WebDriver driver, WebDriverWait wait, SeleniumHelper helper) {
        this.driver = driver;
        this.wait = wait;
        this.helper = helper;
    }

    public void navigateToHomeworkManagement() {
        System.out.println("Navigating to homework management...");
        driver.get("https://elearning.plt.pro.vn/quan-tri-vien/bai-tap");
        wait.until(ExpectedConditions.urlContains("/quan-tri-vien/bai-tap"));
        helper.delay(1500);
    }

    public void clickAddNew() {
        System.out.println("Clicking Add New...");
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(BTN_THEM_MOI));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        helper.delay(2000);
    }

    public void inputHomeworkName(String name) {
        System.out.println("Entering homework name: " + name);
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(INPUT_TEN_BAI_TAP));
        input.clear();
        input.sendKeys(name);
        helper.delay(500);
    }

    public void uploadHomeworkThumbnail() {
        System.out.println("Uploading homework thumbnail: " + IMAGE_FILE);
        // Click the file area to reveal input, then sendKeys with absolute path
        WebElement fileInput = driver.findElement(FORM_FILE_INPUT);
        fileInput.sendKeys(IMAGE_FILE);
        helper.delay(1000);
    }

    // ==================== Question type: DEFAULT (multiple choice) ====================

    public void addDefaultQuestion(QuestionData question, int questionIndex) {
        System.out.println("\n--- Adding default question " + (questionIndex + 1) + " ---");

        // Click "Thêm câu hỏi"
        WebElement btnAdd = wait.until(ExpectedConditions.elementToBeClickable(BTN_THEM_CAU_HOI));
        helper.safeClick(btnAdd);
        helper.delay(1500);

        // Expand the newly added question panel
        expandLastQuestionPanel();

        // Fill question content + answers
        fillQuestionContent(question.content);
        fillAnswers(question.answers);
        tickCorrectAnswers(question.correctAnswers);

        // Collapse
        collapseLastExpandedPanel();
    }

    // ==================== Question type: AUDIO ====================

    public void addAudioQuestion(QuestionData question, int questionIndex) {
        System.out.println("\n--- Adding audio question " + (questionIndex + 1) + " ---");

        clickDropdownAndSelectType(MENU_ITEM_AM_THANH, "Âm thanh");
        helper.delay(1500);

        expandLastQuestionPanel();

        // Upload audio file
        uploadMediaFile("audio", AUDIO_FILE);

        fillQuestionContent(question.content);
        fillAnswers(question.answers);
        tickCorrectAnswers(question.correctAnswers);

        collapseLastExpandedPanel();
    }

    // ==================== Question type: IMAGE ====================

    public void addImageQuestion(QuestionData question, int questionIndex) {
        System.out.println("\n--- Adding image question " + (questionIndex + 1) + " ---");

        clickDropdownAndSelectType(MENU_ITEM_HINH_ANH, "Hình ảnh");
        helper.delay(1500);

        expandLastQuestionPanel();

        // Upload image file
        uploadMediaFile("image", IMAGE_FILE);

        fillQuestionContent(question.content);
        fillAnswers(question.answers);
        tickCorrectAnswers(question.correctAnswers);

        collapseLastExpandedPanel();
    }

    // ==================== Question type: VIDEO ====================

    public void addVideoQuestion(QuestionData question, int questionIndex) {
        System.out.println("\n--- Adding video question " + (questionIndex + 1) + " ---");

        clickDropdownAndSelectType(MENU_ITEM_VIDEO, "Video");
        helper.delay(1500);

        expandLastQuestionPanel();

        // Fill YouTube video ID
        fillVideoId(question.videoId);

        fillQuestionContent(question.content);
        fillAnswers(question.answers);
        tickCorrectAnswers(question.correctAnswers);

        collapseLastExpandedPanel();
    }

    // ==================== Question type: ESSAY ====================

    public void addEssayQuestion(QuestionData question, int questionIndex) {
        System.out.println("\n--- Adding essay question " + (questionIndex + 1) + " ---");

        clickDropdownAndSelectType(MENU_ITEM_TU_LUAN, "Tự luận");
        helper.delay(1500);

        expandLastQuestionPanel();

        fillQuestionContent(question.content);
        fillCharLimit(question.charLimit);

        collapseLastExpandedPanel();
    }

    // ==================== Reload & Verify ====================

    public void clickReload() {
        System.out.println("Clicking Tải lại dữ liệu...");
        WebElement reloadBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[.//i[contains(@class,'mdi-refresh')] and contains(.,'Tải lại dữ liệu')]")));
        helper.safeClick(reloadBtn);
        helper.delay(2000);
        System.out.println("Data reloaded");
    }

    public void verifyHomeworkInList(String expectedName) {
        System.out.println("\n--- Verify Homework In List ---");

        // Find the row containing the homework name
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        System.out.println("Found " + rows.size() + " row(s) in homework list");

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 5) {
                String tenDe = cells.get(0).getText().trim();
                String soCauHoi = cells.get(1).getText().trim();
                String nguoiTao = cells.get(2).getText().trim();
                String ngayTao = cells.get(3).getText().trim();
                String ngayCapNhat = cells.get(4).getText().trim();

                if (tenDe.contains(expectedName)) {
                    System.out.println("[PASS] Homework found in list:");
                    System.out.println("  Tên đề      : " + tenDe);
                    System.out.println("  Số câu hỏi  : " + soCauHoi);
                    System.out.println("  Người tạo   : " + nguoiTao);
                    System.out.println("  Ngày tạo    : " + ngayTao);
                    System.out.println("  Ngày cập nhật: " + ngayCapNhat);
                    return;
                }
            }
        }
        Assert.fail("Homework not found in list: " + expectedName);
    }

    // ==================== Edit (Update) Homework ====================

    public void clickEditHomework(String homeworkName) {
        System.out.println("Clicking edit for homework: " + homeworkName);

        // Find the row with the homework name, then click the pencil icon
        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//table//tr[.//td[contains(.,'" + homeworkName + "')]]")));
        WebElement pencilBtn = row.findElement(
            By.xpath(".//a[.//i[contains(@class,'mdi-pencil')]]"));
        helper.safeClick(pencilBtn);
        helper.delay(2000);

        // Wait for edit page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[contains(text(),'Chỉnh sửa bài tập')]")));
        System.out.println("Edit page loaded - 'Chỉnh sửa bài tập' visible");
    }

    public void updateHomeworkName(String newName) {
        System.out.println("Updating homework name to: " + newName);
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(INPUT_TEN_BAI_TAP));
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        helper.delay(300);
        input.sendKeys(newName);
        helper.delay(500);
    }

    public void updateHomeworkThumbnail() {
        System.out.println("Updating homework thumbnail...");
        String newImage = System.getProperty("user.dir") + "/src/image/golang.jpg";
        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("input[type='file']")));
        fileInput.sendKeys(newImage);
        helper.delay(1000);
        System.out.println("Thumbnail updated: " + newImage);
    }

    public void expandQuestionPanel(int questionIndex) {
        System.out.println("Expanding question " + (questionIndex + 1) + "...");
        List<WebElement> panels = driver.findElements(
            By.xpath("//button[contains(@class, 'v-expansion-panel-header')]"));

        if (questionIndex < panels.size()) {
            WebElement panel = panels.get(questionIndex);
            String expanded = panel.getAttribute("aria-expanded");
            if (!"true".equals(expanded)) {
                helper.safeClick(panel);
                helper.delay(1000);
            }
        }
    }

    public void collapseQuestionPanel(int questionIndex) {
        List<WebElement> panels = driver.findElements(
            By.xpath("//button[contains(@class, 'v-expansion-panel-header')]"));

        if (questionIndex < panels.size()) {
            WebElement panel = panels.get(questionIndex);
            String expanded = panel.getAttribute("aria-expanded");
            if ("true".equals(expanded)) {
                helper.safeClick(panel);
                helper.delay(500);
            }
        }
    }

    public void updateQuestionContent(String newContent) {
        System.out.println("Updating question content to: " + newContent);

        List<WebElement> textareas = driver.findElements(
            By.xpath("//div[contains(@class, 'v-expansion-panel--active')]//label[contains(text(),'Nội dung câu hỏi')]/ancestor::div[contains(@class,'v-input')]//textarea"));

        if (!textareas.isEmpty()) {
            WebElement textarea = textareas.get(textareas.size() - 1);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", textarea);
            helper.delay(200);
            textarea.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
            helper.delay(200);
            textarea.sendKeys(newContent);
        } else {
            List<WebElement> inputs = driver.findElements(
                By.xpath("//div[contains(@class, 'v-expansion-panel--active')]//label[contains(text(),'Nội dung câu hỏi')]/ancestor::div[contains(@class,'v-input')]//input"));
            if (!inputs.isEmpty()) {
                WebElement input = inputs.get(inputs.size() - 1);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", input);
                helper.delay(200);
                input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
                helper.delay(200);
                input.sendKeys(newContent);
            }
        }
        helper.delay(300);
    }

    public void updateAnswers(List<String> newAnswers) {
        if (newAnswers == null || newAnswers.isEmpty()) return;

        System.out.println("Updating " + newAnswers.size() + " answers...");

        List<WebElement> answerInputs = driver.findElements(
            By.xpath("//div[contains(@class, 'v-expansion-panel--active')]//label[contains(text(),'Câu trả lời')]/ancestor::div[contains(@class,'v-input')]//input"));

        int startIndex = Math.max(0, answerInputs.size() - newAnswers.size());

        for (int i = 0; i < newAnswers.size() && (startIndex + i) < answerInputs.size(); i++) {
            WebElement input = answerInputs.get(startIndex + i);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", input);
            helper.delay(200);
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
            helper.delay(200);
            input.sendKeys(newAnswers.get(i));
            System.out.println("  Answer " + (i + 1) + ": " + newAnswers.get(i));
            helper.delay(200);
        }
    }

    // ==================== Save ====================

    public void clickSave() {
        System.out.println("\nSaving homework...");
        WebElement btnSave = wait.until(ExpectedConditions.elementToBeClickable(BTN_SAVE));
        helper.safeClick(btnSave);
        helper.delay(2000);

        // Handle success dialog if any
        helper.clickOK();
        helper.delay(1000);
        System.out.println("Homework saved");
    }

    // ==================== Dispatch question by type ====================

    public void addQuestion(QuestionData question, int index) {
        switch (question.type.toLowerCase()) {
            case "default":
                addDefaultQuestion(question, index);
                break;
            case "audio":
                addAudioQuestion(question, index);
                break;
            case "image":
                addImageQuestion(question, index);
                break;
            case "video":
                addVideoQuestion(question, index);
                break;
            case "essay":
                addEssayQuestion(question, index);
                break;
            default:
                throw new RuntimeException("Unknown question type: " + question.type);
        }
    }

    // ==================== Private helpers ====================

    private void clickDropdownAndSelectType(String menuItemXpath, String typeName) {
        System.out.println("Selecting question type: " + typeName);

        // Find and click the dropdown arrow button
        List<WebElement> arrows = driver.findElements(By.xpath(DROPDOWN_ARROW));
        if (arrows.isEmpty()) {
            throw new RuntimeException("Dropdown arrow button not found");
        }
        // Click the last dropdown arrow (in case there are multiple)
        WebElement arrow = arrows.get(arrows.size() - 1);
        helper.safeClick(arrow);
        helper.delay(800);

        // Click the menu item
        WebElement menuItem = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(menuItemXpath)));
        helper.safeClick(menuItem);
        helper.delay(1000);
    }

    private void expandLastQuestionPanel() {
        System.out.println("Expanding question panel...");
        List<WebElement> panels = driver.findElements(
            By.xpath("//button[contains(@class, 'v-expansion-panel-header')]")
        );

        if (!panels.isEmpty()) {
            WebElement lastPanel = panels.get(panels.size() - 1);
            String expanded = lastPanel.getAttribute("aria-expanded");
            if (!"true".equals(expanded)) {
                helper.safeClick(lastPanel);
                helper.delay(1000);
            }
        }
    }

    private void collapseLastExpandedPanel() {
        System.out.println("Collapsing panel...");
        List<WebElement> expandedPanels = driver.findElements(
            By.xpath("//button[@aria-expanded='true' and contains(@class, 'v-expansion-panel-header')]")
        );

        if (!expandedPanels.isEmpty()) {
            WebElement last = expandedPanels.get(expandedPanels.size() - 1);
            helper.safeClick(last);
            helper.delay(500);
        }
    }

    private void fillQuestionContent(String content) {
        System.out.println("Filling question content: " + content);

        // Find the last expanded panel content area
        List<WebElement> expandedContents = driver.findElements(
            By.xpath("//div[contains(@class, 'v-expansion-panel-content')]//div[contains(@class, 'v-expansion-panel--active')]//textarea | " +
                     "//div[contains(@class, 'v-expansion-panel--active')]//label[contains(text(),'Nội dung câu hỏi')]/ancestor::div[contains(@class,'v-input')]//textarea")
        );

        if (!expandedContents.isEmpty()) {
            WebElement textarea = expandedContents.get(expandedContents.size() - 1);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", textarea);
            helper.delay(200);
            textarea.clear();
            textarea.sendKeys(content);
        } else {
            // Try input field instead of textarea
            List<WebElement> inputs = driver.findElements(
                By.xpath("//div[contains(@class, 'v-expansion-panel--active')]//label[contains(text(),'Nội dung câu hỏi')]/ancestor::div[contains(@class,'v-input')]//input")
            );
            if (!inputs.isEmpty()) {
                WebElement input = inputs.get(inputs.size() - 1);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", input);
                helper.delay(200);
                input.clear();
                input.sendKeys(content);
            }
        }
        helper.delay(300);
    }

    private void fillAnswers(List<String> answers) {
        if (answers == null || answers.isEmpty()) return;

        System.out.println("Filling " + answers.size() + " answers...");

        // Find answer input fields in the active/expanded panel
        List<WebElement> answerInputs = driver.findElements(
            By.xpath("//div[contains(@class, 'v-expansion-panel--active')]//label[contains(text(),'Câu trả lời')]/ancestor::div[contains(@class,'v-input')]//input")
        );

        // Get the last N inputs matching our answer count
        int startIndex = Math.max(0, answerInputs.size() - answers.size());

        for (int i = 0; i < answers.size() && (startIndex + i) < answerInputs.size(); i++) {
            WebElement input = answerInputs.get(startIndex + i);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", input);
            helper.delay(200);
            input.clear();
            input.sendKeys(answers.get(i));
            System.out.println("  Answer " + (i + 1) + ": " + answers.get(i));
            helper.delay(200);
        }
    }

    private void tickCorrectAnswers(List<Integer> correctIndices) {
        if (correctIndices == null || correctIndices.isEmpty()) return;

        System.out.println("Ticking correct answers: " + correctIndices);

        // Find checkboxes in the active panel
        List<WebElement> checkboxes = driver.findElements(
            By.xpath("//div[contains(@class, 'v-expansion-panel--active')]//div[contains(@class,'v-input--checkbox')]//div[contains(@class,'v-input--selection-controls__ripple')]")
        );

        int startIndex = Math.max(0, checkboxes.size() - 4);

        for (int idx : correctIndices) {
            int actualIdx = startIndex + idx;
            if (actualIdx < checkboxes.size()) {
                WebElement checkbox = checkboxes.get(actualIdx);
                helper.safeClick(checkbox);
                System.out.println("  Ticked answer " + (idx + 1));
                helper.delay(300);
            }
        }
    }

    private void uploadMediaFile(String mediaType, String filePath) {
        System.out.println("Uploading " + mediaType + " file: " + filePath);

        String acceptAttr;
        switch (mediaType) {
            case "audio":
                acceptAttr = ".mp3,audio/*";
                break;
            case "image":
                acceptAttr = "image/png, image/jpeg, image/bmp";
                break;
            default:
                acceptAttr = "";
        }

        // Find file input in the active panel
        List<WebElement> fileInputs = driver.findElements(
            By.xpath("//div[contains(@class, 'v-expansion-panel--active')]//input[@type='file']")
        );

        if (!fileInputs.isEmpty()) {
            WebElement fileInput = fileInputs.get(fileInputs.size() - 1);
            fileInput.sendKeys(filePath);
            helper.delay(1500);
            System.out.println(mediaType + " file uploaded");
        } else {
            System.out.println("WARNING: No file input found for " + mediaType);
        }
    }

    private void fillVideoId(String videoId) {
        if (videoId == null) return;

        System.out.println("Filling YouTube video ID: " + videoId);

        List<WebElement> inputs = driver.findElements(
            By.xpath("//div[contains(@class, 'v-expansion-panel--active')]//label[contains(text(),'ID video')]/ancestor::div[contains(@class,'v-input')]//input")
        );

        if (!inputs.isEmpty()) {
            WebElement input = inputs.get(inputs.size() - 1);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", input);
            helper.delay(200);
            input.clear();
            input.sendKeys(videoId);
            helper.delay(300);
        }
    }

    private void fillCharLimit(int charLimit) {
        if (charLimit <= 0) return;

        System.out.println("Setting character limit: " + charLimit);

        List<WebElement> inputs = driver.findElements(
            By.xpath("//div[contains(@class, 'v-expansion-panel--active')]//label[contains(text(),'số lượng ký tự') or contains(text(),'Số lượng ký tự')]/ancestor::div[contains(@class,'v-input')]//input")
        );

        if (!inputs.isEmpty()) {
            WebElement input = inputs.get(inputs.size() - 1);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", input);
            helper.delay(200);
            input.clear();
            input.sendKeys(String.valueOf(charLimit));
            helper.delay(300);
        }
    }
}
