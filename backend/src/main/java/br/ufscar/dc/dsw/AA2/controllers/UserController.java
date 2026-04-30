package br.ufscar.dc.dsw.AA2.controllers;

import br.ufscar.dc.dsw.AA2.dtos.user.*;
import br.ufscar.dc.dsw.AA2.models.enums.UserRoleEnum;
import br.ufscar.dc.dsw.AA2.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<GetUserResponseDTO> create(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateUserRequestDTO request) {
        GetUserResponseDTO user = userService.createUser(token, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping
    public ResponseEntity<List<GetUserResponseDTO>> getAll(@RequestHeader("Authorization") String token) {
        List<GetUserResponseDTO> users = userService.getAllUsers(token);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserResponseDTO> getById(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID id) {
        GetUserResponseDTO user = userService.getUserById(token, id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetUserResponseDTO> update(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequestDTO request) {
        GetUserResponseDTO updatedUser = userService.updateUser(token, id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID id) {
        userService.deleteUser(token, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admins")
    public ResponseEntity<List<GetUserResponseDTO>> getAdmins(@RequestHeader("Authorization") String token) {
        List<GetUserResponseDTO> admins = userService.getUsersByRole(token, UserRoleEnum.ADMIN);
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/testers")
    public ResponseEntity<List<GetUserResponseDTO>> getTesters(@RequestHeader("Authorization") String token) {
        List<GetUserResponseDTO> testers = userService.getUsersByRole(token, UserRoleEnum.TESTER);
        return ResponseEntity.ok(testers);
    }
}
