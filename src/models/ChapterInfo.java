package models;

import java.util.ArrayList;
import java.util.List;

public class ChapterInfo {
    public String chapterName;
    public List<String> lessons;

    public ChapterInfo(String chapterName) {
        this.chapterName = chapterName;
        this.lessons = new ArrayList<>();
    }
}
