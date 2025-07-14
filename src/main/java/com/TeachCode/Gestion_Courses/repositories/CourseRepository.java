package com.TeachCode.Gestion_Courses.repositories;

import com.TeachCode.Gestion_Courses.entities.CategoryEnum;
import com.TeachCode.Gestion_Courses.entities.Course;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query("""
        SELECT c FROM Course c 
        WHERE (:searchQuery IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')))
        AND (:category IS NULL OR c.categoryCourse = :category)
    """)
    List<Course> searchAllCourses(
            @Param("searchQuery") String searchQuery,
            @Param("category") CategoryEnum category
    );

    @Query("""
        SELECT c FROM Course c 
        WHERE (:trainerId IS NULL OR c.trainerId = :trainerId)
        AND (
            LOWER(c.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) 
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchQuery, '%'))
        )
        AND (:category IS NULL OR c.categoryCourse = :category)
    """)
    List<Course> findByTrainerIdAndSearchAll(
            @Param("trainerId") Integer trainerId,
            @Param("searchQuery") String searchQuery,
            @Param("category") CategoryEnum category
    );

    @Query("""
        SELECT c FROM Course c 
        WHERE :studentId MEMBER OF c.studentIds
        AND (
            LOWER(c.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) 
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchQuery, '%'))
        )
        AND (:category IS NULL OR c.categoryCourse = :category)
    """)
    List<Course> findByStudentIdAndSearchAll(
            @Param("studentId") Integer studentId,
            @Param("searchQuery") String searchQuery,
            @Param("category") CategoryEnum category
    );

    List<Course> findByStudentIdsContaining(Integer studentId);

    List<Course> findByTrainerId(Integer trainerId);

    Page<Course> findByTrainerId(Long trainerId, Pageable pageable);

    Page<Course> findByTrainerIdAndCategoryCourse(Long trainerId, @NotNull(message = "Category is required") CategoryEnum categoryCourse, Pageable pageable);

    Page<Course> findByTrainerIdAndTitleContainingIgnoreCase(Integer trainerId, String title, Pageable pageable);

    Page<Course> findByTrainerIdAndTitleContainingIgnoreCaseAndCategoryCourse(
            Integer trainerId, String title, CategoryEnum category, Pageable pageable
    );

    @Query("""
        SELECT DISTINCT c FROM Course c 
        LEFT JOIN FETCH c.resource 
        WHERE c.trainerId = :trainerId
    """)
    Page<Course> findByTrainerIdWithResources(@Param("trainerId") Integer trainerId, Pageable pageable);

    Page<Course> findByStudentIdsContaining(Integer studentId, Pageable pageable);

    // Avec pagination :
    @Query("""
        SELECT c FROM Course c 
        WHERE (:trainerId IS NULL OR c.trainerId = :trainerId)
        AND (
            LOWER(c.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) 
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchQuery, '%'))
        )
        AND (:category IS NULL OR c.categoryCourse = :category)
    """)
    Page<Course> findByTrainerIdAndSearch(
            @Param("trainerId") Integer trainerId,
            @Param("searchQuery") String searchQuery,
            @Param("category") CategoryEnum category,
            Pageable pageable
    );

    @Query("""
        SELECT c FROM Course c 
        WHERE :studentId MEMBER OF c.studentIds
        AND (
            LOWER(c.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) 
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchQuery, '%'))
        )
        AND (:category IS NULL OR c.categoryCourse = :category)
    """)
    Page<Course> findByStudentIdAndSearch(
            @Param("studentId") Integer studentId,
            @Param("searchQuery") String searchQuery,
            @Param("category") CategoryEnum category,
            Pageable pageable
    );

   }
