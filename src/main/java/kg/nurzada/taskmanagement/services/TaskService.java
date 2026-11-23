package kg.nurzada.taskmanagement.services;

import kg.nurzada.taskmanagement.dto.TaskRequest;
import kg.nurzada.taskmanagement.dto.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse create(TaskRequest taskRequest);

    List<TaskResponse> findAll();

    TaskResponse findById(Long id);

    TaskResponse update(Long id, TaskRequest taskRequest);

    void delete(Long id);
}
