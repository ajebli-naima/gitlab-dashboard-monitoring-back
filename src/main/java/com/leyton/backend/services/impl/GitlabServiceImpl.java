package com.leyton.backend.services.impl;

import com.leyton.backend.dto.CommitPerUserDTO;
import com.leyton.backend.dto.FilterRequest;
import com.leyton.backend.dto.StaticticByUnitDto;
import com.leyton.backend.entities.Application;
import com.leyton.backend.services.ApplicationService;
import com.leyton.backend.services.GitlabService;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Event;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class GitlabServiceImpl implements GitlabService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitlabServiceImpl.class);

    @Autowired
    ApplicationService applicationService;
    GitLabApi gitLabApi;

    @Override
    public List<Project> findAllProjects(int idProject) throws GitLabApiException {
        this.authentificationGitlab();

        Application application = applicationService.findApplication(Long.valueOf(idProject));
        String path = application.getUrlGitlab().split("http://gitlab.leyton.fr/")[1];
        List<Project> projects = gitLabApi.getGroupApi().getProjects(path);
        if (projects.isEmpty()) {
            projects = Arrays.asList(gitLabApi.getProjectApi().getProject(path));
        }


        List<Project> list = gitLabApi.getProjectApi().getProjects();


        List<Event> events = gitLabApi.getEventsApi().getUserEvents("fchablou",null,null,null,null, Constants.SortOrder.DESC);

        List<Commit> commitArrayList = new ArrayList<>();
        for (Project p:list) {
            List<Branch> branches = gitLabApi.getRepositoryApi().getBranches(p.getId());
            for (Branch b : branches) {
                List<Commit> commitstemp = gitLabApi.getCommitsApi().getCommits(p.getId(),
                        b.getName(),"");
                commitArrayList.addAll(commitstemp);
            }
        }
        return projects;

    }

    @Override
    public List<Branch> findAllBranchsPerProjectPath(int idProject) throws GitLabApiException {
        Application application = applicationService.findApplication(Long.valueOf(idProject));
        String path = application.getUrlGitlab().split("http://gitlab.leyton.fr/")[1];
        this.authentificationGitlab();
        List<Branch> branches = gitLabApi.getRepositoryApi().getBranches(path);
        return branches;
    }

    @Override
    public List<Branch> findAllBranchsPerProjectId(int idProject) throws GitLabApiException {
        this.authentificationGitlab();
        List<Branch> branches = gitLabApi.getRepositoryApi().getBranches(idProject);
        return branches;
    }

    @Override
    public StaticticByUnitDto findAllCommitsPerProjectByUnit(FilterRequest filterRequest) throws ParseException, GitLabApiException {
        this.authentificationGitlab();

        List<Branch> branches = gitLabApi.getRepositoryApi().getBranches(filterRequest.getIdProject());

        List<Commit> commitArrayList = new ArrayList<>();
        for (Branch b : branches) {
            List<Commit> commitstemp = gitLabApi.getCommitsApi().getCommits(filterRequest.getIdProject(),
                    b.getName(), filterRequest.getDateFrom(), filterRequest.getDateTo());
            commitArrayList.addAll(commitstemp);
        }

        //  Map<String, List<Commit>> stringListMap =
        //        commits.stream().collect(Collectors.groupingBy(w -> w.getId()));

        //List<Commit> commits = gitLabApi.getCommitsApi().
        //      getCommits(filterRequest.getIdProject(),
        //            filterRequest.getIdBranch(), filterRequest.getDateFrom(), filterRequest.getDateTo());

        Set<String> nameSet = new HashSet<>();
        List<Commit> commits = commitArrayList.stream()
                .filter(e -> nameSet.add(e.getId()))
                .collect(Collectors.toList());

        Map<String, Map<Date, Integer>> map = new HashMap<>();
        String unit = "";

        if (!commits.isEmpty()) {
            Commit lastCommit = commits.get(0);
            Commit firstCommit = commits.get(commits.size() - 1);

            if (filterRequest.getDateFrom() == null) {
                String date = "01/01/1970";
                Date since = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                filterRequest.setDateFrom(since);
            }

            if (filterRequest.getDateTo() == null) {
                filterRequest.setDateTo(new Date());
            }

            long diff = lastCommit.getCommittedDate().getTime() - firstCommit.getCommittedDate().getTime();
            long daysDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            LOGGER.info("Days diff : " + daysDiff);

            DateFormat dateFormat;

            if (30 > daysDiff) {
                dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                unit = "day";
            } else if (366 > daysDiff) {
                dateFormat = new SimpleDateFormat("MM-yyyy");
                unit = "month";
            } else {
                dateFormat = new SimpleDateFormat("yyyy");
                unit = "year";
            }

            Map<String, List<Commit>> commitPerUser =
                    commits.stream().collect(Collectors.groupingBy(w -> w.getCommitterEmail().split("@")[0].toLowerCase()));


            for (Map.Entry<String, List<Commit>> entry : commitPerUser.entrySet()) {
                User user = gitLabApi.getUserApi().getUser(entry.getKey());

                List<Commit> commitList = entry.getValue();

                Map<String, List<Commit>> stringListMap =
                        commitList.stream().collect(Collectors.groupingBy(w -> dateFormat.format(w.getCommittedDate())));

                Map<Date, Integer> dateIntegerMap =
                        stringListMap.entrySet().stream().collect(Collectors.toMap(
                                item -> {
                                    try {
                                        return dateFormat.parse(item.getKey());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                },
                                item -> item.getValue().size()));

                if (user != null) {
                    map.put(user.getName(), new TreeMap<>(dateIntegerMap));
                } else {
                    map.put(entry.getValue().get(0).getCommitterName(), new TreeMap<>(dateIntegerMap));
                }

                //System.out.println("Key = " + entry.getKey() +
                //      ", Value = " + entry.getValue());
            }
        }


        StaticticByUnitDto staticticByUnitDto = new StaticticByUnitDto();
        staticticByUnitDto.setCommitPerUnit(map);
        staticticByUnitDto.setUnit(unit);

        return staticticByUnitDto;
    }

    @Override
    public CommitPerUserDTO findAllCommitsPerProject(FilterRequest filterRequest) throws GitLabApiException, ParseException {
        this.authentificationGitlab();

        //Application application = applicationService.findApplication(Long.valueOf(idProject));
        //String path = application.getUrlGitlab().split("http://gitlab.leyton.fr/")[1];

        if (filterRequest.getDateFrom() == null) {
            String sDate1 = "31/12/1998";
            Date since = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
            filterRequest.setDateFrom(since);
        }

        if (filterRequest.getDateTo() == null) {
            filterRequest.setDateTo(new Date());
        }

        List<Branch> branches = gitLabApi.getRepositoryApi().getBranches(filterRequest.getIdProject());

        List<Commit> commitArrayList = new ArrayList<>();
        for (Branch b : branches) {
            List<Commit> commitstemp = gitLabApi.getCommitsApi().getCommits(filterRequest.getIdProject(),
                    b.getName(), filterRequest.getDateFrom(), filterRequest.getDateTo());
            commitArrayList.addAll(commitstemp);
        }
        Set<String> nameSet = new HashSet<>();
        List<Commit> commits = commitArrayList.stream()
                .filter(e -> nameSet.add(e.getId()))
                .collect(Collectors.toList());

        Map<String, List<Commit>> commitPerUser =
                commits.stream().collect(Collectors.groupingBy(w -> w.getCommitterEmail().split("@")[0].toLowerCase()));

        // Map<String, Integer> commitPerUserSize =
        //       commitPerUser.entrySet().stream().collect(Collectors.toMap(
        //             entry -> entry.getKey(),
        //           entry -> entry.getValue().size()));


        Map<String, Integer> commitPerUserSize = new HashMap<>();
        for (Map.Entry<String, List<Commit>> entry : commitPerUser.entrySet()) {
            User user = gitLabApi.getUserApi().getUser(entry.getKey());
            if (user != null) {
                commitPerUserSize.put(user.getName(), entry.getValue().size());
            } else {
                commitPerUserSize.put(entry.getValue().get(0).getCommitterName(), entry.getValue().size());
            }
        }

        CommitPerUserDTO commitPerUserDTO = new CommitPerUserDTO();
        commitPerUserDTO.setCommitPerUser(commitPerUserSize);
        commitPerUserDTO.setTotalCommit(commits.size());
        return commitPerUserDTO;
    }

    @Override
    public List<MergeRequest> findAllMergeRequestPerProject(int idProject) throws GitLabApiException {
        this.authentificationGitlab();

        List<MergeRequest> mergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(idProject);
        return mergeRequests;
    }


    void authentificationGitlab() throws GitLabApiException {
        gitLabApi = new GitLabApi("http://gitlab.leyton.fr/", "kyBE1mcB3mxSrse8yiqq");
    }
}
