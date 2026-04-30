package br.ufscar.dc.dsw.AA2.services;

import br.ufscar.dc.dsw.AA2.config.JwtService;
import br.ufscar.dc.dsw.AA2.dtos.project.*;
import br.ufscar.dc.dsw.AA2.dtos.testSession.GetTestSessionResponseDTO;
import br.ufscar.dc.dsw.AA2.exceptions.ResourceNotFoundException;
import br.ufscar.dc.dsw.AA2.models.Project;
import br.ufscar.dc.dsw.AA2.models.User;
import br.ufscar.dc.dsw.AA2.models.enums.UserRoleEnum;
import br.ufscar.dc.dsw.AA2.repositories.ProjectRepository;
import br.ufscar.dc.dsw.AA2.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public GetProjectResponseDTO createProject(CreateProjectRequestDTO dto) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setCreationDateTime(LocalDateTime.now());

        if (dto.getAllowedMembersIds() != null && !dto.getAllowedMembersIds().isEmpty()) {
            List<User> allowedMembers = userRepository.findAllById(dto.getAllowedMembersIds());
            project.setAllowedMembers(allowedMembers);
        }

        Project saved = projectRepository.save(project);

        return new GetProjectResponseDTO(saved);
    }

    public List<GetProjectResponseDTO> getAllProjectsByToken(String token, boolean filter) {
        User user = jwtService.getUserFromToken(token);
        List<Project> projects;

        if (user.getRole().equals(UserRoleEnum.ADMIN)) {
            projects = projectRepository.findAll();
        } else {
            if (filter) {
                projects = projectRepository.findAll().stream()
                        .filter(p -> p.getAllowedMembers().contains(user))
                        .collect(Collectors.toList());
            } else {
                projects = projectRepository.findAll();
            }
        }

        return projects.stream().map(GetProjectResponseDTO::new).collect(Collectors.toList());
    }

    public GetProjectResponseDTO getProjectById(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id.toString()));
        return new GetProjectResponseDTO(project);
    }

    public GetProjectResponseDTO updateProject(UUID id, UpdateProjectRequestDTO dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id.toString()));

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());

        if (dto.getAllowedMembersIds() != null) {
            List<User> allowedUsers = userRepository.findAllById(dto.getAllowedMembersIds());
            project.setAllowedMembers(allowedUsers);
        }

        projectRepository.save(project);

        return new GetProjectResponseDTO(project);
    }

    public void deleteProject(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id.toString()));

        projectRepository.delete(project);
    }
}
