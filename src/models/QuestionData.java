package models;

import java.util.List;

public class QuestionData {
    /** "default", "audio", "image", "video", "essay" */
    public String type;
    public String content;
    public List<String> answers;
    public List<Integer> correctAnswers;
    /** Only for essay type */
    public int charLimit;
    /** Only for video type — YouTube video ID */
    public String videoId;

    public QuestionData() {}
}
