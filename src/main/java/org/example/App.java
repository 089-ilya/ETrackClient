package org.example;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.AppProperties;
import org.example.job.SendCurrentDataJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

@Slf4j
public class App {

    public static final AppProperties PROPERTIES;

    static {
        PROPERTIES = readProperties();
    }

    public static void main(String[] args) throws SchedulerException {
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        JobDetail jobDetail = JobBuilder.newJob(SendCurrentDataJob.class)
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 * 1/1 * ? *"))
                .startNow()
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();
    }

    @SneakyThrows
    private static AppProperties readProperties() {
        Properties appProps = new Properties();
        appProps.load(App.class.getResourceAsStream("/config.properties"));
        log.info("Properties value {}", appProps);

        return new AppProperties(appProps.getProperty("zoneId"), appProps.getProperty("host"));

    }

}
