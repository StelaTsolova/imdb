package com.imdb.domain.actor.mapping;

import com.imdb.domain.actor.model.dto.ActorDTO;
import com.imdb.domain.actor.model.entity.Actor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActorMapper {

    Actor mapActorDtoToActor(ActorDTO actorDto);
}
