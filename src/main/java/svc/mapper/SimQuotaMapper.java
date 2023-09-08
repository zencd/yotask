package svc.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import svc.dto.CreateQuotaRequest;
import svc.dto.SimQuota;
import svc.dto.SimQuotaType;
import svc.entity.SimCardEntity;
import svc.entity.SimQuotaEntity;

@Mapper(componentModel = "spring")
public interface SimQuotaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "simCard", source = "simCard")
    @Mapping(target = "type", source = "request.type")
    @Mapping(target = "balance", source = "request.amount")
    @Mapping(target = "endDate", source = "request.endDate")
    SimQuotaEntity toSimQuota(CreateQuotaRequest request, SimCardEntity simCard);

    SimQuota toSimQuotaDto(SimQuotaEntity source);

    svc.entity.SimQuotaType toEntity(SimQuotaType source);

}
