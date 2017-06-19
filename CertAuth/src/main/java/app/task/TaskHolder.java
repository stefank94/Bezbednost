package app.task;

import app.beans.CertificateAuthority;
import app.exception.InvalidDataException;
import app.service.CAService;
import app.service.CRLService;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

public class TaskHolder {

    private CRLService crlService;

    private CAService caService;

    private CertificateAuthority ca;

    private IssueCRLTask issueCRLTask;

    private ThreadPoolTaskScheduler taskScheduler;

    private ScheduledFuture scheduledFuture;

    private Date latestExecutionDate;

    private Date nextExecutionDate;

    private String cronExpression;

    public TaskHolder(CertificateAuthority ca, CRLService crlService, CAService caService){
        this.ca = ca;
        this.crlService = crlService;
        this.caService = caService;
        this.issueCRLTask = new IssueCRLTask();
        this.taskScheduler = new ThreadPoolTaskScheduler();
        this.taskScheduler.setPoolSize(10);
        this.taskScheduler.initialize();
    }

    private class IssueCRLTask implements Runnable {

        @Override
        public void run() {
            System.out.println("######### Issuing CRL number " + (ca.getCrlInformation().getCrlNumber() + 1) + " for CA with id: " + ca.getId() + "########");
            scheduleNextExecution();
            latestExecutionDate = crlService.issueCRL(ca.getId(), nextExecutionDate);
        }

    }

    public void init(){
        latestExecutionDate = ca.getCrlInformation().getCurrentIssued();
        nextExecutionDate = ca.getCrlInformation().getNextIssued();
        cronExpression = ca.getCrlInformation().getCronExpression();
        if (nextExecutionDate == null || new Date().after(nextExecutionDate))
            executeNow();
        else
            scheduleNextExecutionDate(nextExecutionDate);
    }

    public void executeNow(){
        issueCRLTask.run();
    }

    public void rescheduleExecution(String cronExp, String frequencyDescription) throws InvalidDataException {
        try {
            CronExpression cron = new CronExpression(cronExp);
            cronExpression = cronExp;
            ca.getCrlInformation().setFrequencyDescription(frequencyDescription);
            Date next = cron.getNextValidTimeAfter(latestExecutionDate);
            if (next.before(new Date())){
                executeNow();
            } else if (next.before(nextExecutionDate)){
                scheduleNextExecutionDate(next);
            } else {
                saveCA();
                // Do nothing. (must execute by previous execution time; change frequency after that.)
            }
        } catch (ParseException e){
            throw new InvalidDataException("Cron expression invalid.");
        }
    }

    public void cancelExecution(){
        if (scheduledFuture != null)
            scheduledFuture.cancel(false);
    }

    private void scheduleNextExecution(){
        try {
            CronExpression cron = new CronExpression(cronExpression);
            Date next = cron.getNextValidTimeAfter(new Date());
            scheduleNextExecutionDate(next);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void scheduleNextExecutionDate(Date date){
        scheduledFuture = taskScheduler.schedule(issueCRLTask, date);
        nextExecutionDate = date;
        saveCA();
    }

    private void saveCA(){
        ca.getCrlInformation().setCronExpression(cronExpression);
        ca.getCrlInformation().setNextIssued(nextExecutionDate);
        ca.getCrlInformation().setCurrentIssued(latestExecutionDate);
        caService.save(ca);
    }

    public CertificateAuthority getCa() {
        return ca;
    }
}
