package app.task;

import app.beans.CertificateAuthority;
import app.exception.InvalidDataException;
import app.service.CAService;
import app.service.CRLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskManager {

    @Autowired
    private CRLService crlService;

    @Autowired
    private CAService caService;

    private List<TaskHolder> taskHolders;

    public TaskManager() {
        this.taskHolders = new ArrayList<>();
    }

    private TaskHolder getSchedule(CertificateAuthority ca){
        for (TaskHolder t : taskHolders)
            if (t.getCa().equals(ca))
                return t;
        return null;
    }

    private boolean hasScheduleFor(CertificateAuthority ca){
        return getSchedule(ca) != null;
    }


    public void addToSchedule(CertificateAuthority ca){
        if (!hasScheduleFor(ca)) {
            TaskHolder taskHolder = new TaskHolder(ca, crlService, caService);
            taskHolders.add(taskHolder);
            taskHolder.init();
        }
    }

    public void addListToSchedule(List<CertificateAuthority> cas){
        for (CertificateAuthority ca : cas)
            addToSchedule(ca);
    }


    public void removeFromSchedule(CertificateAuthority ca){
        TaskHolder taskHolder = getSchedule(ca);
        if (taskHolder != null){
            taskHolder.cancelExecution();
            taskHolders.remove(taskHolder);
        }
    }

    public void reschedule(CertificateAuthority ca, String cronExp, String frequencyDescription) throws InvalidDataException {
        TaskHolder th = getSchedule(ca);
        th.rescheduleExecution(cronExp, frequencyDescription);
    }

    public void rescheduleAll(String cronExp, String frequencyDescription) throws InvalidDataException {
        for (TaskHolder th : taskHolders)
            th.rescheduleExecution(cronExp, frequencyDescription);
    }

}
