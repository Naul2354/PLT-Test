package utils;

import models.ChapterData;
import models.CourseData;
import models.HomeworkData;
import models.LessonData;
import models.QuestionData;
import models.StudentInfo;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataLoader {

    private static List<String> lastNames = new ArrayList<>();
    private static List<String> middleNames = new ArrayList<>();
    private static List<String> firstNames = new ArrayList<>();
    private static List<String> streets = new ArrayList<>();
    private static List<String> districts = new ArrayList<>();

    public static void loadCSV() {
        try {
            System.out.println("Loading CSV data...");

            Reader namesReader = new FileReader("src/resources/vietnamese_names.csv");
            Iterable<CSVRecord> namesRecords = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(namesReader);

            for (CSVRecord record : namesRecords) {
                String type = record.get("type");
                String value = record.get("value");

                if ("lastName".equals(type)) lastNames.add(value);
                else if ("middleName".equals(type)) middleNames.add(value);
                else if ("firstName".equals(type)) firstNames.add(value);
            }
            namesReader.close();

            Reader locationsReader = new FileReader("src/resources/vietnamese_locations.csv");
            Iterable<CSVRecord> locationRecords = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(locationsReader);

            for (CSVRecord record : locationRecords) {
                String type = record.get("type");
                String value = record.get("value");

                if ("street".equals(type)) streets.add(value);
                else if ("district".equals(type)) districts.add(value);
            }
            locationsReader.close();

            System.out.println("CSV data loaded");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load CSV data", e);
        }
    }

    public static StudentInfo generateRandomStudent() {
        if (lastNames.isEmpty()) loadCSV();

        Random rand = new Random();

        String fullName = lastNames.get(rand.nextInt(lastNames.size())) + " " +
                          middleNames.get(rand.nextInt(middleNames.size())) + " " +
                          firstNames.get(rand.nextInt(firstNames.size()));

        String studentCode = "SV" + (System.currentTimeMillis() % 100000);

        String firstName = fullName.substring(fullName.lastIndexOf(" ") + 1).toLowerCase()
                .replaceAll("[áàảãạăắằẳẵặâấầẩẫậ]", "a")
                .replaceAll("[éèẻẽẹêếềểễệ]", "e")
                .replaceAll("[íìỉĩị]", "i")
                .replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o")
                .replaceAll("[úùủũụưứừửữự]", "u")
                .replaceAll("[ýỳỷỹỵ]", "y")
                .replaceAll("đ", "d");
        String[] domains = {"@gmail.com", "@outlook.com", "@yahoo.com"};
        String email = firstName + "." + studentCode.toLowerCase() + domains[rand.nextInt(domains.length)];

        String[] prefixes = {"091", "090", "093", "094", "096", "097", "098", "032", "033"};
        String phone = prefixes[rand.nextInt(prefixes.length)] + String.format("%07d", rand.nextInt(10000000));

        int year = LocalDate.now().getYear() - (18 + rand.nextInt(8));
        int month = 1 + rand.nextInt(12);
        int day = 1 + rand.nextInt(28);
        String dob = String.format("%02d/%02d/%d", month, day, year);

        int houseNum = 1 + rand.nextInt(500);
        String address = houseNum + " " +
                         streets.get(rand.nextInt(streets.size())) + ", " +
                         districts.get(rand.nextInt(districts.size())) + ", TP.HCM";

        String[] genders = {"Nam", "Nữ", "Khác"};
        String gender = genders[rand.nextInt(genders.length)];

        System.out.println("\nGenerated student:");
        System.out.println("  Name: " + fullName);
        System.out.println("  Code: " + studentCode);
        System.out.println("  Email: " + email);
        System.out.println("  Phone: " + phone);

        return new StudentInfo(fullName, studentCode, email, phone, dob, address, gender);
    }

    public static String generateRandomAddress() {
        if (streets.isEmpty()) loadCSV();
        Random rand = new Random();
        return (1 + rand.nextInt(500)) + " " +
                streets.get(rand.nextInt(streets.size())) + ", " +
                districts.get(rand.nextInt(districts.size())) + ", TP.HCM";
    }

    public static List<StudentInfo> loadStudentsFromJSON() throws Exception {
        List<StudentInfo> students = new ArrayList<>();
        String filePath = System.getProperty("user.dir") + "/src/resources/students.json";

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filePath));

        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            StudentInfo student = new StudentInfo(
                (String) jsonObj.get("fullName"),
                (String) jsonObj.get("studentCode"),
                (String) jsonObj.get("email"),
                (String) jsonObj.get("phone"),
                (String) jsonObj.get("dob"),
                (String) jsonObj.get("address"),
                (String) jsonObj.get("gender")
            );
            student.newAddress = (String) jsonObj.get("newAddress");
            students.add(student);
        }

        System.out.println("Loaded " + students.size() + " students from JSON");
        return students;
    }

    public static List<CourseData> loadCoursesFromJSON() throws Exception {
        List<CourseData> courses = new ArrayList<>();
        String filePath = System.getProperty("user.dir") + "/src/resources/courses.json";

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filePath));

        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;

            List<String> learners = new ArrayList<>();
            JSONArray learnersArr = (JSONArray) jsonObj.get("learners");
            if (learnersArr != null) {
                for (Object l : learnersArr) {
                    learners.add((String) l);
                }
            }

            CourseData course = new CourseData(
                (String) jsonObj.get("title"),
                (String) jsonObj.get("description"),
                learners
            );

            // Parse update data
            course.updatedTitle = (String) jsonObj.get("updatedTitle");
            course.updatedDescription = (String) jsonObj.get("updatedDescription");

            // Parse chapters with lessons
            JSONArray chaptersArr = (JSONArray) jsonObj.get("chapters");
            if (chaptersArr != null) {
                course.chapters = new ArrayList<>();
                for (Object chObj : chaptersArr) {
                    JSONObject chJson = (JSONObject) chObj;
                    ChapterData chapter = new ChapterData(
                        (String) chJson.get("title"),
                        (String) chJson.get("description")
                    );

                    JSONArray lessonsArr = (JSONArray) chJson.get("lessons");
                    if (lessonsArr != null) {
                        chapter.lessons = new ArrayList<>();
                        for (Object lObj : lessonsArr) {
                            JSONObject lJson = (JSONObject) lObj;
                            LessonData lesson = new LessonData(
                                (String) lJson.get("title"),
                                (String) lJson.get("description")
                            );
                            lesson.materialType = (String) lJson.get("materialType");
                            lesson.materialName = (String) lJson.get("materialName");
                            lesson.materialUrl = (String) lJson.get("materialUrl");
                            chapter.lessons.add(lesson);
                        }
                    }

                    course.chapters.add(chapter);
                }
            }

            // Parse forum data
            JSONObject forumObj = (JSONObject) jsonObj.get("forum");
            if (forumObj != null) {
                course.forumName = (String) forumObj.get("name");
                course.forumDescription = (String) forumObj.get("description");
            }

            // Parse video conference data
            JSONObject vcObj = (JSONObject) jsonObj.get("videoConference");
            if (vcObj != null) {
                course.videoConferenceYoutubeLink = (String) vcObj.get("youtubeLink");
                course.videoConferenceDescription = (String) vcObj.get("description");
                course.videoConferenceMeetingLink = (String) vcObj.get("meetingLink");
            }

            courses.add(course);
        }

        System.out.println("Loaded " + courses.size() + " courses from JSON");
        return courses;
    }

    public static List<ChapterData> loadChaptersFromJSON() throws Exception {
        List<ChapterData> chapters = new ArrayList<>();
        String filePath = System.getProperty("user.dir") + "/src/resources/chapters.json";

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filePath));

        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            chapters.add(new ChapterData(
                (String) jsonObj.get("title"),
                (String) jsonObj.get("description")
            ));
        }

        System.out.println("Loaded " + chapters.size() + " chapters from JSON");
        return chapters;
    }

    public static List<LessonData> loadLessonsFromJSON() throws Exception {
        List<LessonData> lessons = new ArrayList<>();
        String filePath = System.getProperty("user.dir") + "/src/resources/lessons.json";

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filePath));

        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            lessons.add(new LessonData(
                (String) jsonObj.get("title"),
                (String) jsonObj.get("description")
            ));
        }

        System.out.println("Loaded " + lessons.size() + " lessons from JSON");
        return lessons;
    }

    public static HomeworkData loadHomeworkFromJSON() throws Exception {
        String filePath = System.getProperty("user.dir") + "/src/resources/homework.json";

        JSONParser parser = new JSONParser();
        JSONObject root = (JSONObject) parser.parse(new FileReader(filePath));

        HomeworkData homework = new HomeworkData();
        homework.homeworkName = (String) root.get("homeworkName");
        homework.questions = new java.util.ArrayList<>();

        JSONArray questionsArray = (JSONArray) root.get("questions");
        for (Object obj : questionsArray) {
            JSONObject qObj = (JSONObject) obj;

            QuestionData q = new QuestionData();
            q.type = (String) qObj.get("type");
            q.content = (String) qObj.get("content");

            // Answers (optional for essay)
            JSONArray answersArr = (JSONArray) qObj.get("answers");
            if (answersArr != null) {
                q.answers = new java.util.ArrayList<>();
                for (Object a : answersArr) {
                    q.answers.add((String) a);
                }
            }

            // Correct answers (optional for essay)
            JSONArray correctArr = (JSONArray) qObj.get("correctAnswers");
            if (correctArr != null) {
                q.correctAnswers = new java.util.ArrayList<>();
                for (Object c : correctArr) {
                    q.correctAnswers.add(((Long) c).intValue());
                }
            }

            // Essay char limit
            Object charLimitObj = qObj.get("charLimit");
            if (charLimitObj != null) {
                q.charLimit = ((Long) charLimitObj).intValue();
            }

            // Video ID
            q.videoId = (String) qObj.get("videoId");

            homework.questions.add(q);
        }

        // Parse updated homework data
        homework.updatedHomeworkName = (String) root.get("updatedHomeworkName");

        JSONArray updatedQuestionsArray = (JSONArray) root.get("updatedQuestions");
        if (updatedQuestionsArray != null) {
            homework.updatedQuestions = new java.util.ArrayList<>();
            for (Object obj : updatedQuestionsArray) {
                JSONObject qObj = (JSONObject) obj;

                QuestionData q = new QuestionData();
                q.type = (String) qObj.get("type");
                q.content = (String) qObj.get("content");

                JSONArray answersArr = (JSONArray) qObj.get("answers");
                if (answersArr != null) {
                    q.answers = new java.util.ArrayList<>();
                    for (Object a : answersArr) {
                        q.answers.add((String) a);
                    }
                }

                homework.updatedQuestions.add(q);
            }
        }

        System.out.println("Loaded homework: " + homework.homeworkName +
                           " with " + homework.questions.size() + " questions");
        if (homework.updatedHomeworkName != null) {
            System.out.println("Updated homework: " + homework.updatedHomeworkName +
                               " with " + homework.updatedQuestions.size() + " updated questions");
        }
        return homework;
    }
}
