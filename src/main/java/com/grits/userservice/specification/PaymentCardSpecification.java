package com.grits.userservice.specification;

import com.grits.userservice.entity.PaymentCard;
import org.springframework.data.jpa.domain.Specification;

public class PaymentCardSpecification {

    public static Specification<PaymentCard> hasHolder(String holder) {
        return (root, query, criteriaBuilder) -> {
            if (holder == null || holder.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("holder")),
                    "%" + holder.toLowerCase() + "%"
            );
        };
    }
}
