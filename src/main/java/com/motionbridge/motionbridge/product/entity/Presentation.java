package com.motionbridge.motionbridge.product.entity;

import com.motionbridge.motionbridge.jpa.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Presentation extends BaseEntity {
    String title;
    String content;
    String preview;
    String classes;
    @JoinColumn(name = "product_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    Product product;

    public Presentation(String title, String content, String preview, String classes, Product product) {
        this.title = title;
        this.content = content;
        this.preview = preview;
        this.classes = classes;
        this.product = product;
    }
}
