package kg.nurzada.taskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@SpringBootApplication
public class TaskManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementServiceApplication.class, args);
    }

}
