package app.config;

import app.beans.RootCA;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public RootCA getRootCA(){
        return new RootCA();
    }

}
