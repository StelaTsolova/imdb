package com.example.imdb.model.mapping;

import com.example.imdb.model.dto.ActorDto;
import com.example.imdb.model.entity.Actor;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActorMapper {

    Actor mapActorDtoToActor(ActorDto actorDto);
}
