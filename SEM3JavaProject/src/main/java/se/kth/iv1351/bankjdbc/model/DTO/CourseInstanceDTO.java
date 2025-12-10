package se.kth.iv1351.bankjdbc.model.DTO;

public class CourseInstanceDTO {
    private final int id;
    private final int num_students;
    private final String study_year;
    private final double hp;
    private final String course_code;

    public CourseInstanceDTO(int id, int num_students, String study_year, double hp,
        String course_code){
        
        this.id = id;
        this.num_students = num_students;
        this.study_year = study_year;
        this.hp = hp;
        this.course_code = course_code;
    }

    public int getId(){return this.id;}
    public int getNumstudents(){return this.num_students;}
    public String getStudyYear(){return this.study_year;}
    public double getHp(){return this.hp;}
    public String getCourseCode(){return this.course_code;}
}
