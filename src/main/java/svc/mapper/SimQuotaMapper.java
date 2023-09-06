package svc.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import svc.dto.CreateQuotaRequest;
import svc.entity.SimCard;
import svc.entity.SimQuota;

@Mapper(componentModel = "spring")
public interface SimQuotaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "simCard", source = "simCard")
    @Mapping(target = "type", source = "request.type")
    @Mapping(target = "balance", source = "request.amount")
    @Mapping(target = "endDate", source = "request.endDate")
    SimQuota toSimQuota(CreateQuotaRequest request, SimCard simCard);

}
