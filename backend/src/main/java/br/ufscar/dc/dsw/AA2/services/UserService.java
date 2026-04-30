package br.ufscar.dc.dsw.AA2.services;

import br.ufscar.dc.dsw.AA2.config.JwtService;
import br.ufscar.dc.dsw.AA2.dtos.user.CreateUserRequestDTO;
import br.ufscar.dc.dsw.AA2.dtos.user.GetUserResponseDTO;
import br.ufscar.dc.dsw.AA2.dtos.user.UpdateUserRequestDTO;
import br.ufscar.dc.dsw.AA2.exceptions.ResourceNotFoundException;
import br.ufscar.dc.dsw.AA2.exceptions.UnauthorizedExeption;
import br.ufscar.dc.dsw.AA2.models.Project;
import br.ufscar.dc.dsw.AA2.models.User;
import br.ufscar.dc.dsw.AA2.models.enums.UserRoleEnum;
import br.ufscar.dc.dsw.AA2.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private void checkIfAdmin(String token) {
        User user = jwtService.getUserFromToken(token);
        if (user.getRole() != UserRoleEnum.ADMIN) {
            throw new UnauthorizedExeption("Acesso negado: apenas administradores podem realizar esta operação.");
        }
    }

    public List<GetUserResponseDTO> getAllUsers(String token) {
        checkIfAdmin(token);
        return userRepository.findAll().stream()
                .map(GetUserResponseDTO::new)
                .collect(Collectors.toList());
    }

    public GetUserResponseDTO getUserById(String token, UUID id) {
        checkIfAdmin(token);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));
        return new GetUserResponseDTO(user);
    }

    public List<GetUserResponseDTO> getUsersByRole(String token, UserRoleEnum role) {
        checkIfAdmin(token);
        return userRepository.findByRole(role).stream()
                .map(GetUserResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public GetUserResponseDTO createUser(String token, CreateUserRequestDTO dto) {
        checkIfAdmin(token);

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceNotFoundException("User", "email", dto.getEmail());
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        return new GetUserResponseDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(String token, UUID id) {
        checkIfAdmin(token);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));

        for (Project project : new ArrayList<>(user.getProjects())) {
            project.getAllowedMembers().remove(user);
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public GetUserResponseDTO updateUser(String token, UUID id, UpdateUserRequestDTO dto) {
        checkIfAdmin(token);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        return new GetUserResponseDTO(userRepository.save(user));
    }
}
