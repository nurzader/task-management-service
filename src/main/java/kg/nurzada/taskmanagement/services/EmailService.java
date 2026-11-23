package kg.nurzada.taskmanagement.services;

import kg.nurzada.taskmanagement.dto.TaskCreatedEvent;
import kg.nurzada.taskmanagement.dto.TaskResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.mail.to}")
    private String to;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTaskCreated(TaskCreatedEvent event) {
        TaskResponse task = event.task();
        if (to == null || to.isBlank()) {
            log.warn("app.mail.to не задан, письмо о задаче id={} не отправлено", task.id());
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("Создана задача: " + task.title());
            message.setText("""
                    ID: %d
                    Название: %s
                    Описание: %s
                    Статус: %s
                    Создана: %s
                    """.formatted(task.id(), task.title(), task.description(),
                    task.status(), task.createdAt()));
            mailSender.send(message);
            log.info("Письмо о задаче id={} отправлено на {}", task.id(), to);
        } catch (Exception e) {
            log.error("Не удалось отправить письмо о задаче id={}", task.id(), e);
        }
    }
}
