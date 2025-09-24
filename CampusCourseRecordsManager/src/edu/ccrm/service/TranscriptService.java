package edu.ccrm.service;

import edu.ccrm.domain.Student;
import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Grade;

import java.util.List;
import java.util.stream.Collectors;

public class TranscriptService {

    public String generateTranscript(Student student) {
        StringBuilder transcript = new StringBuilder();

        // Header
        transcript.append("OFFICIAL TRANSCRIPT\n");
        transcript.append("===================\n");
        transcript.append(String.format("Student: %s\n", student.getFullName()));
        transcript.append(String.format("Registration No: %s\n", student.getRegNo()));
        transcript.append(String.format("Overall GPA: %.2f\n\n", student.calculateGPA()));

        // Course details
        transcript.append("COURSE WORK\n");
        transcript.append("===========\n");

        List<Enrollment> gradedEnrollments = student.getEnrollments().stream()
                .filter(Enrollment::isGraded)
                .collect(Collectors.toList());

        if (gradedEnrollments.isEmpty()) {
            transcript.append("No graded courses found.\n");
        } else {
            transcript.append(String.format("%-10s %-30s %-6s %-8s %-10s\n",
                    "Code", "Course Title", "Credits", "Grade", "Marks"));
            transcript.append("-".repeat(70)).append("\n");

            for (Enrollment enrollment : gradedEnrollments) {
                transcript.append(String.format("%-10s %-30s %-6d %-8s %-10.1f\n",
                        enrollment.getCourse().getCode(),
                        enrollment.getCourse().getTitle(),
                        enrollment.getCourse().getCredits(),
                        enrollment.getGrade().name(),
                        enrollment.getMarks()));
            }
        }

        return transcript.toString();
    }

    public void printTranscript(Student student) {
        System.out.println(generateTranscript(student));
    }
}
