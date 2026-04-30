package br.ufscar.dc.dsw.AA2.dtos.user;

import br.ufscar.dc.dsw.AA2.models.enums.UserRoleEnum;
import jakarta.validation.constraints.*;

public class UpdateUserRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ '-]+$", message = "O nome deve conter apenas letras, espaços, apóstrofos ou hífens")
    private String name;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 254, message = "O email deve ter no máximo 254 caracteres")
    private String email;

    @NotNull(message = "O papel do usuário é obrigatório")
    private UserRoleEnum role;

    public UpdateUserRequestDTO() {}

    public UpdateUserRequestDTO(String name, String email, UserRoleEnum role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRoleEnum getRole() {
        return role;
    }

    public void setRole(UserRoleEnum role) {
        this.role = role;
    }
}
