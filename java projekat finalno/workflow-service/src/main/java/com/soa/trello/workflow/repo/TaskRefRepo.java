package com.soa.trello.workflow.repo;
import com.soa.trello.workflow.model.TaskRef; import org.springframework.data.neo4j.repository.Neo4jRepository;
public interface TaskRefRepo extends Neo4jRepository<TaskRef, String> { }
