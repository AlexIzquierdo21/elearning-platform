package com.elearning.elearning_platform.course.application.usecase;

import com.elearning.elearning_platform.course.application.port.in.PublishCourseCommand;
import com.elearning.elearning_platform.course.domain.exception.CourseNotFoundException;
import com.elearning.elearning_platform.course.domain.exception.InvalidCourseStateException;
import com.elearning.elearning_platform.course.domain.model.Course;
import com.elearning.elearning_platform.course.domain.model.CourseStatus;
import com.elearning.elearning_platform.course.domain.port.out.CourseRepositoryPort;
import com.elearning.elearning_platform.course.domain.valueobject.CategoryId;
import com.elearning.elearning_platform.course.domain.valueobject.CourseId;
import com.elearning.elearning_platform.shared.domain.exception.ValidationException;
import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.shared.domain.valueobject.Money;
import com.elearning.elearning_platform.user.domain.exception.UserNotFoundException;
import com.elearning.elearning_platform.user.domain.model.Role;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import com.elearning.elearning_platform.user.domain.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishCourseUseCaseTest {

    @Mock
    private CourseRepositoryPort courseRepository;

    @Mock
    private UserRepositoryPort userRepository;

    private PublishCourseUseCase publishCourseUseCase;

    @BeforeEach
    void setUp() {
        publishCourseUseCase = new PublishCourseUseCase(courseRepository, userRepository);
    }

    @Test
    void testPublishCourseSuccess() {
        // Given
        CourseId courseId = CourseId.generate();
        UserId instructorId = UserId.of(UUID.randomUUID());
        PublishCourseCommand command = new PublishCourseCommand(courseId, instructorId);

        Course course = createDraftCourse(courseId, instructorId);
        User instructor = createActiveInstructor(instructorId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findById(instructorId.value())).thenReturn(Optional.of(instructor));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // When
        Course result = publishCourseUseCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(CourseStatus.PUBLISHED, result.getStatus());
        assertNotNull(result.getPublishedAt());

        verify(courseRepository).findById(courseId);
        verify(userRepository).findById(instructorId.value());
        verify(courseRepository).save(course);
    }

    @Test
    void testPublishCourseNotFound() {
        // Given
        CourseId courseId = CourseId.generate();
        UserId instructorId = UserId.of(UUID.randomUUID());
        PublishCourseCommand command = new PublishCourseCommand(courseId, instructorId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // When & Then
        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class,
                () -> publishCourseUseCase.execute(command));
        assertEquals("Course with ID " + courseId + " not found", exception.getMessage());

        verify(courseRepository).findById(courseId);
        verify(userRepository, never()).findById(any());
        verify(courseRepository, never()).save(any());
    }

    @Test
    void testPublishCourseNotOwner() {
        // Given
        CourseId courseId = CourseId.generate();
        UserId instructorId = UserId.of(UUID.randomUUID());
        UserId differentInstructorId = UserId.of(UUID.randomUUID());
        PublishCourseCommand command = new PublishCourseCommand(courseId, differentInstructorId);

        Course course = createDraftCourse(courseId, instructorId); // owned by a different instructor
        User requestingUser = createActiveInstructor(differentInstructorId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findById(differentInstructorId.value())).thenReturn(Optional.of(requestingUser));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> publishCourseUseCase.execute(command));
        assertEquals("User is not the owner of the course", exception.getMessage());

        verify(courseRepository).findById(courseId);
        verify(userRepository).findById(differentInstructorId.value());
        verify(courseRepository, never()).save(any());
    }

    @Test
    void testPublishCourseWithInactiveInstructor() {
        // Given
        CourseId courseId = CourseId.generate();
        UserId instructorId = UserId.of(UUID.randomUUID());
        PublishCourseCommand command = new PublishCourseCommand(courseId, instructorId);

        Course course = createDraftCourse(courseId, instructorId);
        User inactiveInstructor = createInactiveInstructor(instructorId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findById(instructorId.value())).thenReturn(Optional.of(inactiveInstructor));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> publishCourseUseCase.execute(command));
        assertEquals("User is not active", exception.getMessage());

        verify(courseRepository).findById(courseId);
        verify(userRepository).findById(instructorId.value());
        verify(courseRepository, never()).save(any());
    }

    @Test
    void testPublishCourseWithNonInstructorRole() {
        // Given
        CourseId courseId = CourseId.generate();
        UserId userId = UserId.of(UUID.randomUUID());
        PublishCourseCommand command = new PublishCourseCommand(courseId, userId);

        Course course = createDraftCourse(courseId, userId);
        User student = createActiveStudent(userId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findById(userId.value())).thenReturn(Optional.of(student));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> publishCourseUseCase.execute(command));
        assertEquals("User must be an INSTRUCTOR to publish a course", exception.getMessage());

        verify(courseRepository).findById(courseId);
        verify(userRepository).findById(userId.value());
        verify(courseRepository, never()).save(any());
    }

    @Test
    void testPublishCourseWithUserNotFound() {
        // Given
        CourseId courseId = CourseId.generate();
        UserId instructorId = UserId.of(UUID.randomUUID());
        PublishCourseCommand command = new PublishCourseCommand(courseId, instructorId);

        Course course = createDraftCourse(courseId, instructorId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findById(instructorId.value())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> publishCourseUseCase.execute(command));

        verify(courseRepository).findById(courseId);
        verify(userRepository).findById(instructorId.value());
        verify(courseRepository, never()).save(any());
    }

    @Test
    void testPublishAlreadyPublishedCourse() {
        // Given
        CourseId courseId = CourseId.generate();
        UserId instructorId = UserId.of(UUID.randomUUID());
        PublishCourseCommand command = new PublishCourseCommand(courseId, instructorId);

        Course publishedCourse = createPublishedCourse(courseId, instructorId);
        User instructor = createActiveInstructor(instructorId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(publishedCourse));
        when(userRepository.findById(instructorId.value())).thenReturn(Optional.of(instructor));

        // When & Then
        assertThrows(InvalidCourseStateException.class, () -> publishCourseUseCase.execute(command));

        verify(courseRepository).findById(courseId);
        verify(userRepository).findById(instructorId.value());
        verify(courseRepository, never()).save(any());
    }

    // Helper methods
    private Course createDraftCourse(CourseId courseId, UserId instructorId) {
        return new Course(
                courseId,
                "Java Programming",
                "Learn Java programming from scratch with hands-on examples and exercises",
                Money.of(new BigDecimal("49.99"), "USD"),
                CourseStatus.DRAFT,
                instructorId,
                CategoryId.generate(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Course createPublishedCourse(CourseId courseId, UserId instructorId) {
        return new Course(
                courseId,
                "Java Programming",
                "Learn Java programming from scratch with hands-on examples and exercises",
                Money.of(new BigDecimal("49.99"), "USD"),
                CourseStatus.PUBLISHED,
                instructorId,
                CategoryId.generate(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private User createActiveInstructor(UserId instructorId) {
        return User.fromRepository(
                instructorId,
                Email.of("instructor@example.com"),
                "hashedPassword123",
                "John",
                "Instructor",
                Role.INSTRUCTOR,
                true
        );
    }

    private User createInactiveInstructor(UserId instructorId) {
        return User.fromRepository(
                instructorId,
                Email.of("instructor@example.com"),
                "hashedPassword123",
                "John",
                "Instructor",
                Role.INSTRUCTOR,
                false // inactive
        );
    }

    private User createActiveStudent(UserId userId) {
        return User.fromRepository(
                userId,
                Email.of("student@example.com"),
                "hashedPassword123",
                "Jane",
                "Student",
                Role.STUDENT,
                true
        );
    }
}
