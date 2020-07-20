package com.leyton.backend.services;

import com.leyton.backend.dto.CommitPerUserDTO;
import com.leyton.backend.dto.FilterRequest;
import com.leyton.backend.dto.StaticticByUnitDto;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;

import java.text.ParseException;
import java.util.List;

public interface GitlabService {


    List<Project> findAllProjects(int idProject) throws GitLabApiException;

    List<Branch> findAllBranchsPerProjectPath(int idProject) throws GitLabApiException;

    List<Branch> findAllBranchsPerProjectId(int idProject) throws GitLabApiException;

    StaticticByUnitDto findAllCommitsPerProjectByUnit(FilterRequest filterRequest) throws ParseException, GitLabApiException;

    CommitPerUserDTO findAllCommitsPerProject(FilterRequest filterRequest) throws GitLabApiException, ParseException;

    List<MergeRequest> findAllMergeRequestPerProject(int idProject) throws GitLabApiException;
}
