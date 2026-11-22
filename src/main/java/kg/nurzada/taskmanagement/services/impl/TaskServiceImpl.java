package kg.nurzada.taskmanagement.services.impl;

import kg.nurzada.taskmanagement.dto.TaskRequest;
import kg.nurzada.taskmanagement.dto.TaskResponse;
import kg.nurzada.taskmanagement.entities.Task;
import kg.nurzada.taskmanagement.exceptions.TaskNotFoundException;
import kg.nurzada.taskmanagement.mappers.TaskMapper;
import kg.nurzada.taskmanagement.repositories.TaskRepository;
import kg.nurzada.taskmanagement.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse create(TaskRequest taskRequest) {
        Task task = taskMapper.toEntity(taskRequest);
        Task saved = taskRepository.save(task);
        return taskMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return taskMapper.toResponse(findOrThrow(id));
    }

    @Override
    public TaskResponse update(Long id, TaskRequest taskRequest) {
        Task existing = findOrThrow(id);
        existing.setTitle(taskRequest.title());
        existing.setDescription(taskRequest.description());
        if (taskRequest.status() != null) {
            existing.setStatus(taskRequest.status());
        }
        return taskMapper.toResponse(taskRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }

        taskRepository.deleteById(id);
    }

    private Task findOrThrow(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }
}
