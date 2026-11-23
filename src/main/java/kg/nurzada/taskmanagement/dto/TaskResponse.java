package kg.nurzada.taskmanagement.dto;

import kg.nurzada.taskmanagement.enums.TaskStatus;

import java.io.Serializable;
import java.time.Instant;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        Instant createdAt
) implements Serializable {
}
