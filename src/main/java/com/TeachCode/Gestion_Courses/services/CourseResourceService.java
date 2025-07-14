package com.TeachCode.Gestion_Courses.services;

import com.TeachCode.Gestion_Courses.entities.Course;
import com.TeachCode.Gestion_Courses.entities.CourseResource;
import com.TeachCode.Gestion_Courses.repositories.CourseResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseResourceService implements ICourseResourceService {

    @Autowired
    private CourseResourceRepository courseResourceRepository;

   /* @Override
    public List<CourseResource> getAllResources() {
        return courseResourceRepository.findAll();
    }*/

    @Override
    public List<CourseResource> getAllResources(){
        return courseResourceRepository.findAll();
    }


    @Override
    public CourseResource addResource(CourseResource resource) {
        return courseResourceRepository.save(resource);
    }

    @Override
    public CourseResource updateResource(CourseResource resource, int resourceId) {
        if (courseResourceRepository.existsById(resourceId)) {
            resource.setId(resourceId);
            return courseResourceRepository.save(resource);
        }
        return null;

    }

    @Override
    public void deleteResource(int resourceId) {
        courseResourceRepository.deleteById(resourceId);
    }

    @Override
    public List<CourseResource> getResourcesByCourseId(int courseId) {
        return courseResourceRepository.findByCourseId(courseId);
    }
}