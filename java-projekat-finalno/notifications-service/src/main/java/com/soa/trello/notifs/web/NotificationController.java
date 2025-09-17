package com.soa.trello.notifs.web;
import com.soa.trello.notifs.model.Notification; import com.soa.trello.notifs.repo.NotificationRepo;
import org.springframework.http.ResponseEntity; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.time.Instant; import java.util.Map; import java.util.UUID; import java.util.List;
@RestController @RequestMapping("/notifications")
public class NotificationController {
  private final NotificationRepo repo; public NotificationController(NotificationRepo repo){ this.repo=repo; }
  @GetMapping("/me") public java.util.List<Notification> my(Authentication auth){ return repo.findByUserIdOrderByCreatedAtDesc(String.valueOf(auth.getPrincipal())); }
  @PostMapping("/seed") public ResponseEntity<?> seed(Authentication auth){
    var n=new Notification(); n.userId=String.valueOf(auth.getPrincipal()); n.createdAt=Instant.now(); n.notificationId=UUID.randomUUID(); n.type="INFO"; n.message="Dobrodošli!"; n.read=false; repo.save(n);
    return ResponseEntity.ok(Map.of("ok",true));
  }
}
