package com.grits.userservice.mapper;

import com.grits.userservice.entity.PaymentCard;
import com.grits.userservice.model.request.paymentcard.CreateCardRequest;
import com.grits.userservice.model.request.paymentcard.UpdateCardRequest;
import com.grits.userservice.model.response.paymentcard.PaymentCardResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    PaymentCard toEntity(CreateCardRequest request);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateCardRequest request, @MappingTarget PaymentCard card);

    @Mapping(target = "userId", source = "user.id")
    PaymentCardResponse toResponse(PaymentCard card);
}
