package models;

import java.util.List;

public class CourseData {
    public String title;
    public String description;
    public List<String> learners;
    public List<ChapterData> chapters;

    // Forum data
    public String forumName;
    public String forumDescription;

    // Update data
    public String updatedTitle;
    public String updatedDescription;

    // Video conference data
    public String videoConferenceYoutubeLink;
    public String videoConferenceDescription;
    public String videoConferenceMeetingLink;

    public CourseData(String title, String description, List<String> learners) {
        this.title = title;
        this.description = description;
        this.learners = learners;
    }
}
