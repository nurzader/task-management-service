package kg.nurzada.taskmanagement.rest_client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiService {
    private final RestClient restClient;

    @Value("${app.external.url}")
    private String url;

    public String fetchObjects() {
        String response = restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);

        log.info("Получен ответ от внешнего API ({}): {}", url, response);

        return response;
    }
}
