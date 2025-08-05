package vn.minhnhat.restapi.domain;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.minhnhat.restapi.util.SecurityUtil;
import vn.minhnhat.restapi.util.constant.StatusEnum;

@Entity
@Table(name = "resumes")
@Getter
@Setter
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Email can not emty")
    private String email;
    private String url;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job jobs;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "system";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "system";
        this.updatedAt = Instant.now();
    }
}
