package com.leyton.backend.services.impl;

import com.leyton.backend.entities.LastCron;
import com.leyton.backend.mappers.CommitMapper;
import com.leyton.backend.mappers.MemberMapper;
import com.leyton.backend.mappers.ProjectMapper;
import com.leyton.backend.repositories.CommitRepository;
import com.leyton.backend.repositories.LastCronRepository;
import com.leyton.backend.repositories.MemberRepository;
import com.leyton.backend.repositories.ProjectRepository;
import com.leyton.backend.services.CronService;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    LastCronRepository lastCronRepository;
    @Autowired
    CommitMapper commitMapper;
    @Autowired
    ProjectMapper projectMapper;
    @Autowired
    MemberMapper memberMapper;
    GitLabApi gitLabApi;

    @Override
    public void startCron() throws GitLabApiException, ParseException {

        this.authentificationGitlab();

        Date lastSynch;
        Date currentDate = new Date();
        LastCron lastCron;
        List<LastCron> lastCrons = lastCronRepository.findAll();
        if (!lastCrons.isEmpty()) {
            lastCron = lastCrons.get(0);
            lastSynch = lastCron.getLastSynch();
            lastCron.setLastSynch(currentDate);
        } else {
            String date = "10/11/1997";
            lastSynch = new SimpleDateFormat("dd/MM/yyyy").parse(date);
            lastCron = new LastCron();
            lastCron.setLastSynch(currentDate);
        }
        lastCronRepository.save(lastCron);

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
                memberRepository.save(memberList.get(i));
            } catch (Exception e) {
                LOGGER.info("Member already exist");
            }
        }

        //saving commits List
        List<com.leyton.backend.entities.Commit> commitArrayListDto = new ArrayList<>();
        for (int i = 0; i < projectList.size(); i++) {
            List<Commit> commits = new ArrayList<>();

            LOGGER.info("Scaning project name {} : id {} : ", projectList.get(i).getName(), projectList.get(i).getId());
            List<Branch> branches = gitLabApi.getRepositoryApi().getBranches(projectList.get(i).getId());
            for (Branch b : branches) {
                LOGGER.info("Scaning branch :" + b.getName());
                try {
                    commits.addAll(gitLabApi.getCommitsApi().getCommits(projectList.get(i).getId(),
                            b.getName(), lastSynch, currentDate));
                } catch (Exception e) {
                    LOGGER.error("error in branch" + b.getName());
                }
            }
            List<com.leyton.backend.entities.Commit> commitList = new ArrayList<>();
            for (Commit commit : commits) {
                // Optional<MergeRequest> optional = isMergeRequest(mergeRequests, commit.getId());
                if (!commit.getMessage().contains("Merge branch") || !commit.getMessage().contains("into")
                        || !commit.getMessage().contains("See merge request")) {
                    com.leyton.backend.entities.Commit commitDto = commitMapper.commitToCommitDTO(commit);
                    commitDto.setIdProject(projectList.get(i).getId());
                    commitList.add(commitDto);
                } else {
                    LOGGER.info("Commit id {} is merge request in project {}", commit.getId(), projectList.get(i).getName());
                    LOGGER.info("commit message {}", commit.getMessage());
                }
            }
            commitArrayListDto.addAll(commitList);
        }

        Set<String> nameSet = new HashSet<>();
        List<com.leyton.backend.entities.Commit> commitList = commitArrayListDto.stream()
                .filter(e -> nameSet.add(e.getIdCommit()))
                .collect(Collectors.toList());

        commitRepository.saveAll(commitList);
    }

    public Optional<MergeRequest> isMergeRequest(final List<MergeRequest> list, final String commitId) {
        try {
            return list.stream().filter(o -> o.getMergeCommitSha().equals(commitId)).findFirst();
        } catch (Exception e) {
            LOGGER.error("null in id commit of merge request");
            return null;
        }
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    void authentificationGitlab() throws GitLabApiException {
        gitLabApi = new GitLabApi("http://172.16.0.112/", "kyBE1mcB3mxSrse8yiqq");
    }

}
