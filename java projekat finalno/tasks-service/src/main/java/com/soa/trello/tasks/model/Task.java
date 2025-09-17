package com.soa.trello.tasks.model;
import org.springframework.data.annotation.Id; import org.springframework.data.mongodb.core.mapping.Document;
@Document("tasks")
public class Task {
  @Id public String id;
  public String projectId; public String title; public String description; public String assigneeId;
  public Status status = Status.PENDING;
  public enum Status { PENDING, IN_PROGRESS, DONE }
}
