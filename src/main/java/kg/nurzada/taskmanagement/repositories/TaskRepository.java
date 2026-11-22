package kg.nurzada.taskmanagement.repositories;

import kg.nurzada.taskmanagement.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
