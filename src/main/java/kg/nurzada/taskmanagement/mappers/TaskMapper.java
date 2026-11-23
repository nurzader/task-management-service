package kg.nurzada.taskmanagement.mappers;

import kg.nurzada.taskmanagement.dto.TaskRequest;
import kg.nurzada.taskmanagement.dto.TaskResponse;
import kg.nurzada.taskmanagement.entities.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequest taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        if (taskRequest.status() != null) {
            task.setStatus(taskRequest.status());
        }
        return task;
    }

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt()
        );
    }
}
