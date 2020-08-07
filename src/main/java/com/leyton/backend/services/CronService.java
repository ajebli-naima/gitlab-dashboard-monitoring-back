package com.leyton.backend.services;

import org.gitlab4j.api.GitLabApiException;

import java.text.ParseException;

public interface CronService {

    void startCron() throws GitLabApiException, ParseException;
}
