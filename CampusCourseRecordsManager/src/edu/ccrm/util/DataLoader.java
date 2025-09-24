package edu.ccrm.util;

import edu.ccrm.domain.*;
import edu.ccrm.service.StudentService;
import edu.ccrm.service.CourseService;
import edu.ccrm.service.EnrollmentService;

public class DataLoader {
    public static void loadSampleData() {
        StudentService studentService = StudentService.getInstance();
        CourseService courseService = CourseService.getInstance();
        EnrollmentService enrollmentService = EnrollmentService.getInstance();

        // Create sample instructors
        Instructor instructor1 = new Instructor("I001", "Dr. Smith", "smith@uni.edu", "Computer Science");
        Instructor instructor2 = new Instructor("I002", "Dr. Johnson", "johnson@uni.edu", "Mathematics");

        // Create sample students
        Student student1 = new Student("S001", "2023001", "John Doe", "john.doe@student.edu");
        Student student2 = new Student("S002", "2023002", "Jane Smith", "jane.smith@student.edu");
        Student student3 = new Student("S003", "2023003", "Michael Brown", "michael.b@student.edu");

        // Create sample courses using Builder pattern
        Course course1 = new Course.Builder("CS101", "Introduction to Programming")
                .credits(3)
                .instructor(instructor1)
                .semester(Semester.SPRING)
                .department("Computer Science")
                .build();

        Course course2 = new Course.Builder("MATH101", "Calculus I")
                .credits(4)
                .instructor(instructor2)
                .semester(Semester.SPRING)
                .department("Mathematics")
                .build();

        Course course3 = new Course.Builder("PHYS101", "Physics Fundamentals")
                .credits(3)
                .semester(Semester.FALL)
                .department("Physics")
                .build();

        // Add to services
        studentService.addStudent(student1);
        studentService.addStudent(student2);
        studentService.addStudent(student3);

        courseService.addCourse(course1);
        courseService.addCourse(course2);
        courseService.addCourse(course3);

        // Create some enrollments
        try {
            studentService.enrollStudent(student1, course1);
            studentService.enrollStudent(student1, course2);
            studentService.enrollStudent(student2, course1);
            studentService.enrollStudent(student3, course2);

            // Record some grades
            enrollmentService.recordGrade(student1, course1, 85.5);
            enrollmentService.recordGrade(student1, course2, 92.0);
            enrollmentService.recordGrade(student2, course1, 78.0);

        } catch (Exception e) {
            System.out.println("Warning: Could not create sample enrollments: " + e.getMessage());
        }

        System.out.println("Sample data loaded successfully!");
    }
}