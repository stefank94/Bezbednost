package app.task;

import app.beans.CertificateAuthority;
import app.exception.InvalidDataException;
import app.service.CRLService;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

public class TaskHolder {

    @Autowired
    private CRLService crlService;

    private CertificateAuthority ca;

    private IssueCRLTask issueCRLTask;

    private TaskScheduler taskScheduler;

    private ScheduledFuture scheduledFuture;

    private Date latestExecutionDate;

    private Date nextExecutionDate;

    private String cronExpression;

    public TaskHolder(CertificateAuthority ca){
        this.ca = ca;
        this.issueCRLTask = new IssueCRLTask();
        this.taskScheduler = new ThreadPoolTaskScheduler();
    }

    private class IssueCRLTask implements Runnable {

        @Override
        public void run() {
            System.out.println("Issuing CRL for CA with id: " + ca.getId());
            Date next = scheduleNextExecution();
            nextExecutionDate = next;
            latestExecutionDate = crlService.issueCRL(ca, next);
        }

    }

    public void init(){

    }

    public void executeNow(){
        issueCRLTask.run();
    }

    public void scheduleExecution(String cronExp) throws InvalidDataException {
        try {
            CronExpression cron = new CronExpression(cronExp);
            cronExpression = cronExp;
            Date calculateFrom = latestExecutionDate != null ? latestExecutionDate : new Date();
            Date next = cron.getNextValidTimeAfter(calculateFrom);
            if (nextExecutionDate != null && next.before(nextExecutionDate)){
                executeNow();
            } else {
                // reschedule
                nextExecutionDate = next;
                scheduledFuture = taskScheduler.schedule(issueCRLTask, nextExecutionDate);
            }
        } catch (ParseException e) {
            throw new InvalidDataException("Cron expression invalid.");
        }
    }

    private Date scheduleNextExecution(){
        try {
            CronExpression cron = new CronExpression(cronExpression);
            Date next = cron.getNextValidTimeAfter(new Date());
            scheduledFuture = taskScheduler.schedule(issueCRLTask, next);
            return next;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cancelExecution(){
        if (scheduledFuture != null)
            scheduledFuture.cancel(false);
    }

    public CRLService getCrlService() {
        return crlService;
    }

    public void setCrlService(CRLService crlService) {
        this.crlService = crlService;
    }

    public CertificateAuthority getCa() {
        return ca;
    }

    public void setCa(CertificateAuthority ca) {
        this.ca = ca;
    }

    public IssueCRLTask getIssueCRLTask() {
        return issueCRLTask;
    }

    public void setIssueCRLTask(IssueCRLTask issueCRLTask) {
        this.issueCRLTask = issueCRLTask;
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public ScheduledFuture getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public Date getLastExecutionDate() {
        return latestExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.latestExecutionDate = lastExecutionDate;
    }
}
