package com.razorpay.payment.mapper;

import com.razorpay.payment.Dto.Response.OrderResponse;
import com.razorpay.payment.entity.OrderRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    @Mapping(target = "status", source = "orderStatus")
    OrderResponse toResponse(OrderRecord orderRecord);
}
