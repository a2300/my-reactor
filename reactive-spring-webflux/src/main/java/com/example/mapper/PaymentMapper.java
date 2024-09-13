package com.example.mapper;

import com.example.db.model.Payment;
import com.example.dto.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface PaymentMapper {

    PaymentDto mapToDto(Payment src);


    @Mapping(target = "withUser", ignore = true)
    Payment mapToDb(PaymentDto src);
}
