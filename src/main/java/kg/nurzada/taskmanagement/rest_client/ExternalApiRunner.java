package kg.nurzada.taskmanagement.rest_client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalApiRunner implements ApplicationRunner {

    private final ExternalApiService externalApiService;

    @Value("${app.external.fetch-on-startup:true}")
    private boolean enabled;

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }
        try {
            externalApiService.fetchObjects();
        } catch (Exception e) {
            log.warn("Ошибка при вызове внешнего API: {}", e.getMessage());
        }
    }
}
