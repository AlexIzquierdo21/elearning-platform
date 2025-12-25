package com.elearning.elearning_platform.course.infrastructure.adapter.out.persistence;

import com.elearning.elearning_platform.course.domain.model.CourseStatus;
import com.elearning.elearning_platform.shared.infrastructure.persistence.BaseEntity;
import com.elearning.elearning_platform.user.infrastructure.adapter.out.persistence.UserEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing a course in the persistence layer.
 *
 * This entity maps to the "courses" table in the database and serves
 * as the infrastructure representation of a Course domain model.
 *
 * Key design decisions:
 * Money value object is decomposed into priceAmount and priceCurrency fields
 * The ID is not auto-generated; it is provided by the domain layer through CourseId
 * Foreign key relationships use both UUID fields (for mapper) and entity references (for JPA queries)
 *
 */
@Entity
@Table(name = "courses")
public class CourseEntity extends BaseEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "price_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAmount;

    @Column(name = "price_currency", nullable = false, length = 3)
    private String priceCurrency;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    @Column(name = "instructor_id", nullable = false)
    private UUID instructorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", insertable = false, updatable = false)
    private UserEntity instructor;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private CategoryEntity category;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    /**
     * Default no-args constructor required by JPA.
     */
    public CourseEntity() {
    }

    /**
     * Full constructor for creating a CourseEntity with all fields.
     *
     * @param id unique course identifier
     * @param title course title
     * @param description course description
     * @param priceAmount monetary amount of the course price
     * @param priceCurrency currency code (e.g., "EUR", "USD")
     * @param status current status of the course
     * @param instructorId UUID of the instructor who owns the course
     * @param categoryId UUID of the category the course belongs to
     * @param updatedAt timestamp of last update
     * @param publishedAt timestamp when course was published (null if not published)
     */
    public CourseEntity(UUID id, String title, String description,
                        BigDecimal priceAmount, String priceCurrency,
                        CourseStatus status, UUID instructorId, UUID categoryId,
                        LocalDateTime updatedAt, LocalDateTime publishedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priceAmount = priceAmount;
        this.priceCurrency = priceCurrency;
        this.status = status;
        this.instructorId = instructorId;
        this.categoryId = categoryId;
        this.updatedAt = updatedAt;
        this.publishedAt = publishedAt;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(BigDecimal priceAmount) {
        this.priceAmount = priceAmount;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public UUID getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(UUID instructorId) {
        this.instructorId = instructorId;
    }

    public UserEntity getInstructor() {
        return instructor;
    }

    public void setInstructor(UserEntity instructor) {
        this.instructor = instructor;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}