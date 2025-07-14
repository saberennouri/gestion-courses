package com.TeachCode.Gestion_Courses.services;

import com.TeachCode.Gestion_Courses.entities.Course;
import com.TeachCode.Gestion_Courses.entities.CourseResource;

import java.util.List;

public interface ICourseResourceService { // no usages

    List<Course> getAllResources(); // no usages
    CourseResource addResource(CourseResource resource); // no usages
    CourseResource updateResource(CourseResource resource, int resourceId); // no usages
    void deleteResource(int resourceId); // no usages
    List<CourseResource> getResourcesByCourseId(int courseId); // no usages
}