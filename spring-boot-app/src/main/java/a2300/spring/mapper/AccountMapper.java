package a2300.spring.mapper;

import a2300.spring.db.model.Account;
import a2300.spring.dto.AccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface AccountMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "moneyAmount", source = "moneyAmount")
    AccountDto mapToDto(Account src);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "moneyAmount", source = "moneyAmount")
    Account  mapToDb(AccountDto src);
}
