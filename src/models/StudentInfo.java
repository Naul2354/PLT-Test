package models;

public class StudentInfo {
    public String fullName;
    public String studentCode;
    public String email;
    public String phone;
    public String dob;
    public String address;
    public String gender;

    public StudentInfo(String fullName, String studentCode, String email, String phone,
                       String dob, String address, String gender) {
        this.fullName = fullName;
        this.studentCode = studentCode;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.address = address;
        this.gender = gender;
    }
}
