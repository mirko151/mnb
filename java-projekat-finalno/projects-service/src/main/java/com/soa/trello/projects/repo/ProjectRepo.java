package com.soa.trello.projects.repo;
import com.soa.trello.projects.model.Project; import org.springframework.data.mongodb.repository.MongoRepository; import java.util.List;
public interface ProjectRepo extends MongoRepository<Project, String> {
  java.util.List<Project> findByManagerId(String managerId);
  java.util.List<Project> findByMemberIdsContains(String memberId);
}
