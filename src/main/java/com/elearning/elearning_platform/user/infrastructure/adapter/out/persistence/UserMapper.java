package com.elearning.elearning_platform.user.infrastructure.adapter.out.persistence;

import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.valueobject.UserId;

/**
 * Mapper class to convert between Domain {@link User} and JPA {@link UserEntity}.
 *
 * Provides static utility methods for two-way conversion.
 * This is used by the persistence adapter to isolate domain from infrastructure.
 */
public class UserMapper {

    /**
     * Private constructor to prevent instantiation.
     */
    private UserMapper() {
    }


    /**
     * Converts a {@link User} domain object to a {@link UserEntity} suitable for persistence.
     *
     * @param user the domain User object
     * @return the corresponding UserEntity
     */
    public static UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId().value(),  // ← CAMBIO: UserId → UUID
                user.getEmail().getValue(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getActive()
        );
    }

    /**
     * Converts a {@link UserEntity} from persistence into a {@link User} domain object.
     *
     * @param entity the JPA UserEntity
     * @return the corresponding domain User object
     */
    public static User toDomain(UserEntity entity) {
        return User.fromRepository(
                UserId.of(entity.getId()),
                Email.of(entity.getEmail()),
                entity.getPassword(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getRole(),
                entity.getActive()
        );
    }
}
