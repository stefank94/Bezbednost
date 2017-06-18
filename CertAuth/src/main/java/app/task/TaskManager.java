package app.task;

import app.beans.CertificateAuthority;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskManager {

    private List<TaskHolder> taskHolders;

    public TaskManager() {
        this.taskHolders = new ArrayList<>();
    }

    public TaskHolder getTaskHolder(CertificateAuthority ca){
        for (TaskHolder t : taskHolders)
            if (t.getCa().equals(ca))
                return t;
        return null;
    }

    public boolean hasTaskHolderFor(CertificateAuthority ca){
        return getTaskHolder(ca) != null;
    }

    public void addTaskHolder(CertificateAuthority ca){
        if (!hasTaskHolderFor(ca)) {
            TaskHolder taskHolder = new TaskHolder(ca);
            taskHolders.add(taskHolder);
            taskHolder.scheduleExecution();
        }
    }

    public void addTaskHolders(List<CertificateAuthority> cas){
        for (CertificateAuthority ca : cas)
            addTaskHolder(ca);
    }

    public void removeTaskHolder(CertificateAuthority ca){
        TaskHolder taskHolder = getTaskHolder(ca);
        if (taskHolder != null){
            taskHolder.cancelExecution();
            taskHolders.remove(taskHolder);
        }
    }

}
