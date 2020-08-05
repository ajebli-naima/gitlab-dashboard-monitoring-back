package com.leyton.backend.services;

import org.gitlab4j.api.GitLabApiException;

public interface CronService {

    void startCron(boolean initialisation) throws GitLabApiException;
}
