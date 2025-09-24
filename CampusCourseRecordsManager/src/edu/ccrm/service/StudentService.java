package edu.ccrm.service;


import edu.ccrm.domain.Student;
import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Course;
import edu.ccrm.exceptions.DuplicateEnrollmentException;
import edu.ccrm.exceptions.MaxCreditLimitExceededException;


import java.util.function.Predicate;

import java.util.*;
import java.util.stream.Collectors;

public class StudentService implements Searchable<Student> {
    private final Map<String, Student> students;
    private static final int MAX_CREDITS_PER_SEMESTER = 18;

    // Singleton pattern for StudentService
    private static StudentService instance;

    private StudentService() {
        this.students = new HashMap<>();
    }

    public static synchronized StudentService getInstance() {
        if (instance == null) {
            instance = new StudentService();
        }
        return instance;
    }

    // Student CRUD operations
    public void addStudent(Student student) {
        if (student == null) throw new IllegalArgumentException("Student cannot be null");
        students.put(student.getId(), student);
    }

    public Student getStudent(String id) {
        return students.get(id);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    public boolean updateStudent(String id, String fullName, String email) {
        Student student = students.get(id);
        if (student != null) {
            student.setFullName(fullName);
            student.setEmail(email);
            return true;
        }
        return false;
    }

    public boolean deactivateStudent(String id) {
        Student student = students.get(id);
        if (student != null) {
            student.setActive(false);
            return true;
        }
        return false;
    }

    // Enrollment operations with exception handling
    public void enrollStudent(Student student, Course course)
            throws DuplicateEnrollmentException, MaxCreditLimitExceededException {

        // Check for duplicate enrollment
        boolean alreadyEnrolled = student.getEnrollments().stream()
                .anyMatch(e -> e.getCourse().equals(course));

        if (alreadyEnrolled) {
            throw new DuplicateEnrollmentException(
                    "Student " + student.getFullName() + " is already enrolled in " + course.getTitle());
        }

        // Check credit limit
        int currentCredits = student.getEnrollments().stream()
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
        int attemptedCredits = currentCredits + course.getCredits();

        if (attemptedCredits > MAX_CREDITS_PER_SEMESTER) {
            throw new MaxCreditLimitExceededException(
                    "Credit limit exceeded for student " + student.getFullName(),
                    currentCredits, MAX_CREDITS_PER_SEMESTER, attemptedCredits);
        }

        // Create and add enrollment
        Enrollment enrollment = new Enrollment(student, course);
        student.addEnrollment(enrollment);
    }

    public void unenrollStudent(Student student, Course course) {
        Enrollment enrollmentToRemove = student.getEnrollments().stream()
                .filter(e -> e.getCourse().equals(course))
                .findFirst()
                .orElse(null);

        if (enrollmentToRemove != null) {
            student.removeEnrollment(enrollmentToRemove);
        }
    }

    // Searchable interface implementation using Streams
    @Override
    public List<Student> search(Predicate<Student> condition) {
        return students.values().stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    @Override
    public Student findById(String id) {
        return students.get(id);
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }

    // Additional search methods using Streams API
    public List<Student> searchByName(String name) {
        return students.values().stream()
                .filter(s -> s.getFullName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Student> getActiveStudents() {
        return students.values().stream()
                .filter(Student::isActive)
                .collect(Collectors.toList());
    }

    public List<Student> getStudentsWithGpaAbove(double minGpa) {
        return students.values().stream()
                .filter(s -> s.calculateGPA() >= minGpa)
                .sorted((s1, s2) -> Double.compare(s2.calculateGPA(), s1.calculateGPA()))
                .collect(Collectors.toList());
    }
}

