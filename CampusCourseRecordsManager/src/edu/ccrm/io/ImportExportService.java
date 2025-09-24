package edu.ccrm.io;

import edu.ccrm.domain.*;
import edu.ccrm.exceptions.DataExportException;
import edu.ccrm.service.StudentService;
import edu.ccrm.service.CourseService;
import edu.ccrm.service.EnrollmentService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ImportExportService {

    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public ImportExportService() {
        this.studentService = StudentService.getInstance();
        this.courseService = CourseService.getInstance();
        this.enrollmentService = EnrollmentService.getInstance();
    }

    // Export students to CSV file using NIO.2
    public void exportStudentsToCSV(String filename) throws DataExportException {
        try {
            Path filePath = Path.of(filename);
            List<String> lines = new ArrayList<>();

            // CSV header
            lines.add("ID,RegistrationNo,FullName,Email,Status,CreatedDate");

            // Data rows
            for (Student student : studentService.getAllStudents()) {
                String line = String.format("%s,%s,%s,%s,%s,%s",
                        student.getId(),
                        student.getRegNo(),
                        escapeCsvField(student.getFullName()),
                        student.getEmail(),
                        student.isActive() ? "ACTIVE" : "INACTIVE",
                        student.getCreatedDate()
                );
                lines.add(line);
            }

            // Write to file using NIO.2
            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("✓ Students exported to: " + filePath.toAbsolutePath());

        } catch (IOException e) {
            throw new DataExportException("Failed to export students to CSV: " + e.getMessage(), e);
        }
    }

    // Export courses to CSV
    public void exportCoursesToCSV(String filename) throws DataExportException {
        try {
            Path filePath = Path.of(filename);
            List<String> lines = new ArrayList<>();

            lines.add("Code,Title,Credits,InstructorID,Department,Semester,Status");

            for (Course course : courseService.getAllCourses()) {
                String instructorId = course.getInstructor() != null ? course.getInstructor().getId() : "";
                String line = String.format("%s,%s,%d,%s,%s,%s,%s",
                        course.getCode(),
                        escapeCsvField(course.getTitle()),
                        course.getCredits(),
                        instructorId,
                        escapeCsvField(course.getDepartment()),
                        course.getSemester() != null ? course.getSemester().name() : "",
                        course.isActive() ? "ACTIVE" : "INACTIVE"
                );
                lines.add(line);
            }

            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("✓ Courses exported to: " + filePath.toAbsolutePath());

        } catch (IOException e) {
            throw new DataExportException("Failed to export courses to CSV: " + e.getMessage(), e);
        }
    }

    // Import students from CSV
    public void importStudentsFromCSV(String filename) throws DataExportException {
        try {
            Path filePath = Path.of(filename);

            if (!Files.exists(filePath)) {
                throw new DataExportException("File not found: " + filename);
            }

            List<String> lines = Files.readAllLines(filePath);

            // Skip header line
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;

                String[] fields = parseCsvLine(line);
                if (fields.length >= 5) {
                    Student student = new Student(
                            fields[0], // id
                            fields[1], // regNo
                            fields[2], // fullName
                            fields[3]  // email
                    );

                    if ("INACTIVE".equalsIgnoreCase(fields[4])) {
                        student.setActive(false);
                    }

                    studentService.addStudent(student);
                }
            }

            System.out.println("✓ Students imported from: " + filePath.toAbsolutePath());

        } catch (IOException e) {
            throw new DataExportException("Failed to import students from CSV: " + e.getMessage(), e);
        }
    }

    // Import courses from CSV
    public void importCoursesFromCSV(String filename) throws DataExportException {
        try {
            Path filePath = Path.of(filename);

            if (!Files.exists(filePath)) {
                throw new DataExportException("File not found: " + filename);
            }

            List<String> lines = Files.readAllLines(filePath);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;

                String[] fields = parseCsvLine(line);
                if (fields.length >= 6) {
                    Course.Builder builder = new Course.Builder(fields[0], fields[1])
                            .credits(Integer.parseInt(fields[2]))
                            .department(fields[4]);

                    // Set semester if available
                    if (!fields[5].isEmpty()) {
                        try {
                            builder.semester(Semester.valueOf(fields[5].toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            System.out.println("Warning: Invalid semester: " + fields[5]);
                        }
                    }

                    Course course = builder.build();

                    if (fields.length > 6 && "INACTIVE".equalsIgnoreCase(fields[6])) {
                        course.setActive(false);
                    }

                    courseService.addCourse(course);
                }
            }

            System.out.println("✓ Courses imported from: " + filePath.toAbsolutePath());

        } catch (IOException e) {
            throw new DataExportException("Failed to import courses from CSV: " + e.getMessage(), e);
        }
    }

    // Helper method to escape CSV fields
    private String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    // Helper method to parse CSV line
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }

        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }
}
