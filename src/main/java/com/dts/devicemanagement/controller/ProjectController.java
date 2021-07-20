package com.dts.devicemanagement.controller;


import com.dts.devicemanagement.service.ProjectService;
import com.dts.devicemanagement.service.ProjectServiceJdbcImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/projects")
    public ResponseEntity<Object> projects() {

        return ResponseEntity.ok(projectService.getProjectsStats());
    }


}
