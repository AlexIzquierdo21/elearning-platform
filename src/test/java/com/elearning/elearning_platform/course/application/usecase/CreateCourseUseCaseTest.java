package com.elearning.elearning_platform.course.application.usecase;

import com.elearning.elearning_platform.course.application.port.in.CreateCourseCommand;
import com.elearning.elearning_platform.course.domain.exception.CategoryNotFoundException;
import com.elearning.elearning_platform.course.domain.model.Category;
import com.elearning.elearning_platform.course.domain.model.Course;
import com.elearning.elearning_platform.course.domain.port.out.CategoryRepositoryPort;
import com.elearning.elearning_platform.course.domain.port.out.CourseRepositoryPort;
import com.elearning.elearning_platform.course.domain.valueobject.CategoryId;
import com.elearning.elearning_platform.shared.domain.exception.ValidationException;
import com.elearning.elearning_platform.shared.domain.valueobject.Money;
import com.elearning.elearning_platform.user.domain.exception.UserNotFoundException;
import com.elearning.elearning_platform.user.domain.model.Role;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import com.elearning.elearning_platform.user.domain.valueobject.UserId;
import com.elearning.elearning_platform.shared.domain.valueobject.Email;
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
class CreateCourseUseCaseTest {

    @Mock
    private CourseRepositoryPort courseRepository;

    @Mock
    private CategoryRepositoryPort categoryRepository;

    @Mock
    private UserRepositoryPort userRepository;

    private CreateCourseUseCase createCourseUseCase;

    @BeforeEach
    void setUp() {
        createCourseUseCase = new CreateCourseUseCase(courseRepository, categoryRepository, userRepository);
    }

    @Test
    void testCreateCourseSuccess() {
        // Given
        UserId instructorId = UserId.of(UUID.randomUUID());
        CategoryId categoryId = CategoryId.generate();
        CreateCourseCommand command = new CreateCourseCommand(
                "Java Programming Fundamentals",
                "A comprehensive course covering Java programming from basics to advanced concepts",
                Money.of(new BigDecimal("49.99"), "USD"),
                categoryId,
                instructorId
        );

        User instructor = createInstructor(instructorId, true, Role.INSTRUCTOR);
        Category category = createActiveCategory(categoryId);

        when(userRepository.findById(instructorId.value())).thenReturn(Optional.of(instructor));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // When
        Course result = createCourseUseCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(command.title(), result.getTitle());
        assertEquals(command.description(), result.getDescription());
        assertEquals(command.price(), result.getPrice());
        assertEquals(command.instructorId(), result.getInstructorId());
        assertEquals(command.categoryId(), result.getCategoryId());

        verify(userRepository).findById(instructorId.value());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void testCreateCourseWithInvalidInstructor() {
        // Given
        UserId instructorId = UserId.of(UUID.randomUUID());
        CategoryId categoryId = CategoryId.generate();
        CreateCourseCommand command = createValidCommand(instructorId, categoryId);

        when(userRepository.findById(instructorId.value())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> createCourseUseCase.execute(command));

        verify(userRepository).findById(instructorId.value());
        verify(categoryRepository, never()).findById(any());
    }

    @Test
    void testCreateCourseWithInactiveInstructor() {
        // Given
        UserId instructorId = UserId.of(UUID.randomUUID());
        CategoryId categoryId = CategoryId.generate();
        CreateCourseCommand command = createValidCommand(instructorId, categoryId);

        User inactiveInstructor = createInstructor(instructorId, false, Role.INSTRUCTOR); // inactive

        when(userRepository.findById(instructorId.value())).thenReturn(Optional.of(inactiveInstructor));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> createCourseUseCase.execute(command));
        assertEquals("Instructor is not active", exception.getMessage());

        verify(userRepository).findById(instructorId.value());
        verify(categoryRepository, never()).findById(any());
    }

    @Test
    void testCreateCourseWithNonInstructorRole() {
        // Given
        UserId userId = UserId.of(UUID.randomUUID());
        CategoryId categoryId = CategoryId.generate();
        CreateCourseCommand command = createValidCommand(userId, categoryId);

        User student = createInstructor(userId, true, Role.STUDENT); // not instructor

        when(userRepository.findById(userId.value())).thenReturn(Optional.of(student));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> createCourseUseCase.execute(command));
        assertEquals("User must be an INSTRUCTOR to create a course", exception.getMessage());

        verify(userRepository).findById(userId.value());
        verify(categoryRepository, never()).findById(any());
    }

    @Test
    void testCreateCourseWithInvalidCategory() {
        // Given
        UserId instructorId = UserId.of(UUID.randomUUID());
        CategoryId categoryId = CategoryId.generate();
        CreateCourseCommand command = createValidCommand(instructorId, categoryId);

        User instructor = createInstructor(instructorId, true, Role.INSTRUCTOR);

        when(userRepository.findById(instructorId.value())).thenReturn(Optional.of(instructor));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CategoryNotFoundException.class, () -> createCourseUseCase.execute(command));

        verify(userRepository).findById(instructorId.value());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void testCreateCourseWithInactiveCategory() {
        // Given
        UserId instructorId = UserId.of(UUID.randomUUID());
        CategoryId categoryId = CategoryId.generate();
        CreateCourseCommand command = createValidCommand(instructorId, categoryId);

        User instructor = createInstructor(instructorId, true, Role.INSTRUCTOR);
        Category inactiveCategory = createActiveCategory(categoryId);
        inactiveCategory.deactivate(); // make it inactive

        when(userRepository.findById(instructorId.value())).thenReturn(Optional.of(instructor));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(inactiveCategory));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> createCourseUseCase.execute(command));
        assertEquals("Category is not active", exception.getMessage());

        verify(userRepository).findById(instructorId.value());
        verify(categoryRepository).findById(categoryId);
    }

    // Helper methods
    private CreateCourseCommand createValidCommand(UserId instructorId, CategoryId categoryId) {
        return new CreateCourseCommand(
                "Java Programming Fundamentals",
                "A comprehensive course covering Java programming from basics to advanced concepts",
                Money.of(new BigDecimal("49.99"), "USD"),
                categoryId,
                instructorId
        );
    }

    private User createInstructor(UserId instructorId, boolean active, Role role) {
        return User.fromRepository(
                instructorId,
                Email.of("instructor@example.com"),
                "hashedPassword123",
                "John",
                "Doe",
                role,
                active
        );
    }

    private Category createActiveCategory(CategoryId categoryId) {
        return new Category(
                categoryId,
                "Programming",
                "Programming and Development courses",
                "programming",
                true,
                LocalDateTime.now()
        );
    }
}
