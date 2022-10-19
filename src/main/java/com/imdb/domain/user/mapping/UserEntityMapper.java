package com.imdb.domain.user.mapping;

import com.imdb.domain.user.model.dto.UserEntityRegisterDTO;
import com.imdb.domain.user.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserEntityMapper {

    User mapUserEntityRegisterDtoToUserEntity(UserEntityRegisterDTO userEntityRegisterDto);
}
