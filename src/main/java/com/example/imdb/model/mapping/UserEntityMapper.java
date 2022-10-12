package com.example.imdb.model.mapping;

import com.example.imdb.model.dto.UserEntityRegisterDto;
import com.example.imdb.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserEntityMapper {

    UserEntity mapUserEntityRegisterDtoToUserEntity(UserEntityRegisterDto userEntityRegisterDto);
}
