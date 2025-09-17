package com.soa.trello.users.repo;
import com.soa.trello.users.model.User; import org.springframework.data.mongodb.repository.MongoRepository; import java.util.Optional;
public interface UserRepo extends MongoRepository<User, String> {
  Optional<User> findByUsername(String username);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
}
