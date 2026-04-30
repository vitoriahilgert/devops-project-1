package br.ufscar.dc.dsw.AA2.services;

import br.ufscar.dc.dsw.AA2.config.JwtService;
import br.ufscar.dc.dsw.AA2.dtos.auth.LoginRequestDTO;
import br.ufscar.dc.dsw.AA2.dtos.auth.LoginResponseDTO;
import br.ufscar.dc.dsw.AA2.exceptions.InvalidCredentialsException;
import br.ufscar.dc.dsw.AA2.exceptions.ResourceNotFoundException;
import br.ufscar.dc.dsw.AA2.models.User;
import br.ufscar.dc.dsw.AA2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User", "email", dto.getEmail());
        }

        boolean passwordMatch = passwordEncoder.matches(dto.getPassword(), user.get().getPassword());
        if (!passwordMatch) {
            throw new InvalidCredentialsException();
        }

        return new LoginResponseDTO(jwtService.generateToken(user.get()));
    }
}
