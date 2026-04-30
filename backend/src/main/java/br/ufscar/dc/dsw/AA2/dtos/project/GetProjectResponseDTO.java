package br.ufscar.dc.dsw.AA2.dtos.project;

import br.ufscar.dc.dsw.AA2.models.Project;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GetProjectResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime creationDateTime;
    private List<ProjectMemberDTO> allowedMembers;

    public GetProjectResponseDTO(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.creationDateTime = project.getCreationDateTime();
        this.allowedMembers = project.getAllowedMembers()
                .stream()
                .map(ProjectMemberDTO::new)
                .collect(Collectors.toList());
    }

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreationDateTime() { return creationDateTime; }

    public void setCreationDateTime(LocalDateTime creationDateTime) { this.creationDateTime = creationDateTime; }

    public List<ProjectMemberDTO> getAllowedMembers() { return allowedMembers; }

    public void setAllowedMembers(List<ProjectMemberDTO> allowedMembers) {
        this.allowedMembers = allowedMembers;
    }
}
