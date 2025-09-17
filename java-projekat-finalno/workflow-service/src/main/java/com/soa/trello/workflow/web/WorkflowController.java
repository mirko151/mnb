package com.soa.trello.workflow.web;
import com.soa.trello.workflow.model.TaskRef; import com.soa.trello.workflow.repo.TaskRefRepo;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*; import org.springframework.web.client.RestClient; import java.util.Map;
@RestController @RequestMapping("/workflow")
public class WorkflowController {
  private final TaskRefRepo repo; private final RestClient tasksClient = RestClient.builder().baseUrl(System.getenv("TASKS_URL")).build();
  public WorkflowController(TaskRefRepo repo){ this.repo=repo; }
  private static String key(String projectId, String taskId){ return projectId+":"+taskId; }
  @PostMapping("/projects/{projectId}/deps")
  public ResponseEntity<?> addDep(@PathVariable String projectId, @RequestBody AddDepReq req){
    var a = repo.findById(key(projectId, req.taskId())).orElseGet(() -> { var tr=new TaskRef(); tr.key=key(projectId, req.taskId()); return tr; });
    var b = repo.findById(key(projectId, req.dependsOnId())).orElseGet(() -> { var tr=new TaskRef(); tr.key=key(projectId, req.dependsOnId()); return tr; });
    a.dependsOn.add(b); repo.save(a); return ResponseEntity.ok(Map.of("ok",true));
  }
  @GetMapping("/projects/{projectId}/tasks/{taskId}/can-start")
  public boolean canStart(@PathVariable String projectId, @PathVariable String taskId){
    var node = repo.findById(key(projectId, taskId)).orElse(null);
    if (node==null || node.dependsOn==null || node.dependsOn.isEmpty()) return true;
    for (var dep: node.dependsOn){
      String depTaskId = dep.key.split(":",2)[1];
      var t = tasksClient.get().uri("/tasks/{id}", depTaskId).retrieve().toEntity(Map.class).getBody();
      if (t==null || !"DONE".equals(String.valueOf(t.get("status")))) return false;
    } return true;
  }
  public record AddDepReq(String taskId, String dependsOnId){}
}
