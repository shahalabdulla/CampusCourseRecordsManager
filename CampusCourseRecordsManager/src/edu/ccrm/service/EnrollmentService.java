package edu.ccrm.service;

import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Student;
import edu.ccrm.domain.Course;
import edu.ccrm.domain.Grade;

import java.util.*;
import java.util.stream.Collectors;

public class EnrollmentService {
    private final List<Enrollment> enrollments;

    // Singleton pattern
    private static EnrollmentService instance;

    private EnrollmentService() {
        this.enrollments = new ArrayList<>();
    }

    public static synchronized EnrollmentService getInstance() {
        if (instance == null) {
            instance = new EnrollmentService();
        }
        return instance;
    }

    public void addEnrollment(Enrollment enrollment) {
        if (enrollment == null) throw new IllegalArgumentException("Enrollment cannot be null");
        enrollments.add(enrollment);
    }

    public List<Enrollment> getEnrollmentsByStudent(Student student) {
        return enrollments.stream()
                .filter(e -> e.getStudent().equals(student))
                .collect(Collectors.toList());
    }

    public List<Enrollment> getEnrollmentsByCourse(Course course) {
        return enrollments.stream()
                .filter(e -> e.getCourse().equals(course))
                .collect(Collectors.toList());
    }

    public boolean recordGrade(Student student, Course course, double marks) {
        Optional<Enrollment> enrollmentOpt = enrollments.stream()
                .filter(e -> e.getStudent().equals(student) && e.getCourse().equals(course))
                .findFirst();

        if (enrollmentOpt.isPresent()) {
            Enrollment enrollment = enrollmentOpt.get();
            enrollment.recordMarks(marks);
            return true;
        }
        return false;
    }

    public Map<Course, Grade> getStudentGrades(Student student) {
        return enrollments.stream()
                .filter(e -> e.getStudent().equals(student) && e.isGraded())
                .collect(Collectors.toMap(
                        Enrollment::getCourse,
                        Enrollment::getGrade
                ));
    }

    public double getCourseAverage(Course course) {
        List<Enrollment> courseEnrollments = getEnrollmentsByCourse(course).stream()
                .filter(Enrollment::isGraded)
                .collect(Collectors.toList());

        if (courseEnrollments.isEmpty()) return 0.0;

        return courseEnrollments.stream()
                .mapToDouble(Enrollment::getMarks)
                .average()
                .orElse(0.0);
    }
}
