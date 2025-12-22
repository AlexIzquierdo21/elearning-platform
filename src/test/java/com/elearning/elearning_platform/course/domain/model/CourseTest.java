package com.elearning.elearning_platform.course.domain.model;

import com.elearning.elearning_platform.course.domain.exception.InvalidCourseStateException;
import com.elearning.elearning_platform.course.domain.valueobject.CategoryId;
import com.elearning.elearning_platform.course.domain.valueobject.CourseId;
import com.elearning.elearning_platform.shared.domain.exception.ValidationException;
import com.elearning.elearning_platform.shared.domain.valueobject.Money;
import com.elearning.elearning_platform.user.domain.valueobject.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {

    @Test
    public void testCreateCourseHasDraftStatus() {
        // Given
        Course course = createValidCourse();

        // When
        CourseStatus status = course.getStatus();

        // Then
        assertEquals(CourseStatus.DRAFT, status);
    }

    @Test
    public void testCreateCourseWithValidData() {
        // Given
        String title = "Valid Course Title";
        String description = "A".repeat(50); // Descripción de 50 caracteres
        Money price = Money.of(new BigDecimal("10.00"), "EUR");
        UserId instructorId = UserId.generate();
        CategoryId categoryId = CategoryId.generate();

        // When
        Course course = Course.create(title, description, price, instructorId, categoryId);

        // Then
        assertNotNull(course);
        assertNotNull(course.getId());
        assertNull(course.getPublishedAt()); // aún no publicado
    }

    @Test
    public void testPublishCourseChangesStatusToPublished() {
        // Given
        Course course = createValidCourse(); // Course en DRAFT

        // When
        course.publish();

        // Then
        assertEquals(CourseStatus.PUBLISHED, course.getStatus());
        assertNotNull(course.getPublishedAt());
    }

    @Test
    public void testPublishAlreadyPublishedCourseThrowsException() {
        // Given
        Course course = createValidCourse(); // Course en DRAFT
        course.publish(); // ahora PUBLISHED

        // When/Then
        assertThrows(InvalidCourseStateException.class, () -> course.publish());
    }

    @Test
    public void testArchiveCourseChangesStatusToArchived() {
        // Given
        Course course = createValidCourse(); // Course en DRAFT
        course.publish(); // ahora PUBLISHED

        // When
        course.archive();

        // Then
        assertEquals(CourseStatus.ARCHIVED, course.getStatus());
    }

    @Test
    public void testArchiveDraftCourseThrowsException() {
        // Given
        Course course = createValidCourse(); // Course en DRAFT

        // When/Then
        assertThrows(InvalidCourseStateException.class, () -> course.archive());
    }

    @Test
    public void testIsOwnedByReturnsTrueForOwner() {
        // Given
        UserId instructorId = UserId.generate();
        Course course = Course.create(
                "Valid Course Title",
                "A".repeat(50),
                Money.of(new BigDecimal("10.00"), "EUR"),
                instructorId,
                CategoryId.generate()
        );

        // When
        boolean result = course.isOwnedBy(instructorId);

        // Then
        assertTrue(result);
    }

    @Test
    public void testIsOwnedByReturnsFalseForNonOwner() {
        // Given
        Course course = createValidCourse(); // creado con instructorId X
        UserId otherInstructor = UserId.generate(); // diferente

        // When
        boolean result = course.isOwnedBy(otherInstructor);

        // Then
        assertFalse(result);
    }

    @Test
    public void testCreateCourseWithBlankTitleThrowsException() {
        // Given
        String title = "";
        String description = "A".repeat(50);
        Money price = Money.of(new BigDecimal("10.00"), "EUR");
        UserId instructorId = UserId.generate();
        CategoryId categoryId = CategoryId.generate();

        // When/Then
        assertThrows(ValidationException.class, () ->
                Course.create(title, description, price, instructorId, categoryId));
    }

    @Test
    public void testCreateCourseWithShortDescriptionThrowsException() {
        // Given
        String title = "Valid Course Title";
        String description = "Short"; // 5 chars
        Money price = Money.of(new BigDecimal("10.00"), "EUR");
        UserId instructorId = UserId.generate();
        CategoryId categoryId = CategoryId.generate();

        // When/Then
        assertThrows(ValidationException.class, () ->
                Course.create(title, description, price, instructorId, categoryId));
    }

    /**
     * Helper method para evitar repetir creación de Course.
     * Crea un curso válido con datos por defecto.
     *
     * @return un Course válido en estado DRAFT
     */
    private Course createValidCourse() {
        return Course.create(
                "Valid Course Title",
                "A".repeat(50), // Descripción de 50 caracteres
                Money.of(new BigDecimal("10.00"), "EUR"),
                UserId.generate(),
                CategoryId.generate()
        );
    }
}
