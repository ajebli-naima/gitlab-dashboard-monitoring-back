package com.leyton.backend.dto;

public class ApplicationDTO {
    private Long id;
    private String nameApplication;
    private String urlGitlab;
    private String urlJenkins;
    private String urlSonar;
    private String urlGrafana;
    private String urlGraylog;
    private String urlDeployment;
    private byte[] photo;

    public ApplicationDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameApplication() {
        return nameApplication;
    }

    public void setNameApplication(String nameApplication) {
        this.nameApplication = nameApplication;
    }

    public String getUrlGitlab() {
        return urlGitlab;
    }

    public void setUrlGitlab(String urlGitlab) {
        this.urlGitlab = urlGitlab;
    }

    public String getUrlJenkins() {
        return urlJenkins;
    }

    public void setUrlJenkins(String urlJenkins) {
        this.urlJenkins = urlJenkins;
    }

    public String getUrlSonar() {
        return urlSonar;
    }

    public void setUrlSonar(String urlSonar) {
        this.urlSonar = urlSonar;
    }

    public String getUrlGrafana() {
        return urlGrafana;
    }

    public void setUrlGrafana(String urlGrafana) {
        this.urlGrafana = urlGrafana;
    }

    public String getUrlGraylog() {
        return urlGraylog;
    }

    public void setUrlGraylog(String urlGraylog) {
        this.urlGraylog = urlGraylog;
    }

    public String getUrlDeployment() {
        return urlDeployment;
    }

    public void setUrlDeployment(String urlDeployment) {
        this.urlDeployment = urlDeployment;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
