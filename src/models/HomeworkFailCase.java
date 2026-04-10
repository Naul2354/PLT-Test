package models;

import java.util.List;

public class HomeworkFailCase {
    public String testCase;
    public boolean uploadThumbnail;
    public String homeworkName;
    public boolean addQuestion;
    public String questionContent;
    public List<String> answers;

    public HomeworkFailCase(String testCase, boolean uploadThumbnail, String homeworkName,
                            boolean addQuestion, String questionContent, List<String> answers) {
        this.testCase = testCase;
        this.uploadThumbnail = uploadThumbnail;
        this.homeworkName = homeworkName;
        this.addQuestion = addQuestion;
        this.questionContent = questionContent;
        this.answers = answers;
    }
}
