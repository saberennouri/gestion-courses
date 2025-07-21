package com.TeachCode.Gestion_Courses.controller;

import com.TeachCode.Gestion_Courses.entities.CourseResource;
import com.TeachCode.Gestion_Courses.services.ICourseResourceService;
import com.TeachCode.Gestion_Courses.services.IFileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Course Resources", description = "This web service handles CRUD operations for course resources.")
@RestController
@AllArgsConstructor
@RequestMapping("/course-resources")
public class CourseResourceRestController {

    @Autowired
    private IFileStorageService fileStorageService; // Changed to use the interface

    @Autowired
    private ICourseResourceService courseResourceService;

    @Operation(
            summary = "Retrieve all resources",
            description = "This endpoint retrieves all resources from the database."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all resources"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/retrieve-all-resources")
    public List<CourseResource> getAllResources(){
        return courseResourceService.getAllResources();
    }

    @Operation(
            summary = "Retrieve  resource by course ID",
            description = "This endpoint retrieves resources by the specified course ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved resources"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/course/{courseId}")
    public List<CourseResource> getResourceByCourseId(@PathVariable int courseId){
        return courseResourceService.getResourcesByCourseId(courseId);
    }

    @Operation(
            summary = "Add a new resource",
            description = "This endpoint adds a new resource to the database."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully added resources"),
            @ApiResponse(responseCode = "400", description = "Invalid resource data")
    })
    @PostMapping
    public CourseResource addResource(@Valid @RequestBody CourseResource resource){
        if(resource.getCourse() == null){
            throw  new IllegalArgumentException("Course must not be null");
        }
        return courseResourceService.addResource(resource);
    }



    @Operation(
            summary = "Update an existing resources",
            description = "This endpoint update a course resource by his ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated resource"),
            @ApiResponse(responseCode = "404", description = "Resource not found")

    })
    @PutMapping("/{id}")
    public CourseResource updateResource(@Valid @RequestBody CourseResource resource,@PathVariable int id){
        return courseResourceService.updateResource(resource, id);
    }

    @Operation(
            summary = "Delete a resources",
            description = "This endpoint delete a course resource by his ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted resource"),
            @ApiResponse(responseCode = "404", description = "Resource not found")

    })
    @DeleteMapping("/{id}")
    public void deleteResource(@PathVariable int id){
        courseResourceService.deleteResource(id);
    }

    @PostMapping(value = "/upload-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.updloadDocument(file);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload document: " + e.getMessage());
        }
    }

    @PostMapping(value = "/upload-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.uploadVideo(file);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload video: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-document")
    public ResponseEntity<String> deleteDocument(@RequestParam String fileUrl) {
        try {
            fileStorageService.deleteDocument(fileUrl);
            return ResponseEntity.ok("Document deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete document: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-video")
    public ResponseEntity<String> deleteVideo(@RequestParam String fileUrl) {
        try {
            fileStorageService.deleteVideo(fileUrl);
            return ResponseEntity.ok("Video deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete video: " + e.getMessage());
        }
    }
}