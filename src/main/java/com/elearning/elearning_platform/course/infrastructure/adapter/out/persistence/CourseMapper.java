package com.elearning.elearning_platform.course.infrastructure.adapter.out.persistence;

import com.elearning.elearning_platform.course.domain.model.Course;
import com.elearning.elearning_platform.course.domain.valueobject.CourseId;

public class CourseMapper {

    private CourseMapper() {}

    public static CourseEntity toEntity(Course course) {
        return new CourseEntity(
                course.getId().value(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice().getAmount(),
                course.getStatus(),
                course.getInstructorId().value(),
                course.getCategoryId().value(),
                course.getPublishedAt()
        );
    }

    public static Course toDomain (CourseEntity entity) {
        return Course.fromRepository(
                CourseId.of(entity.getId()),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStatus(),
                entity.getInstructorId(),
                entity.getCategoryId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getPublishedAt()
        );
    }
}
