package models;

import java.util.List;

public class ChapterData {
    public String title;
    public String description;
    public List<LessonData> lessons;

    public ChapterData(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
