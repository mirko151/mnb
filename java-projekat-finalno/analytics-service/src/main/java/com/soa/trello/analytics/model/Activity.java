package com.soa.trello.analytics.model;
import org.springframework.data.annotation.Id; import org.springframework.data.mongodb.core.mapping.Document; import java.time.Instant; import java.util.Map;
@Document("activities")
public class Activity {
  @Id public String id; public String projectId; public String userId; public String type; public Instant ts = Instant.now(); public Map<String,Object> metadata;
}
