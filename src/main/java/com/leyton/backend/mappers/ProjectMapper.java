package com.leyton.backend.mappers;

import com.leyton.backend.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {


    @Mappings({
            @Mapping(target = "idProject", source = "entity.id"),
            @Mapping(target = "id", ignore = true)
    })
    Project projectToProjectDTO(org.gitlab4j.api.models.Project entity);

    @Mappings({
            @Mapping(target = "id", source = "dto.idProject"),
    })
    org.gitlab4j.api.models.Project projectDTOtoProject(Project dto);


    default List<Project> toProjectDTOs(List<org.gitlab4j.api.models.Project> projects) {
        if (projects == null) {
            return Collections.emptyList();
        }
        List<Project> list = new ArrayList<>(projects.size());
        for (org.gitlab4j.api.models.Project project : projects) {
            list.add(projectToProjectDTO(project));
        }
        return list;
    }


}