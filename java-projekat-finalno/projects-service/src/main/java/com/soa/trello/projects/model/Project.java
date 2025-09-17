package com.soa.trello.projects.model;
import org.springframework.data.annotation.Id; import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate; import java.util.HashSet; import java.util.Set;
@Document("projects")
public class Project {
  @Id public String id;
  public String name;
  public LocalDate expectedEndDate;
  public int minTeamSize; public int maxTeamSize;
  public String managerId;
  public Set<String> memberIds = new HashSet<>();
}
