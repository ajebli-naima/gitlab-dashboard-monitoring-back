package com.leyton.backend.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyton.backend.dto.ApplicationDTO;
import com.leyton.backend.entities.Application;
import com.leyton.backend.mappers.ApplicationMapper;
import com.leyton.backend.repositories.ApplicationRepository;
import com.leyton.backend.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    ApplicationMapper applicationMapper;

    @Override
    public List<ApplicationDTO> findAllApplications() {
        List<Application> applications = applicationRepository.findAll();

        return applicationMapper.toApplicationDTOs(applications);
    }

    @Override
    public Application findApplication(Long idApplication) {
        return applicationRepository.findById(idApplication).get();
    }

    @Override
    public Application saveAppplication(String app, MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ApplicationDTO applicationDTO = mapper.readValue(app, ApplicationDTO.class);

        if (file != null) {
            applicationDTO.setPhoto(file.getBytes());
        } else if (applicationDTO.getId() != null){
            Optional<Application> appOrgin = applicationRepository.findById(applicationDTO.getId());
            appOrgin.ifPresent(application -> applicationDTO.setPhoto(application.getPhoto()));
        }

        Application application = applicationMapper.toApplication(applicationDTO);
        return applicationRepository.save(application);
    }
}
