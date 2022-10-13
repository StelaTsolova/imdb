package com.imdb.domain.model.mapping;

import com.imdb.domain.model.dto.UserEntityRegisterDto;
import com.imdb.domain.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserEntityMapper {

    UserEntity mapUserEntityRegisterDtoToUserEntity(UserEntityRegisterDto userEntityRegisterDto);
}
