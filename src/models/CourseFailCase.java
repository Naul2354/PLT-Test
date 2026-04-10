package models;

public class CourseFailCase {
    public String testCase;
    public boolean uploadThumbnail;
    public String title;
    public String description;

    public CourseFailCase(String testCase, boolean uploadThumbnail, String title, String description) {
        this.testCase = testCase;
        this.uploadThumbnail = uploadThumbnail;
        this.title = title;
        this.description = description;
    }
}
