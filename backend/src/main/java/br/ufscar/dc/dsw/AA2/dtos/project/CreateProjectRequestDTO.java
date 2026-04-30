package br.ufscar.dc.dsw.AA2.dtos.project;

import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class CreateProjectRequestDTO {
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
    private String name;

    @NotBlank(message = "A descrição é obrigatória.")
    private String description;

    private List<UUID> allowedMembersIds;

    public CreateProjectRequestDTO() {
    }

    public CreateProjectRequestDTO(String name, String description, List<UUID> allowedMembersIds) {
        this.name = name;
        this.description = description;
        this.allowedMembersIds = allowedMembersIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UUID> getAllowedMembersIds() {
        return allowedMembersIds;
    }

    public void setAllowedMembersIds(List<UUID> allowedMembers) {
        this.allowedMembersIds = allowedMembers;
    }
}


