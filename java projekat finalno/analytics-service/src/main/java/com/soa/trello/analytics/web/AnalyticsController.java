package com.soa.trello.analytics.web;
import com.soa.trello.analytics.model.Activity; import com.soa.trello.analytics.repo.ActivityRepo; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/analytics")
public class AnalyticsController {
  private final ActivityRepo repo; public AnalyticsController(ActivityRepo repo){ this.repo=repo; }
  @PostMapping("/events") public Activity logEvent(@RequestBody Activity a){ return repo.save(a); }
  @GetMapping("/projects/{projectId}/timeline") public java.util.List<Activity> history(@PathVariable String projectId){ return repo.findByProjectIdOrderByTsDesc(projectId); }
}
