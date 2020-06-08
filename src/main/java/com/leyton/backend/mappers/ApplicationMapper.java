package com.leyton.backend.mappers;

import com.leyton.backend.dto.ApplicationDTO;
import com.leyton.backend.entities.Application;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    ApplicationDTO toApplicationDTO(Application application);

    List<ApplicationDTO> toApplicationDTOs(List<Application> applications);

    Application toApplication(ApplicationDTO applicationDTO);

}