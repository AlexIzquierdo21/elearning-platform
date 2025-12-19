package com.elearning.elearning_platform.user.domain.port.out;


import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.user.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface that defines the contract for persisting and retrieving {@link User} entities.
 *
 * This is an output port in the Hexagonal Architecture:
 * the domain defines what it needs from the outside world (repositories),
 * without knowing the implementation details (JPA, JDBC, Mongo, etc.).
 *
 */
public interface UserRepositoryPort {

    /**
     * Persists a new user or updates an existing one.
     *
     * @param user the user entity to save
     * @return the saved {@link User} instance, usually with an assigned ID
     */
    User save(User user);

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the ID of the user
     * @return an {@link Optional} containing the user if found, or empty otherwise
     */
    Optional<User> findById(UUID id);

    /**
     * Retrieves a user by its unique email.
     *
     * @param email the email of the user
     * @return an {@link Optional} containing the user if found, or empty otherwise
     */
    Optional<User> findByEmail(Email email);

    /**
     * Checks whether a user exists with the given email.
     *
     * @param email the email to check for existence
     * @return true if a user with the given email exists, false otherwise
     */
    boolean existsByEmail(Email email);

    /**
     * Deletes a user by its unique identifier.
     *
     * @param id the ID of the user to delete
     */
    void deleteById(UUID id);

    /**
     * Retrieves all users in the system.
     *
     * @return a {@link List} of all {@link User} entities
     */
    List<User> findAll();
}
