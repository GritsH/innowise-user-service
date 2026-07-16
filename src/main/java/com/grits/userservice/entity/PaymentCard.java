package com.grits.userservice.entity;

import com.grits.userservice.entity.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "`payment_card`")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCard extends AuditableEntity {

    @Id
    @Column(
            name = "id",
            length = 36,
            updatable = false,
            nullable = false,
            unique = true
    )
    private String id;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "holder", nullable = false)
    private String holder;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
