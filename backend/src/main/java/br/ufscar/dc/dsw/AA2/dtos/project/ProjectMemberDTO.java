package br.ufscar.dc.dsw.AA2.dtos.project;

import br.ufscar.dc.dsw.AA2.models.User;

import java.util.UUID;

public class ProjectMemberDTO {
    private UUID id;
    private String name;
    private String email;

    public ProjectMemberDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
