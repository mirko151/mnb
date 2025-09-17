package com.soa.trello.tasks.repo;
import com.soa.trello.tasks.model.Task; import org.springframework.data.mongodb.repository.MongoRepository; import java.util.List;
public interface TaskRepo extends MongoRepository<Task, String> {
  java.util.List<Task> findByProjectId(String projectId);
  long countByProjectIdAndStatusNot(String projectId, Task.Status status);
  boolean existsByProjectIdAndAssigneeIdAndStatus(String projectId, String assigneeId, Task.Status status);
}
