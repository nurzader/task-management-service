package kg.nurzada.taskmanagement;

import kg.nurzada.taskmanagement.dto.TaskCreatedEvent;
import kg.nurzada.taskmanagement.dto.TaskRequest;
import kg.nurzada.taskmanagement.dto.TaskResponse;
import kg.nurzada.taskmanagement.entities.Task;
import kg.nurzada.taskmanagement.enums.TaskStatus;
import kg.nurzada.taskmanagement.exceptions.TaskNotFoundException;
import kg.nurzada.taskmanagement.mappers.TaskMapper;
import kg.nurzada.taskmanagement.repositories.TaskRepository;
import kg.nurzada.taskmanagement.services.EmailService;
import kg.nurzada.taskmanagement.services.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Spy
    private TaskMapper taskMapper = new TaskMapper();

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task buildTask(Long id, String title, TaskStatus status) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription("description");
        task.setStatus(status);
        task.setCreatedAt(Instant.now());
        return task;
    }

    // Создание задачи

    @Test
    void create_shouldSaveTaskAndReturnResponse() {
        TaskRequest taskRequest = new TaskRequest("Новая задача", "описание", TaskStatus.IN_PROGRESS);

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> {
                    Task task = invocation.getArgument(0);
                    task.setId(1L);
                    return task;
                });

        TaskResponse result = taskService.create(taskRequest);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Новая задача");
        assertThat(result.status()).isEqualTo(TaskStatus.IN_PROGRESS);
        verify(taskRepository).save(any(Task.class));
        verify(eventPublisher).publishEvent(any(TaskCreatedEvent.class));
    }

    @Test
    void create_withoutStatus_shouldUseDefaultTodo() {
        TaskRequest request = new TaskRequest("Задача", null, null);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse result = taskService.create(request);

        assertThat(result.status()).isEqualTo(TaskStatus.TODO);
    }

    // Получение списка всех задач

    @Test
    void findAll_shouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(
                buildTask(1L, "A", TaskStatus.TODO),
                buildTask(2L, "B", TaskStatus.DONE)
        ));

        List<TaskResponse> result = taskService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TaskResponse::title).containsExactly("A", "B");
    }

    @Test
    void findAll_whenEmpty_shouldReturnEmptyList() {
        when(taskRepository.findAll()).thenReturn(List.of());

        assertThat(taskService.findAll()).isEmpty();
    }

    // Получение задачи по ID

    @Test
    void findById_whenExists_shouldReturnTask() {
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(buildTask(1L, "A", TaskStatus.TODO)));

        TaskResponse result = taskService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("A");
    }

    @Test
    void findById_whenNotFound_shouldThrow() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");
    }

    // Обновление задачи

    @Test
    void update_shouldChangeFieldsAndSave() {
        Task existing = buildTask(1L, "Старое", TaskStatus.TODO);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse result = taskService.update(1L, new TaskRequest("Новое", "new desc", TaskStatus.DONE));

        assertThat(result.title()).isEqualTo("Новое");
        assertThat(result.description()).isEqualTo("new desc");
        assertThat(result.status()).isEqualTo(TaskStatus.DONE);
        verify(taskRepository).save(existing);
    }

    @Test
    void update_withNullStatus_shouldKeepOldStatus() {
        Task existing = buildTask(1L, "Старое", TaskStatus.IN_PROGRESS);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse result = taskService.update(1L, new TaskRequest("Новое", null, null));

        assertThat(result.status()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void update_whenNotFound_shouldThrowAndNotSave() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(99L, new TaskRequest("X", null, null)))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository, never()).save(any());
    }

    // Удаление задачи
    @Test
    void delete_whenExists_shouldDelete() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.delete(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotFound_shouldThrowAndNotDelete() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.delete(99L))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository, never()).deleteById(any());
    }
}
