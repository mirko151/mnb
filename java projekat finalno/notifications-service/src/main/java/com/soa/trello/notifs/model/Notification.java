package com.soa.trello.notifs.model;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn; import org.springframework.data.cassandra.core.mapping.PrimaryKeyType; import org.springframework.data.cassandra.core.mapping.Table;
import java.time.Instant; import java.util.UUID;
@Table("notifications_by_user")
public class Notification {
  @PrimaryKeyColumn(name="user_id", type=PrimaryKeyType.PARTITIONED) public String userId;
  @PrimaryKeyColumn(name="created_at", type=PrimaryKeyType.CLUSTERED, ordinal=0) public Instant createdAt;
  @PrimaryKeyColumn(name="notification_id", type=PrimaryKeyType.CLUSTERED, ordinal=1) public UUID notificationId;
  public String type; public String message; public boolean read;
}
