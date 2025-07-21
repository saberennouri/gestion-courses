package com.TeachCode.Gestion_Courses.controller;

import com.TeachCode.Gestion_Courses.Feign.AuthServiceClient;
import com.TeachCode.Gestion_Courses.entities.CategoryEnum;
import com.TeachCode.Gestion_Courses.entities.Course;
import com.TeachCode.Gestion_Courses.services.ICourseService;
import com.TeachCode.Gestion_Courses.services.IFileStorageService;
import com.TeachCode.Gestion_Courses.services.JwtService;
import com.TeachCode.Gestion_Courses.services.QRCodeService;
import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name="Courses",description = "this web service handles CRUD operations for courses, ")
@RestController
@RequestMapping("/courses")
public class CourseRestController {
@Autowired
    private ICourseService courseService;
@Autowired
    private JwtService jwtService;
@Autowired
    private IFileStorageService fileStorageService;
@Autowired
    private QRCodeService qrCodeService;
@Autowired
    private AuthServiceClient authServiceClient;
@Value("Welcome to the Gestion Courses API")
    private String welcomeMessage;
@GetMapping("/welcome")
    public String welcome(){
    return welcomeMessage;
}
@Operation(summary = "Retrieve all courses",description = "this endpoint retrieves all courses from the data")
@ApiResponses(value={
        @ApiResponse(responseCode = "200",description="Succefully retrieved all courses"),
        @ApiResponse(responseCode = "500",description = "Internal server error")
})
    @GetMapping
    public List<Course> getAllCourses(){ return courseService.getAllCourses(); }
    @GetMapping("/my-courses")
    public ResponseEntity<List<Course>> getMyCourses(
     @RequestParam Integer userId,
             @RequestParam String userRole){
        System.out.println("[DEBUG] recieved request -userId:"+userId+",role:"+userRole);
        String normalizeRole = userRole.replaceAll("[\\[\\]]", "");
        System.out.println("[DEBUG] normalized Role:"+normalizeRole);
        List<Course> courses = Collections.emptyList();
        if ("TRAINER".equalsIgnoreCase(normalizeRole)){
            courses = courseService.getCoursesByTrainer(userId);
            System.out.println("[DEBUG] found:"+courses.size()+" courses for trainer");
        } else if ("STUDENT".equalsIgnoreCase(normalizeRole)) {
            courses = courseService.getCoursesByStudent(userId);
            System.out.println("[DEBUG] found:"+courses.size()+" courses for student");

        }
        return ResponseEntity.ok(courses);
    }
   @Operation(summary="Retrieve course by id ",description = "the endpoint retrieves a course by its ID")
   @ApiResponses(value={
           @ApiResponse(responseCode = "200",description = "successfully retrieved course"),
           @ApiResponse(responseCode = "404",description = "course not found")
   })
   @PostMapping
   public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course,
                                              @RequestParam Long trainerId) throws IOException,WriterException {

      //Add the course with the trainer ID
       Course savedCourse =courseService.addCourse(course,Math.toIntExact(trainerId));
       // Generate the QR code
       String qrText=String.format("Course:%s\nDescription:%s",
               savedCourse.getTitle(),savedCourse.getDescription());
       byte[] qrCode = qrCodeService.generateQRCodeImage(qrText,250,250);
       String qrCodeUrl=fileStorageService.uploadQRCode(qrCode,"qr-code-"+savedCourse.getId()+".png");

       // Upload the course with the QR code URL
       savedCourse.setQrCodeUrl(qrCodeUrl);
       courseService.updateCourse(savedCourse,savedCourse.getId());
       return ResponseEntity.ok(savedCourse);
   }
   @GetMapping("/{id}/qr-code-base64")
    public ResponseEntity<Map<String, String>> getCourseQRCodeBase64(@PathVariable int id) {
       try {
           Course course = courseService.getCourseById(id);
           if (course == null) {
               return ResponseEntity.notFound().build();
           }
           // Updated to only include name and description
           String qrText = String.format("Course:%s\nDescription: %s",
                   course.getTitle(), course.getDescription());

           byte[] qrCode = qrCodeService.generateQRCodeImage(qrText, 250, 250);
           String base64Image = Base64.getEncoder().encodeToString(qrCode);

           Map<String, String> response = new HashMap<>();
           response.put("qrCodeBase64", "data:image/png;base64," + base64Image);
           response.put("downloadUrl", "/courses/" + id + "/qr-code");

           return ResponseEntity.ok(response);
       } catch (WriterException | IOException e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
       }
   }

    @Operation(summary = "Generate QR code for course",
            description = "Generates a QR code containing course details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QR code generated successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/{id}/qr-code")
    public ResponseEntity<byte[]> getCourseQRCode(@PathVariable int id) {
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.notFound().build();
            }

            // Updated to only include name and description
            String qrText = String.format("Course: %s\nDescription: %s",
                    course.getTitle(), course.getDescription());

            byte[] qrCode = qrCodeService.generateQRCodeImage(qrText, 250, 250);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"course-" + id + "-qrcode.png\"")
                    .body(qrCode);
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Operation(summary = "Generate QR code with course URL", description = "Generates a QR code containing a URL to access the course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QR code generated successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping(value = "/{id}/qr-code-url", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getCourseQRCodeWithUrl(
            @PathVariable int id,
            @RequestParam String baseUrl) {

        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.notFound().build();
            }

            String courseUrl = baseUrl + "/courses/" + id;
            byte[] qrCode = qrCodeService.generateQRCodeImage(courseUrl, 250, 250);
            return ResponseEntity.ok().body(qrCode);

        } catch (WriterException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Operation(summary = "Update an existing course", description = "This endpoint updates a course by its ID and regenerates its QR code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated course"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable int id,
            @Valid @RequestBody Course course) throws IOException, WriterException {

        // First update the course
        Course updatedCourse = courseService.updateCourse(course, id);
        if (updatedCourse == null) {
            return ResponseEntity.notFound().build();
        }

        // Regenerate QR code with updated info
        String qrText = String.format("Course: %s\nDescription: %s",
                updatedCourse.getTitle(), updatedCourse.getDescription());

        byte[] qrCode = qrCodeService.generateQRCodeImage(qrText, 250, 250);
        String qrCodeUrl = fileStorageService.uploadQRCode(qrCode, "qr-code-" + updatedCourse.getId() + ".png");

        // Update the course with new QR code
        updatedCourse.setQrCodeUrl(qrCodeUrl);
        Course finalUpdatedCourse = courseService.updateCourse(updatedCourse, updatedCourse.getId());
        return ResponseEntity.ok(finalUpdatedCourse);
    }

    @Operation(summary = "Delete a course", description = "This endpoint deletes a course by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted course"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable int id) {
        courseService.deleteCourse(id);
    }

    @PostMapping("/{courseId}/enroll/{studentId}")
    public ResponseEntity<Course> enrollStudent(
            @PathVariable int courseId,
            @PathVariable Integer studentId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Course course = courseService.enrollStudentInCourse(courseId, studentId);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/students/details")
    public ResponseEntity<List<Map<String, Object>>> getAllStudentsWithDetails(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            List<Map<String, Object>> students = authServiceClient.getAllStudents();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{courseId}/students")
    public ResponseEntity<Set<Integer>> getEnrolledStudents(@PathVariable int courseId) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(course.getStudentIds());
    }

    @GetMapping("/{courseId}/students/details")
    public ResponseEntity<List<Map<String, Object>>> getEnrolledStudentsWithDetails(
            @PathVariable int courseId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Course course = courseService.getCourseById(courseId);
            if (course == null || course.getStudentIds() == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Map<String, Object>> students = course.getStudentIds().stream()
                    .map(studentId -> authServiceClient.getStudentById(studentId))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{courseId}/students/{studentId}")
    public ResponseEntity<Void> removeStudentFromCourse(
            @PathVariable int courseId,
            @PathVariable int studentId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            courseService.removeStudentFromCourse(courseId, studentId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/my-courses/search-all")
    public ResponseEntity<List<Course>> searchAllMyCourses(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String category,
            @RequestParam Integer userId,
            @RequestParam String userRole) {

        String normalizedRole = userRole.replaceAll("[\\[\\]]", "");
        CategoryEnum categoryEnum = null;

        if (category != null && !category.isEmpty()) {
            try {
                categoryEnum = CategoryEnum.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        List<Course> courses;
        if ("TRAINER".equalsIgnoreCase(normalizedRole)) {
            courses = courseService.searchAllCoursesByTrainer(searchQuery, categoryEnum, userId);
        } else if ("STUDENT".equalsIgnoreCase(normalizedRole)) {
            courses = courseService.searchAllCoursesByStudent(searchQuery, categoryEnum, userId);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")  // + Amira-Laabidi
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable int id) {
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("course", course);
        response.put("qrCodeUrl", course.getQrCodeUrl());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/update-rate")  // + Amira-Laabidi
    public ResponseEntity<Void> updateCourseRate(
            @PathVariable int id,
            @RequestBody Map<String, Double> request) {
        try {
            if (request == null || !request.containsKey("rate")) {
                return ResponseEntity.badRequest().build();
            }

            Double rate = request.get("rate");
            Course course = courseService.getCourseById(id);
            if(course == null) {
                return ResponseEntity.notFound().build();
            }
            course.setRate(rate);
            courseService.updateCourse(course,id);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.uploadFile(file);
            return ResponseEntity.ok(fileUrl); // Just return the plain string URL
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-file")
    public ResponseEntity<String> deleteFile(@RequestParam String fileUrl) {
        try {
            fileStorageService.deleteFile(fileUrl);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete file: " + e.getMessage());
        }
    }
}
