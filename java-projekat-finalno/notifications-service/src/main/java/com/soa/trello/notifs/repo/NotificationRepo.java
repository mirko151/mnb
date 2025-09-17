package com.soa.trello.notifs.repo;
import com.soa.trello.notifs.model.Notification; import org.springframework.data.cassandra.repository.CassandraRepository; import java.util.List; import java.util.UUID;
public interface NotificationRepo extends CassandraRepository<Notification, UUID> {
  java.util.List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
}
