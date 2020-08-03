package com.leyton.backend.controller;


import com.leyton.backend.dto.CommitPerUserDTO;
import com.leyton.backend.dto.FilterRequest;
import com.leyton.backend.dto.StaticticByUnitDto;
import com.leyton.backend.services.GitlabService;
import com.leyton.backend.utils.Paths;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(Paths.GITLAB)
public class GitlabController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitlabController.class);

    private final GitlabService gitlabService;

    public GitlabController(GitlabService gitlabService) {
        this.gitlabService = gitlabService;
    }


    @GetMapping(path = "/projects/{idProject}")
    public List<Project> findAllProjects(@PathVariable("idProject") int idProject)
            throws GitLabApiException {
        List<Project> projects = gitlabService.findAllProjects(idProject);
        return projects;
    }

    @GetMapping(path = "/branchs/{idProject}")
    public List<Branch> findAllBranchsPerProjectPath(@PathVariable("idProject") int idProject)
            throws GitLabApiException {
        List<Branch> branches = gitlabService.findAllBranchsPerProjectPath(idProject);
        return branches;
    }

    @GetMapping(path = "/branchs/path/{idProject}")
    public List<Branch> findAllBranchsPerProjectId(@PathVariable("idProject") int idProject)
            throws GitLabApiException {
        List<Branch> branches = gitlabService.findAllBranchsPerProjectId(idProject);
        return branches;
    }


    @PostMapping(path = "/commits/unit")
    public StaticticByUnitDto findAllCommitsPerProjectByUnit(@RequestBody FilterRequest filterRequest)
            throws GitLabApiException, ParseException {
        StaticticByUnitDto staticticByUnitDto = gitlabService.findAllCommitsPerProjectByUnit(filterRequest);
        return staticticByUnitDto;
    }

    @PostMapping(path = "/commits")
    public CommitPerUserDTO findAllCommitsPerProject(@RequestBody FilterRequest filterRequest)
            throws GitLabApiException, ParseException {
        CommitPerUserDTO commitPerUserDTO = gitlabService.findAllCommitsPerProject(filterRequest);
        return commitPerUserDTO;
    }

    @GetMapping(path = "/mergeRequest/{idProject}")
    public List<MergeRequest> findAllMergeRequestPerProject(@PathVariable("idProject") int idProject)
            throws GitLabApiException {
        List<MergeRequest> mergeRequests = gitlabService.findAllMergeRequestPerProject(idProject);
        return mergeRequests;
    }
}
