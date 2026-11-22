package kg.nurzada.taskmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import kg.nurzada.taskmanagement.enums.TaskStatus;

public record TaskRequest(
        @NotBlank String title,
        String description,
        TaskStatus status
) {
}
