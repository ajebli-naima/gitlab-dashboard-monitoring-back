package com.leyton.backend.services.impl;

import com.leyton.backend.mappers.CommitMapper;
import com.leyton.backend.mappers.MemberMapper;
import com.leyton.backend.mappers.ProjectMapper;
import com.leyton.backend.repositories.CommitRepository;
import com.leyton.backend.repositories.MemberRepository;
import com.leyton.backend.repositories.ProjectRepository;
import com.leyton.backend.services.CronService;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CronServiceImpl implements CronService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CronServiceImpl.class);


    @Autowired
    CommitRepository commitRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CommitMapper commitMapper;
    @Autowired
    ProjectMapper projectMapper;
    @Autowired
    MemberMapper memberMapper;
    GitLabApi gitLabApi;

    @Override
    public void startCron(boolean initialisation) throws GitLabApiException {

        this.authentificationGitlab();

        //saving Projects list
        List<Project> projectList = gitLabApi.getProjectApi().getProjects();
        List<com.leyton.backend.entities.Project> toProjectDTOs = projectMapper.toProjectDTOs(projectList);

        LOGGER.info("Projects size:", projectList.size());


        for (int i = 0; i < toProjectDTOs.size(); i++) {
            try {
                projectRepository.save(toProjectDTOs.get(i));
            } catch (Exception e) {
                LOGGER.info("Project already exist");
            }
        }

        //saving memebers List
        List<User> userList = gitLabApi.getUserApi().getActiveUsers();
        List<com.leyton.backend.entities.Member> memberList = memberMapper.toMemberDTOs(userList);

        LOGGER.info("Members size:" + memberList.size());
        for (int i = 0; i < memberList.size(); i++) {
            try {
                memberRepository.save(memberList.get(0));
            } catch (Exception e) {
                LOGGER.info("Member already exist");
            }
        }

        //saving commits List
        List<com.leyton.backend.entities.Commit> commitArrayListDto = new ArrayList<>();
        for (int i = 0; i < projectList.size(); i++) {
            List<Commit> commits = new ArrayList<>();

            LOGGER.info("Scaning project :" + projectList.get(i).getName());
            List<Branch> branches = gitLabApi.getRepositoryApi().getBranches(projectList.get(i).getId());
            for (Branch b : branches) {
                LOGGER.info("Scaning branch :" + b.getName());
                try {
                    if (initialisation) {
                        commits.addAll(gitLabApi.getCommitsApi().getCommits(projectList.get(i).getId(),
                                b.getName(), ""));
                    } else {
                        commits.addAll(gitLabApi.getCommitsApi().getCommits(projectList.get(i).getId(),
                                b.getName(), yesterday(), new Date()));
                    }
                } catch (Exception e) {
                    LOGGER.error("error in branch" + b.getName());
                }
            }
            List<com.leyton.backend.entities.Commit> commitList = new ArrayList<>();
            for (Commit commit : commits) {
                com.leyton.backend.entities.Commit commitDto = commitMapper.commitToCommitDTO(commit);
                commitDto.setIdProject(projectList.get(i).getId());
                commitList.add(commitDto);
            }
            commitArrayListDto.addAll(commitList);
        }

        Set<String> nameSet = new HashSet<>();
        List<com.leyton.backend.entities.Commit> commitList = commitArrayListDto.stream()
                .filter(e -> nameSet.add(e.getIdCommit()))
                .collect(Collectors.toList());

        commitRepository.saveAll(commitList);
    }


    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    void authentificationGitlab() throws GitLabApiException {
        gitLabApi = new GitLabApi("http://gitlab.leyton.fr/", "kyBE1mcB3mxSrse8yiqq");
    }

}
