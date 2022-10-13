package com.imdb.domain.model.mapping;

import com.imdb.domain.model.dto.ActorDto;
import com.imdb.domain.model.entity.Actor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActorMapper {

    Actor mapActorDtoToActor(ActorDto actorDto);
}
