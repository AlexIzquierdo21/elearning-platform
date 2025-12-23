package com.elearning.elearning_platform.user.infrastructure.adapter.out.persistence;

import com.elearning.elearning_platform.shared.infrastructure.persistence.BaseEntity;
import com.elearning.elearning_platform.user.domain.model.Role;
import com.elearning.elearning_platform.user.domain.valueobject.UserId;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * JPA Entity representing a User in the database.
 *
 * Maps the domain User model to the "users" table.
 * Extends {@link BaseEntity} to inherit auditing fields (createdAt, updatedAt).
 */
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

    /**
     * Primary key of the user, generated as a UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * User's email, must be unique and not null.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * User's hashed password, required field.
     */
    @Column(nullable = false)
    private String password;

    /**
     * User's first name, required field with max length 100.
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * User's last name, required field with max length 100.
     */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * User's role, stored as enum.
     */
    @Column(nullable = false)
    private Role role;

    /**
     * Indicates if the user is active or not.
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Default no-args constructor required by JPA.
     */
    public UserEntity() {}

    /**
     * Full constructor to create a UserEntity instance.
     *
     * @param id User's UUID
     * @param email User's email
     * @param password Hashed password
     * @param firstName First name
     * @param lastName Last name
     * @param role User role
     * @param active Active status
     */
    public UserEntity(UUID id, String email, String password,
                      String firstName, String lastName,
                      Role role, Boolean active) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.active = active;
    }

    /// --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
