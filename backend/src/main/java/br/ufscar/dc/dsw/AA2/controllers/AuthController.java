package br.ufscar.dc.dsw.AA2.controllers;

import br.ufscar.dc.dsw.AA2.dtos.auth.LoginRequestDTO;
import br.ufscar.dc.dsw.AA2.dtos.auth.LoginResponseDTO;
import br.ufscar.dc.dsw.AA2.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO loginResponseDTO = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponseDTO);
    }
}
