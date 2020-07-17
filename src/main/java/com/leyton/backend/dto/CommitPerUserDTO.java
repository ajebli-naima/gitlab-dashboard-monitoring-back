package com.leyton.backend.dto;

import java.util.Map;

public class CommitPerUserDTO {
    private Map<String, Integer> commitPerUser;
    private long totalCommit;

    public CommitPerUserDTO() {
    }

    public Map<String, Integer> getCommitPerUser() {
        return commitPerUser;
    }

    public void setCommitPerUser(Map<String, Integer> commitPerUser) {
        this.commitPerUser = commitPerUser;
    }

    public long getTotalCommit() {
        return totalCommit;
    }

    public void setTotalCommit(long totalCommit) {
        this.totalCommit = totalCommit;
    }
}
