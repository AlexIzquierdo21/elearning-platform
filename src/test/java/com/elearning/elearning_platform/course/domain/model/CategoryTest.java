package com.elearning.elearning_platform.course.domain.model;

import com.elearning.elearning_platform.shared.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {

    @Test
    public void testCreateCategoryGeneratesValidSlug() {
        // Given
        String name = "Programming & Development";

        // When
        Category category = Category.create(name, "Description");

        // Then
        assertEquals("programming-development", category.getSlug());
    }

    @Test
    public void testCreateCategoryWithSpecialCharacters() {
        // Given
        String name = "Data Science: AI & ML!!!";

        // When
        Category category = Category.create(name, "Description");

        // Then
        assertEquals("data-science-ai-ml", category.getSlug());
    }

    @Test
    public void testCreateCategoryWithBlankNameThrowsException() {
        // Given
        String name = "";

        // When/Then
        assertThrows(ValidationException.class, () -> Category.create(name, "Description"));
    }

    @Test
    public void testCreateCategoryWithLongNameThrowsException() {
        // Given
        String name = "A".repeat(101); // 101 caracteres

        // When/Then
        assertThrows(ValidationException.class, () -> Category.create(name, "Description"));
    }

    @Test
    public void testDeactivateCategorySetsActiveFalse() {
        // Given
        Category category = Category.create("Name", "Description"); // active=true por defecto

        // When
        category.deactivate();

        // Then
        assertFalse(category.isActive());
    }

    @Test
    public void testActivateCategorySetsActiveTrue() {
        // Given
        Category category = Category.create("Name", "Description");
        category.deactivate(); // ahora active=false

        // When
        category.activate();

        // Then
        assertTrue(category.isActive());
    }
}
