package com.soa.trello.workflow.model;
import org.springframework.data.neo4j.core.schema.Id; import org.springframework.data.neo4j.core.schema.Node; import org.springframework.data.neo4j.core.schema.Relationship; import java.util.HashSet; import java.util.Set;
@Node("TaskRef")
public class TaskRef {
  @Id public String key; // projectId:taskId
  @Relationship(type="DEPENDS_ON") public java.util.Set<TaskRef> dependsOn = new java.util.HashSet<>();
}
