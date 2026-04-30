package br.ufscar.dc.dsw.AA2.services;

import br.ufscar.dc.dsw.AA2.config.JwtService;
import br.ufscar.dc.dsw.AA2.dtos.testSession.*;
import br.ufscar.dc.dsw.AA2.exceptions.BadRequestException;
import br.ufscar.dc.dsw.AA2.exceptions.ResourceNotFoundException;
import br.ufscar.dc.dsw.AA2.exceptions.UnauthorizedExeption;
import br.ufscar.dc.dsw.AA2.models.Project;
import br.ufscar.dc.dsw.AA2.models.Strategy;
import br.ufscar.dc.dsw.AA2.models.TestSession;
import br.ufscar.dc.dsw.AA2.models.User;
import br.ufscar.dc.dsw.AA2.models.enums.TestSessionStatusEnum;
import br.ufscar.dc.dsw.AA2.models.enums.UserRoleEnum;
import br.ufscar.dc.dsw.AA2.repositories.ProjectRepository;
import br.ufscar.dc.dsw.AA2.repositories.StrategyRepository;
import br.ufscar.dc.dsw.AA2.repositories.TestSessionRepository;
import br.ufscar.dc.dsw.AA2.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TestSessionService {
    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Qualifier("taskScheduler")
    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    public GetTestSessionResponseDTO createTestSession(String token, UUID projectId, CreateTestSessionRequestDTO dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto", "id", projectId.toString()));

        Strategy strategy = strategyRepository.findById(UUID.fromString(dto.getStrategyId()))
                .orElseThrow(() -> new ResourceNotFoundException("Estratégia", "id", dto.getStrategyId()));

        User tester = jwtService.getUserFromToken(token);
        if (tester == null) {
            throw new ResourceNotFoundException("User", "id", jwtService.getIdFromToken(token).toString());
        }

        checkIfUserIsAllowedOnProject(projectId, tester);

        TestSession testSession = new TestSession();
        testSession.setDuration(dto.getDuration());
        testSession.setProject(project);
        testSession.setTester(tester);
        testSession.setStrategy(strategy);
        testSession.setDescription(dto.getDescription());

        testSessionRepository.save(testSession);

        return new GetTestSessionResponseDTO(testSession);
    }

    public GetTestSessionResponseDTO getTestSessionById(String token, UUID sessionId) {
        User user = jwtService.getUserFromToken(token);
        checkIfUserIsAllowedOnSession(sessionId, user);

        TestSession testSession = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de teste", "id", sessionId.toString()));
        return new GetTestSessionResponseDTO(testSession);
    }

    public List<GetTestSessionResponseDTO> getAllowedTestSessionsByToken(String token, Optional<UUID> projectId) {
        User user = jwtService.getUserFromToken(token);
        Project project = null;

        if (projectId.isPresent()) {
            project = projectRepository.findById(projectId.get())
                    .orElseThrow(() -> new ResourceNotFoundException("Projeto", "id", projectId.toString()));
        }

        List<TestSession> testSessions;

        if (user.getRole().equals(UserRoleEnum.ADMIN)) {
            if (projectId.isPresent()) {
                testSessions = testSessionRepository.findAllByProject(project);
            } else {
                testSessions = testSessionRepository.findAll();
            }
        } else {
            if (projectId.isPresent()) {
                testSessions = testSessionRepository.findAllByProjectAndTester(project, user);
            } else {
                testSessions = testSessionRepository.findByTester(user);
            }
        }

        return testSessions.stream().map(GetTestSessionResponseDTO::new).collect(Collectors.toList());
    }


    public void deleteTestSession(String token, UUID sessionId) {
        TestSession testSession = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de teste", "id", sessionId.toString()));

        User user = jwtService.getUserFromToken(token);
        checkIfUserIsAllowedOnSession(sessionId, user);

        testSessionRepository.delete(testSession);
    }

    public GetTestSessionResponseDTO updateSession(String token, UUID sessionId, UpdateSessionRequestDTO dto) {
        TestSession testSession = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de teste", "id", sessionId.toString()));

        if (!strategyRepository.existsById(UUID.fromString(dto.getStrategyId()))) {
            throw new ResourceNotFoundException("Estratégia", "id", dto.getStrategyId());
        }

        if (!testSession.getStatus().equals(TestSessionStatusEnum.CREATED)) {
            throw new BadRequestException("A sessão de teste só pode ser editada antes de ser inicializada.");
        }

        User user = jwtService.getUserFromToken(token);
        checkIfUserIsAllowedOnSession(sessionId, user);

        testSession.setDuration(dto.getDuration());
        testSession.setDescription(dto.getDescription());
        testSession.setProject(testSession.getProject());
        testSession.setStrategy(testSession.getStrategy());

        testSessionRepository.save(testSession);
        return new GetTestSessionResponseDTO(testSession);
    }

    public UpdateSessionStatusResponseDTO updateSessionStatus(String token, UUID sessionId) {
        TestSession testSession = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de teste", "id", sessionId.toString()));

        TestSessionStatusEnum status = testSession.getStatus();

        User user = jwtService.getUserFromToken(token);
        checkIfUserIsAllowedOnSession(sessionId, user);

        if (status.equals(TestSessionStatusEnum.CREATED)) {
            testSession.setStatus(TestSessionStatusEnum.IN_PROGRESS);
            testSession.setStartDateTime(LocalDateTime.now());

            LocalDateTime endTime = LocalDateTime.now().plusMinutes(testSession.getDuration());
            testSession.setFinishDateTime(endTime);

            taskScheduler.schedule(() -> finishTestSession(testSession.getId()), endTime.atZone(ZoneId.of("America/Sao_Paulo")).toInstant());

        } else if (status.equals(TestSessionStatusEnum.IN_PROGRESS)) {
            testSession.setFinishDateTime(LocalDateTime.now());
            testSession.setStatus(TestSessionStatusEnum.FINISHED);
        } else {
            throw new BadRequestException("A sessão de teste já foi finalizada.");
        }

        testSessionRepository.save(testSession);
        return new UpdateSessionStatusResponseDTO(testSession, status);
    }

    public AddTestSessionBugResponseDTO addTestSessionBugs(String token, UUID sessionId, AddTestSessionBugRequestDTO dto) throws JsonProcessingException {
        TestSession testSession = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de teste", "id", sessionId.toString()));

        if (!testSession.getStatus().equals(TestSessionStatusEnum.IN_PROGRESS)) {
            throw new BadRequestException("Os bugs só podem ser registrados enquanto a sessão de teste está em progresso.");
        }

        User user = jwtService.getUserFromToken(token);
        checkIfUserIsAllowedOnSession(sessionId, user);

        List<String> bugsList = (testSession.getBugs() == null) ? new ArrayList<>() : objectMapper.readValue(testSession.getBugs(), new TypeReference<>() {});
        bugsList.add(dto.getBug());
        String updatedBugs = objectMapper.writeValueAsString(bugsList);

        testSession.setBugs(updatedBugs);
        testSessionRepository.save(testSession);
        return new AddTestSessionBugResponseDTO(testSession);
    }

    private void finishTestSession(UUID sessionId) {
        TestSession testSession = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de teste", "id", sessionId.toString()));

        testSession.setStatus(TestSessionStatusEnum.FINISHED);
        testSession.setFinishDateTime(LocalDateTime.now());

        testSessionRepository.save(testSession);

        System.out.println("Sessão de teste de id igual a " + sessionId + " finalizada pelo taskScheduler");
    }

    private void checkIfUserIsAllowedOnProject(UUID projectId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto", "id", projectId.toString()));

        if (user.getRole().equals(UserRoleEnum.TESTER)) {
            if (!project.getAllowedMembers().contains(user)) {
                throw new UnauthorizedExeption("O testador não é um membro autorizado do projeto.");
            }
        }
    }

    private void checkIfUserIsAllowedOnSession(UUID sessionId, User user) {
        TestSession testSession = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de teste", "id", sessionId.toString()));

        if (user.getRole().equals(UserRoleEnum.TESTER)) {
            if (!testSession.getTester().getId().equals(user.getId())) {
                throw new UnauthorizedExeption("O testador não é o dono da sessão de teste.");
            }
        }
    }
}
