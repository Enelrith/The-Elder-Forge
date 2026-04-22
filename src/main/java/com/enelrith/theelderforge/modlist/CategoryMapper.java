package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.modlist.dto.CategoryDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    Category toEntity(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);
}