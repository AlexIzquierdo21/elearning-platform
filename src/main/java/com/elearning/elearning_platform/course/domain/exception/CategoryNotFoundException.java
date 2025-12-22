package com.elearning.elearning_platform.course.domain.exception;

import com.elearning.elearning_platform.course.domain.valueobject.CategoryId;
import com.elearning.elearning_platform.shared.domain.exception.NotFoundException;

/**
 * Exception thrown when a specified category cannot be found in the system.
 *
 * This exception is a specialization of {@link NotFoundException} and is used
 * specifically for scenarios where an operation fails due to a category not
 * being available or identifiable by the given criteria.
 */
public class CategoryNotFoundException extends NotFoundException {

    /**
     * Constructs a new CategoryNotFoundException with the specified category ID.
     *
     * This exception is thrown when a category identified by the given category ID
     * cannot be located in the system.
     *
     * @param categoryId the unique identifier of the category that could not be found
     */
    public CategoryNotFoundException(CategoryId categoryId) {
        super("Category", categoryId.toString());
    }

    /**
     * Constructs a new CategoryNotFoundException with a detailed message.
     *
     * This exception is thrown when a specific category cannot be found,
     * providing additional context through a descriptive message.
     *
     * @param message a detailed message explaining the reason for the exception
     */
    public CategoryNotFoundException(String message) {
        super("Category", message);
    }
}
