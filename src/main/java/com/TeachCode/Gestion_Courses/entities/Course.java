package com.TeachCode.Gestion_Courses.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @NotBlank(message = "Level is required")
    @Size(min = 1, max = 20, message = "Level must be between 1 and 20 characters")
    private String level;

    @NotBlank(message = "Description is required")
    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    private String description;

    @Min(0) @Max(5)
    private double rate = 0; // Average rating of the course with default value

    @NotBlank(message = "Image URL is required")
    private String image;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    private CategoryEnum categoryCourse;

    // Simplified trainer information (will be linked via Feign later)
    private Long trainerId;
    private String trainerName;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CourseResource> resources;

    private String qrCodeURL; // URL to the generated QR code image

    @ElementCollection
    private Set<Integer> studentIds = new HashSet<>();

    public String getQrCodeUrl() {
        return this.qrCodeURL;
    }

    public void setQrCodeUrl(String qrCodeURL) {
        this.qrCodeURL = qrCodeURL;
    }
}