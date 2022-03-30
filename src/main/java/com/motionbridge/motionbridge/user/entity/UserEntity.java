package com.motionbridge.motionbridge.user.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "uuid")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name ="user")
public class UserEntity {

    @Id
    @GeneratedValue
    Long id;

    String uuid = UUID.randomUUID().toString();

    @CreatedDate
    LocalDateTime createdAt;

    @LastModifiedDate
    LocalDateTime modifiedAt;

    String username;

    String password;

    @CollectionTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    Set<String> roles;

}
