package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.modlist.dto.AddModlistRequest;
import com.enelrith.theelderforge.modlist.dto.ModlistDto;
import com.enelrith.theelderforge.modlist.dto.UpdateModlistRequest;
import com.enelrith.theelderforge.user.UserMapper;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class, ModMapper.class, PluginMapper.class})
public interface ModlistMapper {
    Modlist toEntity(AddModlistRequest addModlistRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Modlist partialUpdate(AddModlistRequest addModlistRequest, @MappingTarget Modlist modlist);

    Modlist toEntity(ModlistDto modlistDto);

    ModlistDto toModlistDto(Modlist modlist);

    Modlist toEntity(UpdateModlistRequest updateModlistRequest);

    UpdateModlistRequest toDto(Modlist modlist);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Modlist partialUpdate(UpdateModlistRequest updateModlistRequest, @MappingTarget Modlist modlist);
}