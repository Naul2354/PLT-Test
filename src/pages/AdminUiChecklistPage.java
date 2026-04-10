package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminUiChecklistPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private List<String> errors = new ArrayList<>();

    public AdminUiChecklistPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // =========================
    // LEFT MENU
    // =========================
    public void assertLeftMenuOrder() {

        List<String> expected = Arrays.asList(
                "Trang chủ",
                "Quản lý học viên",
                "Quản lý khóa học",
                "Quản lý bài tập"
        );

        List<WebElement> items = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//a[.//div[contains(@class,'v-list-item__title')]]")
        ));

        List<String> actual = new ArrayList<>();

        for (WebElement el : items) {
            actual.add(el.getText().trim());
        }

        for (int i = 0; i < expected.size(); i++) {
            if (i >= actual.size()) {
                softAssert(false, "Missing menu: " + expected.get(i));
                continue;
            }

            if (!normalize(actual.get(i)).equals(normalize(expected.get(i)))) {
                softAssert(false, "Wrong menu: " + expected.get(i));
            } else {
                System.out.println("[PASS][LEFT_MENU] " + expected.get(i));
            }
        }
    }

    // =========================
    // NAVIGATION
    // =========================
    public void goToStudentManagement() {
        driver.get("https://elearning.plt.pro.vn/quan-tri-vien/hoc-vien");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table")));
    }

    public void goToCourseManagement() {
        driver.get("https://elearning.plt.pro.vn/quan-tri-vien/khoa-hoc");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table")));
    }

    public void goToHomeworkManagement() {
        driver.get("https://elearning.plt.pro.vn/quan-tri-vien/bai-tap");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table")));
    }

    // =========================
    // LOGO IMAGE COMPARE
    // =========================
    public void assertSystemLogoMatches(String expectedImagePath) {
        try {
            driver.get("https://elearning.plt.pro.vn/quan-tri-vien/trang-chu");

            WebElement logo = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".v-image__image[style*='logo']")
            ));

            String style = logo.getAttribute("style");

            String imageUrl = style.replaceAll(".*url\\(\"?", "")
                                   .replaceAll("\"?\\).*", "");

            System.out.println("[INFO][LOGO] Source URL: " + imageUrl);

            BufferedImage actual = ImageIO.read(new java.net.URL(imageUrl));
            BufferedImage expected = ImageIO.read(new File(expectedImagePath));

            softAssert(compareImages(actual, expected), "Logo mismatch");

        } catch (Exception e) {
            softAssert(false, "Logo error: " + e.getMessage());
        }
    }

    private boolean compareImages(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) return false;

        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) return false;
            }
        }
        return true;
    }

    // =========================
    // TABLE HEADER
    // =========================
    public void assertTableHeaders(List<String> expectedHeaders) {

        List<WebElement> headers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//table//thead//th")
        ));

        List<String> actual = new ArrayList<>();

        for (WebElement th : headers) {
            String txt = th.getText().trim();
            if (!txt.isEmpty()) actual.add(normalize(txt));
        }

        for (String expected : expectedHeaders) {
            boolean found = actual.stream().anyMatch(h -> h.contains(normalize(expected)));

            if (!found) {
                softAssert(false, "Missing header: " + expected);
            } else {
                System.out.println("[PASS][TABLE_HEADER] " + expected);
            }
        }
    }

    // =========================
    // SORTING
    // =========================
    public void assertSortingByColumnSafe(String columnName, int index) {

        try {
            driver.findElements(By.xpath("//table//thead//th")).get(index).click();
            sleep();

            List<String> asc = getColumnValues(index);
            softAssert(isSortedASC(asc), "ASC FAIL: " + columnName);

            driver.findElements(By.xpath("//table//thead//th")).get(index).click();
            sleep();

            List<String> desc = getColumnValues(index);
            softAssert(isSortedDESC(desc), "DESC FAIL: " + columnName);

        } catch (Exception e) {
            softAssert(false, "ERROR: " + columnName);
        }
    }

    private List<String> getColumnValues(int index) {

        List<String> values = new ArrayList<>();
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));

        for (WebElement row : rows) {
            List<WebElement> tds = row.findElements(By.tagName("td"));

            if (tds.size() > index) {
                String val = tds.get(index).getText().trim();
                if (val.isEmpty()) val = "ZZZ_EMPTY";
                values.add(val);
            }
        }

        return values;
    }

    private boolean isSortedASC(List<String> list) {
        List<String> sorted = new ArrayList<>(list);
        sorted.sort(this::compareSmart);
        return list.equals(sorted);
    }

    private boolean isSortedDESC(List<String> list) {
        List<String> sorted = new ArrayList<>(list);
        sorted.sort((a, b) -> compareSmart(b, a));
        return list.equals(sorted);
    }

    private int compareSmart(String a, String b) {

        if (a.matches("\\d+") && b.matches("\\d+")) {
            return Long.compare(Long.parseLong(a), Long.parseLong(b));
        }

        LocalDate da = tryParseDate(a);
        LocalDate db = tryParseDate(b);

        if (da != null && db != null) return da.compareTo(db);

        return a.compareToIgnoreCase(b);
    }

    private LocalDate tryParseDate(String s) {
        try {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (Exception e) {
            return null;
        }
    }
    public void checkResponsiveStudentPage() {

        driver.manage().window().setSize(new Dimension(375, 667)); // mobile

        goToStudentManagement();

        // 1. overflow
        Long scrollWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.body.scrollWidth");

        Long clientWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.body.clientWidth");

        softAssert(scrollWidth.equals(clientWidth), "❌ Overflow layout");

        // 2. sidebar
        WebElement sidebar = driver.findElement(By.cssSelector(".v-navigation-drawer"));
        int sidebarWidth = sidebar.getSize().getWidth();
        int screenWidth = driver.manage().window().getSize().getWidth();

        softAssert(sidebarWidth < screenWidth * 0.5, "❌ Sidebar lỗi");

        // 3. table header
        List<WebElement> headers = driver.findElements(By.xpath("//table//thead//th"));
        softAssert(headers.size() > 3, "❌ Table bị vỡ");

    }
    private void checkResponsiveCommon() {

        // ===== 1. overflow =====
        Long scrollWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.body.scrollWidth");

        Long clientWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.body.clientWidth");

        softAssert(scrollWidth.equals(clientWidth), "❌ Overflow layout");

        // ===== 2. sidebar =====
        WebElement sidebar = driver.findElement(By.cssSelector(".v-navigation-drawer"));

        int sidebarWidth = sidebar.getSize().getWidth();
        int screenWidth = driver.manage().window().getSize().getWidth();

        softAssert(sidebarWidth < screenWidth * 0.5,
                "❌ Sidebar không responsive");

        // ===== 3. table =====
        List<WebElement> headers = driver.findElements(By.xpath("//table//thead//th"));

        softAssert(headers.size() > 3, "❌ Table bị vỡ layout");

        // ===== 4. overlap (xịn) =====
        try {
            WebElement content = driver.findElement(By.cssSelector(".v-main"));

            Rectangle r1 = sidebar.getRect();
            Rectangle r2 = content.getRect();

            boolean overlap =
                    r1.getX() < r2.getX() + r2.getWidth() &&
                    r1.getX() + r1.getWidth() > r2.getX() &&
                    r1.getY() < r2.getY() + r2.getHeight() &&
                    r1.getY() + r1.getHeight() > r2.getY();

            softAssert(!overlap, "❌ Sidebar đè content");

        } catch (Exception ignored) {}
    }
    public void checkResponsiveAllPages() {

        // các màn hình cần test
        int[][] sizes = {
                {375, 667},   // mobile
                {768, 1024},  // tablet
                {1366, 768}   // laptop
        };

        for (int[] size : sizes) {

            int width = size[0];
            int height = size[1];

            System.out.println("\n==================================================");
            System.out.println("[SECTION][RESPONSIVE] Viewport: " + width + "x" + height);
            System.out.println("==================================================");

            driver.manage().window().setSize(new Dimension(width, height));

            // =========================
            // STUDENT
            // =========================
            System.out.println("[PAGE] Student Management");
            goToStudentManagement();
            checkResponsiveCommon();

            // =========================
            // COURSE
            // =========================
            System.out.println("[PAGE] Course Management");
            goToCourseManagement();
            checkResponsiveCommon();

            // =========================
            // HOMEWORK
            // =========================
            System.out.println("[PAGE] Homework Management");
            goToHomeworkManagement();
            checkResponsiveCommon();
        }
    }

    // =========================
    // 🔥 BUTTON COLOR (FIXED)
    // =========================
    public void assertPrimaryButtonColor(String expectedColor) {

        List<WebElement> buttons = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("button.v-btn.primary")
                )
        );

        for (WebElement btn : buttons) {
            String color = btn.getCssValue("background-color");

            System.out.println("[INFO][BUTTON_COLOR] Actual color: " + color);

            softAssert(color.equals(expectedColor),
                    "Button color mismatch");
        }
    }

    // =========================
    // UTIL
    // =========================
    private void softAssert(boolean condition, String message) {
        if (!condition) {
            System.out.println("[FAIL] " + message);
            errors.add(message);
        } else {
            System.out.println("[PASS] " + message);
        }
    }

    private String normalize(String s) {
        return s.toLowerCase().replace("đ", "d");
    }

    private void sleep() {
        try { Thread.sleep(800); } catch (Exception ignored) {}
    }

    public List<String> getErrors() {
        return errors;
    }
}