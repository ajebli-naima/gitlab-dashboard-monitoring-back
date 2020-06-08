package com.leyton.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leyton.backend.dto.ApplicationDTO;
import com.leyton.backend.entities.Application;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ApplicationService {

    List<ApplicationDTO> findAllApplications();

    Application findApplication(Long idApplication);

    Application saveAppplication(String ap, MultipartFile file) throws IOException;

}
