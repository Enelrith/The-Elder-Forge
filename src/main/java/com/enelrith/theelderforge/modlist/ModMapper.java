package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.modlist.dto.ModDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {CategoryMapper.class})
public interface ModMapper {
    Mod toEntity(ModDto modDto);

    ModDto toModDto(Mod mod);
}