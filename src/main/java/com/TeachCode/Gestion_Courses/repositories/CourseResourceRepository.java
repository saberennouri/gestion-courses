package com.TeachCode.Gestion_Courses.repositories;

import com.TeachCode.Gestion_Courses.entities.CourseResource;
import com.TeachCode.Gestion_Courses.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public  interface CourseResourceRepository extends JpaRepository<CourseResource ,Integer> {

     List<CourseResource> findByCourse(Course course);


}
