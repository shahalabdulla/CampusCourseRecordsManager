package edu.ccrm.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student extends Person {
    private String regNo;
    private boolean active;
    private List<Enrollment> enrollments;

    public Student(String id, String regNo, String fullName, String email) {
        super(id, fullName, email);
        this.regNo = Objects.requireNonNull(regNo);
        this.active = true;
        this.enrollments = new ArrayList<>();
    }

    @Override
    public String getRole() {
        return "Student";
    }

    // Getters and setters
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<Enrollment> getEnrollments() { return enrollments; }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
    }

    public double calculateGPA() {
        if (enrollments.isEmpty()) return 0.0;

        double totalPoints = 0.0;
        int totalCredits = 0;

        for (Enrollment enrollment : enrollments) {
            if (enrollment.getGrade() != null) {
                totalPoints += enrollment.getGrade().getGradePoints() * enrollment.getCourse().getCredits();
                totalCredits += enrollment.getCourse().getCredits();
            }
        }

        return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }

    @Override
    public String toString() {
        return String.format("Student[%s, RegNo: %s, Active: %s, GPA: %.2f]",
                super.toString(), regNo, active, calculateGPA());
    }
}