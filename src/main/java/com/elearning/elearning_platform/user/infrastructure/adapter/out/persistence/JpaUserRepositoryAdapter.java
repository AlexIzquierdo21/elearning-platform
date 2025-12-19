package com.elearning.elearning_platform.user.infrastructure.adapter.out.persistence;

import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter that implements {@link UserRepositoryPort} using JPA.
 *
 * Bridges the domain repository interface with Spring Data JPA.
 * Handles mapping between {@link User} (domain) and {@link UserEntity} (persistence).
 */
@Component
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepositorySpring jpaRepository;

    /**
     * Constructor injecting the JPA repository.
     *
     * @param jpaRepository Spring Data JPA repository
     */
    public JpaUserRepositoryAdapter(JpaUserRepositorySpring jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Saves a {@link User} in the database.
     *
     * @param user the domain User to save
     * @return the saved User with updated fields (e.g., generated ID)
     */
    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return UserMapper.toDomain(saved);
    }

    /**
     * Finds a User by its UUID.
     *
     * @param id the user ID
     * @return an Optional containing the User if found, or empty
     */
    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    /**
     * Finds a User by email.
     *
     * @param email the user's email
     * @return an Optional containing the User if found, or empty
     */
    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue())
                .map(UserMapper::toDomain);
    }

    /**
     * Checks if a User exists by email.
     *
     * @param email the user's email
     * @return true if a user with the email exists, false otherwise
     */
    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }

    /**
     * Deletes a User by its UUID.
     *
     * @param id the user's ID
     */
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all Users in the system
     */
    @Override
    public List<User> findAll() {
        List<UserEntity> userEntities = jpaRepository.findAll();
        return userEntities.stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }
}
















