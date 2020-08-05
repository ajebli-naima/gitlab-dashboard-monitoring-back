package com.leyton.backend.mappers;

import com.leyton.backend.entities.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mappings({
            @Mapping(target = "idMember", source = "entity.id"),
             @Mapping(target = "email", source = "entity.email"),

            @Mapping(target = "id", ignore = true)
    })
    Member memberToMemberDTO(org.gitlab4j.api.models.User entity);

    @Mappings({
            @Mapping(target = "id", source = "dto.idMember"),
    })
    org.gitlab4j.api.models.Member memberDTOtoMember(Member dto);


    default List<Member> toMemberDTOs(List<org.gitlab4j.api.models.User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        List<Member> list = new ArrayList<>(users.size());
        for (org.gitlab4j.api.models.User member : users) {
            list.add(memberToMemberDTO(member));
        }
        return list;
    }

}