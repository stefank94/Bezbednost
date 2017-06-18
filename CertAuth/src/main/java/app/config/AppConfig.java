package app.config;

import app.task.TaskManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
public class AppConfig {

    @Bean
    public TaskManager getTaskManager(){
        return new TaskManager();
    }

}
