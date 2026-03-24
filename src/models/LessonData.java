package models;

public class LessonData {
    public String title;
    public String description;

    // Material data
    public String materialType;   // "video" or "attachment"
    public String materialName;
    public String materialUrl;    // YouTube URL for video type

    public LessonData(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
