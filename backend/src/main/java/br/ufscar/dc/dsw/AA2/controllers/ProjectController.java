package br.ufscar.dc.dsw.AA2.controllers;

import br.ufscar.dc.dsw.AA2.dtos.project.*;
import br.ufscar.dc.dsw.AA2.services.ProjectService;
import br.ufscar.dc.dsw.AA2.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<GetProjectResponseDTO> createProject(
            @RequestBody @Valid CreateProjectRequestDTO request) {

        GetProjectResponseDTO project = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProjectResponseDTO> getProjectById(@PathVariable UUID id) {
        GetProjectResponseDTO project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @GetMapping
    public ResponseEntity<List<GetProjectResponseDTO>> getAllProjects(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "filter", required = false, defaultValue = "false") boolean filter) {

        List<GetProjectResponseDTO> projects = projectService.getAllProjectsByToken(token, filter);
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<GetProjectResponseDTO> updateProject(@PathVariable UUID id, @RequestBody UpdateProjectRequestDTO request) {
        GetProjectResponseDTO project = projectService.updateProject(id, request);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
