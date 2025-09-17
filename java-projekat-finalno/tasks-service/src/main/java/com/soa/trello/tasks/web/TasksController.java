package com.soa.trello.tasks.web;
import com.soa.trello.tasks.model.Task; import com.soa.trello.tasks.repo.TaskRepo;
import org.springframework.http.ResponseEntity; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.List; import java.util.Map;
@RestController @RequestMapping("/tasks")
public class TasksController {
  private final TaskRepo repo; public TasksController(TaskRepo repo){ this.repo=repo; }
  @PostMapping @PreAuthorize("hasAnyRole('MANAGER','MEMBER')")
  public ResponseEntity<?> create(@RequestBody CreateTask req){
    if (req.projectId==null || req.title==null || req.title.isBlank()) return ResponseEntity.badRequest().build();
    var t=new Task(); t.projectId=req.projectId; t.title=req.title; t.description=req.description; t.assigneeId=req.assigneeId; repo.save(t); return ResponseEntity.ok(t);
  }
  @GetMapping("/{id}") @PreAuthorize("hasAnyRole('MANAGER','MEMBER')")
  public ResponseEntity<?> get(@PathVariable String id){ var t=repo.findById(id).orElse(null); return t==null? ResponseEntity.notFound().build() : ResponseEntity.ok(Map.of("id",t.id,"projectId",t.projectId,"status",t.status.name())); }
  @GetMapping("/project/{projectId}") @PreAuthorize("hasAnyRole('MANAGER','MEMBER')")
  public java.util.List<Task> listByProject(@PathVariable String projectId){ return repo.findByProjectId(projectId); }
  @PatchMapping("/{id}/status") @PreAuthorize("hasAnyRole('MANAGER','MEMBER')")
  public ResponseEntity<?> setStatus(@PathVariable String id, @RequestBody Map<String,String> body, Authentication auth){
    var t=repo.findById(id).orElse(null); if (t==null) return ResponseEntity.notFound().build();
    String callerId=String.valueOf(auth.getPrincipal()); boolean isManager=auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_MANAGER"));
    if (!isManager && t.assigneeId!=null && !t.assigneeId.equals(callerId)) return ResponseEntity.status(403).body(Map.of("error","Možete menjati samo svoje zadatke"));
    try { t.status = Task.Status.valueOf(body.get("status")); } catch (Exception e){ return ResponseEntity.badRequest().body(Map.of("error","Nepoznat status")); }
    repo.save(t); return ResponseEntity.ok(Map.of("ok",true));
  }
  @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('MANAGER','MEMBER')")
  public ResponseEntity<Void> delete(@PathVariable String id, Authentication auth){
    var t=repo.findById(id).orElse(null); if (t==null) return ResponseEntity.notFound().build();
    boolean isManager=auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_MANAGER"));
    if (!isManager) return ResponseEntity.status(403).build();
    repo.deleteById(id); return ResponseEntity.noContent().build();
  }
  @GetMapping("/tasks/internal/project/{projectId}/has-open-tasks")
  public boolean hasOpen(@PathVariable String projectId){ return repo.countByProjectIdAndStatusNot(projectId, Task.Status.DONE) > 0; }
  @GetMapping("/tasks/internal/project/{projectId}/user/{userId}/has-in-progress")
  public boolean hasInProgress(@PathVariable String projectId, @PathVariable String userId){ return repo.existsByProjectIdAndAssigneeIdAndStatus(projectId,userId, Task.Status.IN_PROGRESS); }
  public static class CreateTask { public String projectId; public String title; public String description; public String assigneeId; }
}
