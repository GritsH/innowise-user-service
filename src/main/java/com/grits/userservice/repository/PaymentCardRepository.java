package com.grits.userservice.repository;

import com.grits.userservice.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, UUID>, JpaSpecificationExecutor<PaymentCard> {

    @Query("""
       SELECT p
       FROM PaymentCard p
       WHERE p.user.id = :userId
       """)
    List<PaymentCard> findAllByUserId(@Param("userId") UUID userId);

    @Query(
            value = """
            SELECT COUNT(*)
            FROM userservice.payment_card
            WHERE user_id = :userId
            """,
            nativeQuery = true
    )
    int countByUserId(@Param("userId") UUID userId);

    boolean existsByNumber(String number);
}
