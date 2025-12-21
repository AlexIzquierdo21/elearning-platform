package com.elearning.elearning_platform.shared.infrastructure.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A simple controller to test secured endpoints for different roles.
 *
 * Provides endpoints to verify that role-based access control is working correctly.
 * Each endpoint corresponds to a specific role: ADMIN, INSTRUCTOR, or STUDENT.
 */
@RestController
public class TestSecurityController {

    /**
     * Test endpoint for ADMIN role.
     * Accessible only to users with the ADMIN role.
     *
     * @return a simple string confirming the ADMIN endpoint is working
     */
    @GetMapping("/admin/test")
    public String adminTest() {
        return "Admin endpoint works!";
    }

    /**
     * Test endpoint for INSTRUCTOR role.
     * Accessible only to users with the INSTRUCTOR role.
     *
     * @return a simple string confirming the INSTRUCTOR endpoint is working
     */
    @GetMapping("/instructor/test")
    public String instructorTest() {
        return "Instructor endpoint works!";
    }

    /**
     * Test endpoint for STUDENT role.
     * Accessible only to users with the STUDENT role.
     *
     * @return a simple string confirming the STUDENT endpoint is working
     */
    @GetMapping("/student/test")
    public String studentTest() {
        return "Student endpoint works!";
    }

    /**
     * Public endpoint accessible without authentication.
     *
     * Used to verify that public routes are not protected by JWT or role-based security.
     *
     * @return a simple string confirming the public courses endpoint works
     */
    @GetMapping("/courses")
    public String publicCourses() {
        return "Public courses catalog";
    }

    /**
     * Protected endpoint accessible only to users with the INSTRUCTOR role.
     *
     * Used to verify that instructors can access course creation functionality
     * and that role-based authorization is correctly enforced.
     *
     * @return a simple string confirming course creation by an instructor
     */
    @PostMapping("/instructor/courses")
    public String createCourse() {
        return "Course created by instructor";
    }

    /**
     * Protected endpoint accessible only to users with the ADMIN role.
     *
     * Used to verify that administrators can manage users
     * and that ADMIN-level authorization is correctly enforced.
     *
     * @return a simple string confirming user management by an admin
     */
    @PostMapping("/admin/users")
    public String manageUsers() {
        return "User managed by admin";
    }

}
