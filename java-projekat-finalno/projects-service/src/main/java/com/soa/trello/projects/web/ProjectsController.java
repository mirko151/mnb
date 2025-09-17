package com.soa.trello.projects.web;
import com.soa.trello.projects.model.Project; import com.soa.trello.projects.repo.ProjectRepo;
import org.springframework.http.ResponseEntity; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import org.springframework.web.client.RestClient;
import java.util.Map;
@RestController @RequestMapping("/projects")
public class ProjectsController {
  private final ProjectRepo repo; private final RestClient tasksClient;
  public ProjectsController(ProjectRepo repo){ this.repo=repo; this.tasksClient=RestClient.builder().baseUrl(System.getenv("TASKS_URL")).build(); }
  @PostMapping
  public ResponseEntity<?> create(@RequestBody CreateProject req, Authentication auth){
    String callerId = String.valueOf(auth.getPrincipal());
    boolean isManager = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
    if (!isManager) return ResponseEntity.status(403).body(Map.of("error","Samo menadžer može da kreira projekat"));
    var p = new Project();
    p.name = req.name(); p.expectedEndDate = req.expectedEndDate(); p.minTeamSize = req.minTeamSize(); p.maxTeamSize = req.maxTeamSize(); p.managerId = callerId;
    repo.save(p); return ResponseEntity.ok(p);
  }
  @PostMapping("/{projectId}/members/{memberId}")
  public ResponseEntity<?> addMember(@PathVariable String projectId, @PathVariable String memberId, Authentication auth){
    String callerId = String.valueOf(auth.getPrincipal());
    var p = repo.findById(projectId).orElse(null); if (p==null) return ResponseEntity.notFound().build();
    if (!p.managerId.equals(callerId)) return ResponseEntity.status(403).body(Map.of("error","Niste menadžer ovog projekta"));
    boolean hasUnfinished = Boolean.TRUE.equals(tasksClient.get().uri("/tasks/internal/project/{projectId}/has-open-tasks", projectId).retrieve().toEntity(Boolean.class).getBody());
    if (!hasUnfinished) return ResponseEntity.badRequest().body(Map.of("error","Projekat je završen – dodavanje nije dozvoljeno"));
    if (p.memberIds.size() >= p.maxTeamSize) return ResponseEntity.badRequest().body(Map.of("error","Popunjen maksimalni kapacitet"));
    p.memberIds.add(memberId); repo.save(p); return ResponseEntity.ok(Map.of("ok",true));
  }
  @DeleteMapping("/{projectId}/members/{memberId}")
  public ResponseEntity<?> removeMember(@PathVariable String projectId, @PathVariable String memberId, Authentication auth){
    String callerId = String.valueOf(auth.getPrincipal());
    var p = repo.findById(projectId).orElse(null); if (p==null) return ResponseEntity.notFound().build();
    if (!p.managerId.equals(callerId)) return ResponseEntity.status(403).body(Map.of("error","Niste menadžer ovog projekta"));
    boolean hasInProgress = Boolean.TRUE.equals(tasksClient.get().uri("/tasks/internal/project/{projectId}/user/{userId}/has-in-progress", projectId, memberId).retrieve().toEntity(Boolean.class).getBody());
    if (hasInProgress) return ResponseEntity.badRequest().body(Map.of("error","Korisnik ima zadatke u izradi"));
    p.memberIds.remove(memberId); repo.save(p); return ResponseEntity.noContent().build();
  }
  @GetMapping("/internal/manager/{id}/has-active")
  public boolean managerHasActive(@PathVariable String id){
    for (var p: repo.findByManagerId(id)){
      Boolean open = tasksClient.get().uri("/tasks/internal/project/{projectId}/has-open-tasks", p.id).retrieve().toEntity(Boolean.class).getBody();
      if (Boolean.TRUE.equals(open)) return true;
    } return false;
  }
  @GetMapping("/internal/member/{id}/has-active-membership")
  public boolean memberOnActive(@PathVariable String id){
    for (var p: repo.findByMemberIdsContains(id)){
      Boolean open = tasksClient.get().uri("/tasks/internal/project/{projectId}/has-open-tasks", p.id).retrieve().toEntity(Boolean.class).getBody();
      if (Boolean.TRUE.equals(open)) return true;
    } return false;
  }
  public record CreateProject(String name, java.time.LocalDate expectedEndDate, int minTeamSize, int maxTeamSize){}
}
