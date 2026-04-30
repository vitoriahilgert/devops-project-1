package br.ufscar.dc.dsw.AA2.controllers;

import br.ufscar.dc.dsw.AA2.dtos.testSession.*;
import br.ufscar.dc.dsw.AA2.services.TestSessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/test-sessions")
public class TestSessionController {

    @Autowired
    private TestSessionService testSessionService;

    @PostMapping("/{projectId}")
    public ResponseEntity<GetTestSessionResponseDTO> create(@RequestHeader("Authorization") String token, @PathVariable("projectId") UUID projectId,
                                                            @Valid @RequestBody CreateTestSessionRequestDTO request) {
        GetTestSessionResponseDTO session = testSessionService.createTestSession(token, projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @GetMapping
    public ResponseEntity<List<GetTestSessionResponseDTO>> getAllAllowed(@RequestHeader("Authorization") String token, @RequestParam("projectId") Optional<UUID> projectId) {
        List<GetTestSessionResponseDTO> sessions = testSessionService.getAllowedTestSessionsByToken(token, projectId);
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/{id}")
    public ResponseEntity<GetTestSessionResponseDTO> getById(@RequestHeader("Authorization") String token, @PathVariable UUID id) {
        GetTestSessionResponseDTO session = testSessionService.getTestSessionById(token, id);
        return ResponseEntity.ok(session);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetTestSessionResponseDTO> update(@RequestHeader("Authorization") String token, @PathVariable UUID id,
                                                                       @Valid @RequestBody UpdateSessionRequestDTO request) {
        GetTestSessionResponseDTO session = testSessionService.updateSession(token, id, request);
        return ResponseEntity.ok(session);
    }

    @PatchMapping("status/{id}")
    public ResponseEntity<UpdateSessionStatusResponseDTO> updateStatus(@RequestHeader("Authorization") String token, @PathVariable UUID id) {
        UpdateSessionStatusResponseDTO response = testSessionService.updateSessionStatus(token, id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("add-bug/{id}")
    public ResponseEntity<AddTestSessionBugResponseDTO> addBug(@RequestHeader("Authorization") String token, @PathVariable UUID id,
                                                               @Valid @RequestBody AddTestSessionBugRequestDTO request) throws JsonProcessingException {
        AddTestSessionBugResponseDTO response = testSessionService.addTestSessionBugs(token, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader("Authorization") String token, @PathVariable UUID id) {
        testSessionService.deleteTestSession(token, id);
        return ResponseEntity.noContent().build();
    }
}
