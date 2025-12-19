package com.elearning.elearning_platform.user.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository interface for {@link UserEntity}.
 *
 * Provides CRUD operations and custom queries for User persistence.
 * Extends {@link JpaRepository} to leverage Spring Data JPA features.
 */
@Repository
public interface JpaUserRepositorySpring extends JpaRepository<UserEntity, UUID> {

    /**
     * Finds a user by their email.
     *
     * @param email the user's email
     * @return an Optional containing the UserEntity if found, or empty otherwise
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email the user's email
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
