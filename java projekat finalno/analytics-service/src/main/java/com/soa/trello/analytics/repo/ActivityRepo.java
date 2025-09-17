package com.soa.trello.analytics.repo;
import com.soa.trello.analytics.model.Activity; import org.springframework.data.mongodb.repository.MongoRepository;
public interface ActivityRepo extends MongoRepository<Activity, String> {
  java.util.List<Activity> findByProjectIdOrderByTsDesc(String projectId);
}
