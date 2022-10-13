package com.imdb.model.mapping;

import com.imdb.model.dto.ActorDto;
import com.imdb.model.entity.Actor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActorMapper {

    Actor mapActorDtoToActor(ActorDto actorDto);
}
