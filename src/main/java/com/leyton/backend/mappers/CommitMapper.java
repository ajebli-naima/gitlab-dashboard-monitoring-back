package com.leyton.backend.mappers;

import com.leyton.backend.entities.Commit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommitMapper {


    @Mappings({
            @Mapping(target = "idCommit", source = "entity.id"),
            @Mapping(target = "id", ignore = true)
    })
    Commit commitToCommitDTO(org.gitlab4j.api.models.Commit entity);

    @Mappings({
            @Mapping(target = "id", source = "dto.idCommit"),
    })
    org.gitlab4j.api.models.Commit commitDTOtoCommit(Commit dto);


    default List<Commit> toCommitDTOs(List<org.gitlab4j.api.models.Commit> commits) {
        if (commits == null) {
            return Collections.emptyList();
        }
        List<Commit> list = new ArrayList<>(commits.size());
        for (org.gitlab4j.api.models.Commit commit : commits) {
            list.add(commitToCommitDTO(commit));
        }
        return list;
    }


    // List<Commit> toCommitDTOs(List<org.gitlab4j.api.models.Commit> applications);


}


