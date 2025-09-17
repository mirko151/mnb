package com.soa.trello.users.web;
import com.soa.trello.users.model.User; import com.soa.trello.users.repo.UserRepo;
import io.jsonwebtoken.Jwts; import io.jsonwebtoken.SignatureAlgorithm; import io.jsonwebtoken.security.Keys;
import org.springframework.http.ResponseEntity; import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.web.bind.annotation.*;
import javax.crypto.SecretKey; import java.nio.charset.StandardCharsets; import java.time.Instant; import java.util.Date; import java.util.Map;
@RestController @RequestMapping("/users/auth")
public class AuthController {
  private final UserRepo repo; private final BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
  public AuthController(UserRepo repo){ this.repo=repo; }
  private SecretKey key(){ return Keys.hmacShaKeyFor(System.getenv("JWT_SECRET").getBytes(StandardCharsets.UTF_8)); }
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterReq req){
    if (repo.existsByUsername(req.username)) return ResponseEntity.badRequest().body(Map.of("error","Username zauzet"));
    if (repo.existsByEmail(req.email)) return ResponseEntity.badRequest().body(Map.of("error","Email zauzet"));
    if (req.firstName==null || req.lastName==null || req.email==null) return ResponseEntity.badRequest().body(Map.of("error","Nedostaju polja"));
    var u=new User(); u.username=req.username; u.passwordHash=enc.encode(req.password);
    u.firstName=req.firstName; u.lastName=req.lastName; u.email=req.email; u.role=User.Role.valueOf(req.role);
    repo.save(u); return ResponseEntity.ok(Map.of("id", u.id));
  }
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginReq req){
    var u=repo.findByUsername(req.username).orElse(null);
    if (u==null || !enc.matches(req.password,u.passwordHash)) return ResponseEntity.status(401).body(Map.of("error","Neispravni kredencijali"));
    String token = Jwts.builder().setSubject(u.username).setIssuedAt(new Date()).setExpiration(Date.from(Instant.now().plusSeconds(60L*60*4)))
      .addClaims(Map.of("userId", u.id, "role", u.role.name())).signWith(key(), SignatureAlgorithm.HS256).compact();
    return ResponseEntity.ok(Map.of("token", token, "userId", u.id, "role", u.role));
  }
  public record RegisterReq(String username,String password,String firstName,String lastName,String email,String role){}
  public record LoginReq(String username,String password){}
}
