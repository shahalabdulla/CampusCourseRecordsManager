package edu.ccrm.service;


import edu.ccrm.domain.Course;
import edu.ccrm.domain.Semester;
import edu.ccrm.domain.Instructor;


import java.util.function.Predicate;

import java.util.*;
import java.util.stream.Collectors;

public class CourseService implements Searchable<Course> {
    private final Map<String, Course> courses;

    // Singleton pattern
    private static CourseService instance;

    private CourseService() {
        this.courses = new HashMap<>();
    }

    public static synchronized CourseService getInstance() {
        if (instance == null) {
            instance = new CourseService();
        }
        return instance;
    }

    // Course CRUD operations
    public void addCourse(Course course) {
        if (course == null) throw new IllegalArgumentException("Course cannot be null");
        courses.put(course.getCode(), course);
    }

    public Course getCourse(String code) {
        return courses.get(code);
    }

    public List<Course> getAllCourses() {
        return new ArrayList<>(courses.values());
    }

    public boolean updateCourse(String code, String title, int credits, String department) {
        Course course = courses.get(code);
        if (course != null) {
            course.setTitle(title);
            course.setCredits(credits);
            course.setDepartment(department);
            return true;
        }
        return false;
    }

    public boolean deactivateCourse(String code) {
        Course course = courses.get(code);
        if (course != null) {
            course.setActive(false);
            return true;
        }
        return false;
    }

    // Search methods using Streams API
    public List<Course> searchByInstructor(Instructor instructor) {
        return courses.values().stream()
                .filter(c -> instructor.equals(c.getInstructor()))
                .collect(Collectors.toList());
    }

    public List<Course> searchByDepartment(String department) {
        return courses.values().stream()
                .filter(c -> c.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    public List<Course> searchBySemester(Semester semester) {
        return courses.values().stream()
                .filter(c -> semester.equals(c.getSemester()))
                .collect(Collectors.toList());
    }

    public List<Course> getActiveCourses() {
        return courses.values().stream()
                .filter(Course::isActive)
                .collect(Collectors.toList());
    }

    // Searchable interface implementation
    @Override
    public List<Course> search(Predicate<Course> condition) {
        return courses.values().stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    @Override
    public Course findById(String id) {
        return courses.get(id);
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(courses.values());
    }
}


