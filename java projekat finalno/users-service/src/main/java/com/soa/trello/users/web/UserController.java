package com.soa.trello.users.web;
import com.soa.trello.users.repo.UserRepo; import org.springframework.http.ResponseEntity; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import org.springframework.web.client.RestClient; import java.util.Map;
@RestController @RequestMapping("/users")
public class UserController {
  private final UserRepo repo; private final RestClient projectsClient;
  public UserController(UserRepo repo){ this.repo=repo; this.projectsClient=RestClient.builder().baseUrl(System.getenv("PROJECTS_URL")).build(); }
  @GetMapping("/me")
  public ResponseEntity<?> me(Authentication auth){
    var u = repo.findById(String.valueOf(auth.getPrincipal())).orElse(null);
    return u==null? ResponseEntity.notFound().build() : ResponseEntity.ok(Map.of("id",u.id,"username",u.username,"firstName",u.firstName,"lastName",u.lastName,"email",u.email,"role",u.role));
  }
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable String id, Authentication auth){
    var caller = repo.findById(String.valueOf(auth.getPrincipal())).orElse(null);
    if (caller==null) return ResponseEntity.status(401).build();
    if (!caller.id.equals(id)) return ResponseEntity.status(403).body(Map.of("error","Možete brisati samo svoj nalog"));
    if (caller.role== com.soa.trello.users.model.User.Role.MANAGER){
      var resp = projectsClient.get().uri("/projects/internal/manager/{id}/has-active", id).retrieve().toEntity(Boolean.class).getBody();
      if (Boolean.TRUE.equals(resp)) return ResponseEntity.badRequest().body(Map.of("error","Imate aktivne projekte"));
    } else {
      var resp = projectsClient.get().uri("/projects/internal/member/{id}/has-active-membership", id).retrieve().toEntity(Boolean.class).getBody();
      if (Boolean.TRUE.equals(resp)) return ResponseEntity.badRequest().body(Map.of("error","Član ste na aktivnom projektu"));
    }
    repo.deleteById(id); return ResponseEntity.noContent().build();
  }
}
