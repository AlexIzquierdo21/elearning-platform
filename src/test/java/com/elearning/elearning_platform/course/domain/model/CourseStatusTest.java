package com.elearning.elearning_platform.course.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CourseStatusTest {

    @Test
    public void testDraftCanTransitionToPublished() {
        // Given
        CourseStatus status = CourseStatus.DRAFT;

        // When
        boolean result = status.canTransitionTo(CourseStatus.PUBLISHED);

        // Then
        assertTrue(result);
    }

    @Test
    public void testDraftCannotTransitionToArchived() {
        // Given
        CourseStatus status = CourseStatus.DRAFT;

        // When
        boolean result = status.canTransitionTo(CourseStatus.ARCHIVED);

        // Then
        assertFalse(result);
    }

    @Test
    public void testPublishedCanTransitionToArchived() {
        // Given
        CourseStatus status = CourseStatus.PUBLISHED;

        // When
        boolean result = status.canTransitionTo(CourseStatus.ARCHIVED);

        // Then
        assertTrue(result);
    }

    @Test
    public void testPublishedCannotTransitionToDraft() {
        // Given
        CourseStatus status = CourseStatus.PUBLISHED;

        // When
        boolean result = status.canTransitionTo(CourseStatus.DRAFT);

        // Then
        assertFalse(result);
    }

    @Test
    public void testArchivedCannotTransitionToAnything() {
        // Given
        CourseStatus status = CourseStatus.ARCHIVED;

        // When
        boolean toPublished = status.canTransitionTo(CourseStatus.PUBLISHED);
        boolean toDraft = status.canTransitionTo(CourseStatus.DRAFT);
        boolean toArchived = status.canTransitionTo(CourseStatus.ARCHIVED);

        // Then
        assertFalse(toPublished);
        assertFalse(toDraft);
        assertFalse(toArchived);
    }

    @Test
    public void testCannotTransitionToSameStatus() {
        // DRAFT → DRAFT = false
        assertFalse(CourseStatus.DRAFT.canTransitionTo(CourseStatus.DRAFT));

        // PUBLISHED → PUBLISHED = false
        assertFalse(CourseStatus.PUBLISHED.canTransitionTo(CourseStatus.PUBLISHED));

        // ARCHIVED → ARCHIVED = false
        assertFalse(CourseStatus.ARCHIVED.canTransitionTo(CourseStatus.ARCHIVED));
    }

    @Test
    public void testGetTransitionErrorMessageForSameStatus() {
        // Given
        CourseStatus status = CourseStatus.DRAFT;

        // When
        String message = status.getTransitionErrorMessage(CourseStatus.DRAFT);

        // Then
        assertNotNull(message);
        assertTrue(message.contains("already") || message.contains("DRAFT"));
    }

    @Test
    public void testGetTransitionErrorMessageForInvalidTransition() {
        // Given
        CourseStatus status = CourseStatus.DRAFT;

        // When
        String message = status.getTransitionErrorMessage(CourseStatus.ARCHIVED);

        // Then
        assertNotNull(message);
        assertTrue(message.contains("published")); // porque DRAFT solo puede ir a PUBLISHED
    }
}