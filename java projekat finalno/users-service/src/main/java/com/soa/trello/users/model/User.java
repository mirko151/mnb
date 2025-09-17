package com.soa.trello.users.model;
import org.springframework.data.annotation.Id; import org.springframework.data.mongodb.core.index.Indexed; import org.springframework.data.mongodb.core.mapping.Document;
@Document("users")
public class User {
  @Id public String id;
  @Indexed(unique = true) public String username;
  public String passwordHash;
  public String firstName; public String lastName;
  @Indexed(unique = true) public String email;
  public Role role; public enum Role { MANAGER, MEMBER }
}
