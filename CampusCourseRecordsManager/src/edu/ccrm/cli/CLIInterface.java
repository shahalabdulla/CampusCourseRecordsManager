package edu.ccrm.cli;

import edu.ccrm.domain.*;
import edu.ccrm.service.*;
import edu.ccrm.io.*;
import edu.ccrm.exceptions.*;
import edu.ccrm.util.DataLoader;

import java.util.List;
import java.util.Scanner;

public class CLIInterface {
    private final Scanner scanner;
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final TranscriptService transcriptService;
    private final ImportExportService importExportService;
    private final BackupService backupService;

    public CLIInterface() {
        this.scanner = new Scanner(System.in);
        this.studentService = StudentService.getInstance();
        this.courseService = CourseService.getInstance();
        this.enrollmentService = EnrollmentService.getInstance();
        this.transcriptService = new TranscriptService();
        this.importExportService = new ImportExportService();
        this.backupService = new BackupService();

        // Load sample data
        DataLoader.loadSampleData();
    }

    public void start() {
        System.out.println("=== Campus Course & Records Manager (CCRM) ===");
        System.out.println("Version 1.0 - Developed in Java SE");

        boolean running = true;

        // Main menu loop with labeled break
        mainLoop:
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    manageStudents();
                    break;
                case 2:
                    manageCourses();
                    break;
                case 3:
                    manageEnrollments();
                    break;
                case 4:
                    manageGrades();
                    break;
                case 5:
                    importExportData();
                    break;
                case 6:
                    backupOperations();
                    break;
                case 7:
                    generateReports();
                    break;
                case 8:
                    displayJavaInfo();
                    break;
                case 0:
                    System.out.println("Thank you for using CCRM. Goodbye!");
                    running = false;
                    break mainLoop;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MAIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. Manage Students");
        System.out.println("2. Manage Courses");
        System.out.println("3. Manage Enrollments");
        System.out.println("4. Manage Grades");
        System.out.println("5. Import/Export Data");
        System.out.println("6. Backup Operations");
        System.out.println("7. Generate Reports");
        System.out.println("8. Java Platform Info");
        System.out.println("0. Exit");
        System.out.println("=".repeat(50));
    }

    private void manageStudents() {
        boolean backToMain = false;

        studentMenu:
        while (!backToMain) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("STUDENT MANAGEMENT");
            System.out.println("=".repeat(40));
            System.out.println("1. Add Student");
            System.out.println("2. List All Students");
            System.out.println("3. Search Student by Name");
            System.out.println("4. Update Student");
            System.out.println("5. Deactivate Student");
            System.out.println("6. View Student Profile");
            System.out.println("7. View Student Transcript");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    listAllStudents();
                    break;
                case 3:
                    searchStudentByName();
                    break;
                case 4:
                    updateStudent();
                    break;
                case 5:
                    deactivateStudent();
                    break;
                case 6:
                    viewStudentProfile();
                    break;
                case 7:
                    viewStudentTranscript();
                    break;
                case 0:
                    backToMain = true;
                    break studentMenu;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addStudent() {
        System.out.println("\n--- Add New Student ---");

        String id = getStringInput("Student ID: ");
        String regNo = getStringInput("Registration Number: ");
        String fullName = getStringInput("Full Name: ");
        String email = getStringInput("Email: ");

        Student student = new Student(id, regNo, fullName, email);
        studentService.addStudent(student);

        System.out.println("✓ Student added successfully!");
    }

    private void listAllStudents() {
        System.out.println("\n--- All Students ---");
        List<Student> students = studentService.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        for (Student student : students) {
            System.out.printf("ID: %s, Name: %s, Email: %s, Status: %s, GPA: %.2f%n",
                    student.getId(), student.getFullName(), student.getEmail(),
                    student.isActive() ? "Active" : "Inactive", student.calculateGPA());
        }
        System.out.println("Total students: " + students.size());
    }

    private void searchStudentByName() {
        System.out.println("\n--- Search Student by Name ---");
        String name = getStringInput("Enter name to search: ");

        List<Student> results = studentService.searchByName(name);

        if (results.isEmpty()) {
            System.out.println("No students found matching: " + name);
        } else {
            System.out.println("Found " + results.size() + " student(s):");
            for (Student student : results) {
                System.out.println("- " + student.getFullName() + " (" + student.getId() + ")");
            }
        }
    }

    private void updateStudent() {
        System.out.println("\n--- Update Student ---");
        String id = getStringInput("Enter student ID to update: ");

        Student student = studentService.getStudent(id);
        if (student == null) {
            System.out.println("Student not found with ID: " + id);
            return;
        }

        System.out.println("Current details: " + student);

        String fullName = getStringInput("New full name (or press Enter to keep current): ");
        String email = getStringInput("New email (or press Enter to keep current): ");

        if (!fullName.isEmpty()) student.setFullName(fullName);
        if (!email.isEmpty()) student.setEmail(email);

        System.out.println("✓ Student updated successfully!");
    }

    private void deactivateStudent() {
        System.out.println("\n--- Deactivate Student ---");
        String id = getStringInput("Enter student ID to deactivate: ");

        if (studentService.deactivateStudent(id)) {
            System.out.println("✓ Student deactivated successfully!");
        } else {
            System.out.println("Student not found with ID: " + id);
        }
    }

    private void viewStudentProfile() {
        System.out.println("\n--- Student Profile ---");
        String id = getStringInput("Enter student ID: ");

        Student student = studentService.getStudent(id);
        if (student == null) {
            System.out.println("Student not found with ID: " + id);
            return;
        }

        System.out.println("\n" + student.toString());
        System.out.println("Enrolled courses: " + student.getEnrollments().size());
        System.out.println("Current GPA: " + String.format("%.2f", student.calculateGPA()));
    }

    private void viewStudentTranscript() {
        System.out.println("\n--- Student Transcript ---");
        String id = getStringInput("Enter student ID: ");

        Student student = studentService.getStudent(id);
        if (student == null) {
            System.out.println("Student not found with ID: " + id);
            return;
        }

        transcriptService.printTranscript(student);
    }

    private void manageCourses() {
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("COURSE MANAGEMENT");
            System.out.println("=".repeat(40));
            System.out.println("1. Add Course");
            System.out.println("2. List All Courses");
            System.out.println("3. Search Courses by Department");
            System.out.println("4. Search Courses by Semester");
            System.out.println("5. Update Course");
            System.out.println("6. Deactivate Course");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addCourse();
                    break;
                case 2:
                    listAllCourses();
                    break;
                case 3:
                    searchCoursesByDepartment();
                    break;
                case 4:
                    searchCoursesBySemester();
                    break;
                case 5:
                    updateCourse();
                    break;
                case 6:
                    deactivateCourse();
                    break;
                case 0:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addCourse() {
        System.out.println("\n--- Add New Course ---");

        String code = getStringInput("Course Code: ");
        String title = getStringInput("Course Title: ");
        int credits = getIntInput("Credits: ");
        String department = getStringInput("Department: ");

        System.out.println("Available semesters: SPRING, SUMMER, FALL");
        String semesterInput = getStringInput("Semester: ").toUpperCase();

        Semester semester;
        switch (semesterInput) {
            case "SPRING" -> semester = Semester.SPRING;
            case "SUMMER" -> semester = Semester.SUMMER;
            case "FALL" -> semester = Semester.FALL;
            default -> {
                System.out.println("Invalid semester. Defaulting to SPRING.");
                semester = Semester.SPRING;
            }
        }

        Course course = new Course.Builder(code, title)
                .credits(credits)
                .department(department)
                .semester(semester)
                .build();

        courseService.addCourse(course);
        System.out.println("✓ Course added successfully!");
    }

    private void listAllCourses() {
        System.out.println("\n--- All Courses ---");
        List<Course> courses = courseService.getAllCourses();

        if (courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }

        int count = 0;
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);

            if (!course.isActive()) {
                continue;
            }

            System.out.printf("%d. %s - %s (%d credits, %s)%n",
                    ++count, course.getCode(), course.getTitle(),
                    course.getCredits(), course.getDepartment());

            if (count >= 10) {
                System.out.println("... and " + (courses.size() - i - 1) + " more courses");
                break;
            }
        }
    }

    private void searchCoursesByDepartment() {
        System.out.println("\n--- Search Courses by Department ---");
        String department = getStringInput("Enter department: ");

        List<Course> results = courseService.searchByDepartment(department);

        if (results.isEmpty()) {
            System.out.println("No courses found in department: " + department);
        } else {
            System.out.println("Found " + results.size() + " course(s) in " + department + ":");
            results.forEach(course ->
                    System.out.println("- " + course.getCode() + ": " + course.getTitle()));
        }
    }

    private void searchCoursesBySemester() {
        System.out.println("\n--- Search Courses by Semester ---");
        System.out.println("Available: SPRING, SUMMER, FALL");
        String semesterInput = getStringInput("Enter semester: ").toUpperCase();

        try {
            Semester semester = Semester.valueOf(semesterInput);
            List<Course> results = courseService.searchBySemester(semester);

            if (results.isEmpty()) {
                System.out.println("No courses found for semester: " + semester);
            } else {
                System.out.println("Found " + results.size() + " course(s) for " + semester + ":");
                results.forEach(course ->
                        System.out.println("- " + course.getCode() + ": " + course.getTitle()));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid semester: " + semesterInput);
        }
    }

    private void updateCourse() {
        System.out.println("\n--- Update Course ---");
        String code = getStringInput("Enter course code to update: ");

        Course course = courseService.getCourse(code);
        if (course == null) {
            System.out.println("Course not found with code: " + code);
            return;
        }

        System.out.println("Current details: " + course);

        String title = getStringInput("New title (or press Enter to keep current): ");
        String creditsStr = getStringInput("New credits (or press Enter to keep current): ");
        String department = getStringInput("New department (or press Enter to keep current): ");

        if (!title.isEmpty()) course.setTitle(title);
        if (!creditsStr.isEmpty()) course.setCredits(Integer.parseInt(creditsStr));
        if (!department.isEmpty()) course.setDepartment(department);

        System.out.println("✓ Course updated successfully!");
    }

    private void deactivateCourse() {
        System.out.println("\n--- Deactivate Course ---");
        String code = getStringInput("Enter course code to deactivate: ");

        if (courseService.deactivateCourse(code)) {
            System.out.println("✓ Course deactivated successfully!");
        } else {
            System.out.println("Course not found with code: " + code);
        }
    }

    private void manageEnrollments() {
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("ENROLLMENT MANAGEMENT");
            System.out.println("=".repeat(40));
            System.out.println("1. Enroll Student in Course");
            System.out.println("2. Unenroll Student from Course");
            System.out.println("3. View Student's Enrollments");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    enrollStudent();
                    break;
                case 2:
                    unenrollStudent();
                    break;
                case 3:
                    viewStudentEnrollments();
                    break;
                case 0:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void enrollStudent() {
        System.out.println("\n--- Enroll Student in Course ---");

        String studentId = getStringInput("Student ID: ");
        String courseCode = getStringInput("Course Code: ");

        Student student = studentService.getStudent(studentId);
        Course course = courseService.getCourse(courseCode);

        if (student == null) {
            System.out.println("Student not found with ID: " + studentId);
            return;
        }
        if (course == null) {
            System.out.println("Course not found with code: " + courseCode);
            return;
        }

        try {
            studentService.enrollStudent(student, course);
            System.out.println("✓ Student enrolled successfully!");
        } catch (DuplicateEnrollmentException e) {
            System.out.println("Enrollment error: " + e.getMessage());
        } catch (MaxCreditLimitExceededException e) {
            System.out.printf("Credit limit exceeded! Current: %d, Max: %d, Attempted: %d%n",
                    e.getCurrentCredits(), e.getMaxCredits(), e.getAttemptedCredits());
        }
    }

    private void unenrollStudent() {
        System.out.println("\n--- Unenroll Student from Course ---");

        String studentId = getStringInput("Student ID: ");
        String courseCode = getStringInput("Course Code: ");

        Student student = studentService.getStudent(studentId);
        Course course = courseService.getCourse(courseCode);

        if (student == null || course == null) {
            System.out.println("Student or course not found.");
            return;
        }

        studentService.unenrollStudent(student, course);
        System.out.println("✓ Student unenrolled successfully!");
    }

    private void viewStudentEnrollments() {
        System.out.println("\n--- Student Enrollments ---");
        String studentId = getStringInput("Enter student ID: ");

        Student student = studentService.getStudent(studentId);
        if (student == null) {
            System.out.println("Student not found with ID: " + studentId);
            return;
        }

        System.out.println("Enrollments for " + student.getFullName() + ":");
        if (student.getEnrollments().isEmpty()) {
            System.out.println("No enrollments found.");
        } else {
            student.getEnrollments().forEach(enrollment ->
                    System.out.println("- " + enrollment.getCourse().getTitle() +
                            (enrollment.isGraded() ? " (Graded: " + enrollment.getGrade() + ")" : " (Not Graded)")));
        }
    }

    private void manageGrades() {
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("GRADE MANAGEMENT");
            System.out.println("=".repeat(40));
            System.out.println("1. Record Grade for Student");
            System.out.println("2. View Course Grades Summary");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    recordGrade();
                    break;
                case 2:
                    viewCourseGrades();
                    break;
                case 0:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void recordGrade() {
        System.out.println("\n--- Record Grade ---");

        String studentId = getStringInput("Student ID: ");
        String courseCode = getStringInput("Course Code: ");
        double marks = getDoubleInput("Marks (0-100): ");

        if (enrollmentService.recordGrade(
                studentService.getStudent(studentId),
                courseService.getCourse(courseCode),
                marks
        )) {
            System.out.println("✓ Grade recorded successfully!");
        } else {
            System.out.println("Failed to record grade. Check if student is enrolled.");
        }
    }

    private void viewCourseGrades() {
        System.out.println("\n--- Course Grades Summary ---");
        String courseCode = getStringInput("Enter course code: ");

        Course course = courseService.getCourse(courseCode);
        if (course == null) {
            System.out.println("Course not found with code: " + courseCode);
            return;
        }

        double average = enrollmentService.getCourseAverage(course);
        System.out.printf("Course: %s - %s%n", course.getCode(), course.getTitle());
        System.out.printf("Average Marks: %.2f%n", average);
        System.out.printf("Letter Grade Equivalent: %s%n", Grade.fromScore(average));
    }

    private void importExportData() {
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("IMPORT/EXPORT DATA");
            System.out.println("=".repeat(40));
            System.out.println("1. Export Students to CSV");
            System.out.println("2. Export Courses to CSV");
            System.out.println("3. Import Students from CSV");
            System.out.println("4. Import Courses from CSV");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        importExportService.exportStudentsToCSV("students_export.csv");
                        break;
                    case 2:
                        importExportService.exportCoursesToCSV("courses_export.csv");
                        break;
                    case 3:
                        importExportService.importStudentsFromCSV("test-data/students.csv");
                        break;
                    case 4:
                        importExportService.importCoursesFromCSV("test-data/courses.csv");
                        break;
                    case 0:
                        backToMain = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (DataExportException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void backupOperations() {
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("BACKUP OPERATIONS");
            System.out.println("=".repeat(40));
            System.out.println("1. Create Backup");
            System.out.println("2. Show Backup Size (Recursive)");
            System.out.println("3. List Backup Files (Recursive)");
            System.out.println("4. Restore from Latest Backup");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        backupService.createBackup();
                        break;
                    case 2:
                        long size = backupService.calculateBackupSize();
                        System.out.printf("Total backup size: %,d bytes (%.2f MB)%n",
                                size, size / (1024.0 * 1024.0));
                        break;
                    case 3:
                        backupService.listBackupFiles();
                        break;
                    case 4:
                        backupService.restoreFromLatestBackup();
                        break;
                    case 0:
                        backToMain = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (DataExportException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void generateReports() {
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("REPORTS");
            System.out.println("=".repeat(40));
            System.out.println("1. Top Students by GPA");
            System.out.println("2. GPA Distribution");
            System.out.println("3. Course Enrollment Summary");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    showTopStudents();
                    break;
                case 2:
                    showGpaDistribution();
                    break;
                case 3:
                    showCourseEnrollmentSummary();
                    break;
                case 0:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void showTopStudents() {
        System.out.println("\n--- Top Students by GPA ---");
        double minGpa = getDoubleInput("Minimum GPA (e.g., 8.0): ");

        List<Student> topStudents = studentService.getStudentsWithGpaAbove(minGpa);

        if (topStudents.isEmpty()) {
            System.out.println("No students found with GPA above " + minGpa);
        } else {
            System.out.println("Top students (GPA >= " + minGpa + "):");
            topStudents.forEach(student ->
                    System.out.printf("- %s: %.2f GPA%n", student.getFullName(), student.calculateGPA()));
        }
    }

    private void showGpaDistribution() {
        System.out.println("\n--- GPA Distribution ---");

        studentService.getAllStudents().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        s -> {
                            double gpa = s.calculateGPA();
                            if (gpa >= 9.0) return "A (9.0+)";
                            else if (gpa >= 8.0) return "B (8.0-8.9)";
                            else if (gpa >= 7.0) return "C (7.0-7.9)";
                            else if (gpa > 0) return "D (Below 7.0)";
                            else return "U (Ungraded)";
                        },
                        java.util.stream.Collectors.counting()
                ))
                .forEach((category, count) ->
                        System.out.println(category + ": " + count + " students"));
    }

    private void showCourseEnrollmentSummary() {
        System.out.println("\n--- Course Enrollment Summary ---");

        courseService.getAllCourses().stream()
                .filter(Course::isActive)
                .forEach(course -> {
                    int enrollmentCount = enrollmentService.getEnrollmentsByCourse(course).size();
                    System.out.printf("%s: %d students enrolled%n",
                            course.getTitle(), enrollmentCount);
                });
    }

    private void displayJavaInfo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("JAVA PLATFORM INFORMATION");
        System.out.println("=".repeat(50));
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
        System.out.println("JVM Version: " + System.getProperty("java.vm.version"));
        System.out.println("Operating System: " + System.getProperty("os.name"));

        System.out.println("\n--- Java SE vs ME vs EE ---");
        System.out.println("Java SE (Standard Edition): Desktop and server applications");
        System.out.println("Java ME (Micro Edition): Embedded and mobile devices");
        System.out.println("Java EE (Enterprise Edition): Enterprise-level applications");

        System.out.println("\nThis application uses Java SE (Standard Edition)");
        System.out.println("=".repeat(50));
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}