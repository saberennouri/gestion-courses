package com.TeachCode.Gestion_Courses.services;

import com.TeachCode.Gestion_Courses.entities.CategoryEnum;
import com.TeachCode.Gestion_Courses.entities.Course;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ICourseService {

    List<Course> getAllCourses();

    Course addCourse(Course course, Integer trainerId);

    List<Course> searchAllCourses(String searchQuery, CategoryEnum category);

    List<Course> searchAllCoursesByTrainer(String searchQuery, CategoryEnum category, Integer trainerId);

    List<Course> searchAllCoursesByStudent(String searchQuery, CategoryEnum category, Integer studentId);

    Course updateCourse(Course course, int courseId);

    void deleteCourse(int courseId);

    Course getCourseById(int courseId);

    Course findById(Long courseId);

    Optional<Course> findById(int courseId);

    Course enrollStudentInCourse(int courseId, int studentId);

    void removeStudentFromCourse(int courseId, int studentId);

    List<Course> getCoursesByStudent(int studentId);

    List<Map<String, Object>> getEnrolledStudentWithDetails(int courseId);

    List<Course> getCoursesByTrainer(Integer userId);
}