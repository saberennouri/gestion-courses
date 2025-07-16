package com.TeachCode.Gestion_Courses.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "courses_resource")
public class CourseResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @NotNull(message = "Resource type is required")
    private String resourceType;
    @NotNull(message = "link of video is required")
    private String linkVideo;  // Changed to camelCase
    @NotNull(message = "link of document is required")
    private String linkDocument;  // Changed to camelCase

    @NotNull(message = "Description is required")
    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    private String description;

    @NotNull(message = "Upload date required")
    private java.sql.Date uploadDate;  // Changed to LocalDate

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "courses_id")
    private Course course;
}