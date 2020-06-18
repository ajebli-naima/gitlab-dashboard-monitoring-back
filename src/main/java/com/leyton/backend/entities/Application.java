package com.leyton.backend.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nameApplication;
    @Column(length = 500)
    private String urlGitlab;
    @Column(length = 500)
    private String urlJenkins;
    @Column(length = 500)
    private String urlSonar;
    @Column(length = 500)
    private String urlGrafana;
    @Column(length = 500)
    private String urlGraylog;
    @Column(length = 500)
    private String urlDeployment;
    @Lob
    private byte[] photo;

    public Application() {
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
