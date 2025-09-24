package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Enrollment {
    private Student student;
    private Course course;
    private Grade grade;
    private LocalDate enrollmentDate;
    private double marks;

    public Enrollment(Student student, Course course) {
        this.student = Objects.requireNonNull(student);
        this.course = Objects.requireNonNull(course);
        this.enrollmentDate = LocalDate.now();
        this.marks = -1; // Not graded yet
    }

    public void recordMarks(double marks) {
        if (marks < 0 || marks > 100) {
            throw new IllegalArgumentException("Marks must be between 0 and 100");
        }
        this.marks = marks;
        this.grade = Grade.fromScore(marks);
    }

    // Getters
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public Grade getGrade() { return grade; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public double getMarks() { return marks; }

    // ADD THIS METHOD - This was missing!
    public boolean isGraded() {
        return marks >= 0;
    }

    @Override
    public String toString() {
        return String.format("Enrollment[Student: %s, Course: %s, Grade: %s, Marks: %.1f]",
                student.getFullName(), course.getTitle(),
                grade != null ? grade : "Not Graded", marks);
    }
}
