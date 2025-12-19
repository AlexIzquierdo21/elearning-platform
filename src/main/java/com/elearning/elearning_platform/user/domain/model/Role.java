package com.elearning.elearning_platform.user.domain.model;

/**
 * Enumeration that defines the roles available in the system.
 *
 * Roles determine the permissions and functionalities that each user
 * can access on the e-learning platform.
 *
 */
public enum Role {

    /**
     * System administrator with full access.
     * Can manage users, categories, and view global metrics.
     */
    ADMIN,

    /**
     * Instructor who can create and manage courses.
     * Can create content, publish courses, and view their metrics.
     */
    INSTRUCTOR,

    /**
     * Students who can purchase and access courses.
     * Can browse the catalog, purchase courses, and rate them.
     */
    STUDENT
}
