package br.ufscar.dc.dsw.AA2.dtos.user;

import br.ufscar.dc.dsw.AA2.models.enums.UserRoleEnum;
import jakarta.validation.constraints.*;

public class CreateUserRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ '-]+$", message = "O nome deve conter apenas letras, espaços, apóstrofos ou hífens")
    private String name;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 254, message = "O email deve ter no máximo 254 caracteres")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 64, message = "A senha deve ter entre 6 e 64 caracteres")
    private String password;

    @NotNull(message = "O papel do usuário é obrigatório")
    private UserRoleEnum role;

    public CreateUserRequestDTO() {}

    public CreateUserRequestDTO(String name, String email, String password, UserRoleEnum role) {
        this.name = name;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRoleEnum getRole() {
        return role;
    }

    public void setRole(UserRoleEnum role) {
        this.role = role;
    }
}
