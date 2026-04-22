package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.modlist.dto.PluginDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {ModMapper.class})
public interface PluginMapper {
    Plugin toEntity(PluginDto pluginDto);

    PluginDto toPluginDto(Plugin plugin);
}