package com.motionbridge.motionbridge.users.entity;

import com.motionbridge.motionbridge.jpa.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@OnDelete(action= OnDeleteAction.CASCADE)
public class UserEntity extends BaseEntity {

    @CreatedDate
    LocalDateTime createdAt;

    @LastModifiedDate
    LocalDateTime modifiedAt;

    String login;

    Boolean isBlocked = false;

    Boolean isVerified = false;

    Boolean acceptedTerms;

    Boolean acceptedNewsletter;

    String email;

    String password;

    @CollectionTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    Set<String> roles = new HashSet<>();

    public UserEntity(String login, String email, String password, Boolean acceptedTerms, Boolean acceptedNewsletter) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.acceptedTerms = acceptedTerms;
        this.acceptedNewsletter = acceptedNewsletter;
        this.roles = Set.of("ROLE_USER");
    }
}
