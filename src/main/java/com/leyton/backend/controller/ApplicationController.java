package com.leyton.backend.controller;

import com.leyton.backend.dto.ApplicationDTO;
import com.leyton.backend.entities.Application;
import com.leyton.backend.services.ApplicationService;
import com.leyton.backend.utils.Paths;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping(Paths.APPLICATION)
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping(path = Paths.FIND_ALL)
    public List<ApplicationDTO> findAll() {
        List<ApplicationDTO> allApplications = this.applicationService.findAllApplications();
        return allApplications;
    }

    @GetMapping(path = "/{idApplication}")
    public Application findApplication(@PathVariable("idApplication") Long idApplication) {
        Application application = applicationService.findApplication(idApplication);
        application.setPhoto(null);
        return application;
    }

    @PostMapping("")
    public ResponseEntity<Void> update(@RequestParam(value = "file", required = false) MultipartFile file,
                                       @RequestParam("application") String app) throws IOException {
        applicationService.saveAppplication(app, file);
        return status(CREATED).build();
    }
}
