package com.leyton.backend;

import com.leyton.backend.services.CronService;
import org.gitlab4j.api.GitLabApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class DevopsDashboardApplication {


    @Value("${cron.initialisation}")
    private boolean initialisation;

    private static final Logger LOGGER = LoggerFactory.getLogger(DevopsDashboardApplication.class);


    @Autowired
    private CronService cronService;


    public static void main(String[] args) {
        SpringApplication.run(DevopsDashboardApplication.class, args);
    }


    // @Scheduled(cron = "${cron.expression}")
    public void scheduleDynamicTaskWithCronExpression() throws GitLabApiException {

        this.cronService.startCron(false);
    }

    //@Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void cron() {
        System.out.println("eeee");
    }


    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            if (initialisation) {
                this.cronService.startCron(false);
            }
        };
    }


}


