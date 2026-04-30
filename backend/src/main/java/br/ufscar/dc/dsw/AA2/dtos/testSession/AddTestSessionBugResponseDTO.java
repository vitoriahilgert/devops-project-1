package br.ufscar.dc.dsw.AA2.dtos.testSession;

import br.ufscar.dc.dsw.AA2.models.TestSession;

import java.util.UUID;

public class AddTestSessionBugResponseDTO {
    private UUID sessionId;
    private String bugs;

    public AddTestSessionBugResponseDTO(TestSession session) {
        this.sessionId = session.getId();
        this.bugs = session.getBugs();
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public String getBugs() {
        return bugs;
    }

    public void setBugs(String bugs) {
        this.bugs = bugs;
    }
}
