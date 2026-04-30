package br.ufscar.dc.dsw.AA2.dtos.testSession;

import br.ufscar.dc.dsw.AA2.models.TestSession;
import br.ufscar.dc.dsw.AA2.models.enums.TestSessionStatusEnum;
import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.UUID;

public class GetTestSessionResponseDTO {
    private UUID id;
    private int duration;
    private String description;
    private TestSessionStatusEnum status;
    private LocalDateTime creationDateTime;
    private LocalDateTime startDateTime;
    private LocalDateTime finishDateTime;
    private UUID testerId;
    private UUID projectId;
    private UUID strategyId;
    private String bugs;


    public GetTestSessionResponseDTO(TestSession testSession) {
        this.id = testSession.getId();
        this.duration = testSession.getDuration();
        this.description = testSession.getDescription();
        this.status = testSession.getStatus();
        this.creationDateTime = testSession.getCreationDateTime();
        this.testerId = testSession.getTester().getId();
        this.creationDateTime = testSession.getCreationDateTime();
        this.projectId = testSession.getProject().getId();
        this.strategyId = testSession.getStrategy().getId();
        this.startDateTime = testSession.getStartDateTime();
        this.finishDateTime = testSession.getFinishDateTime();
        this.bugs = testSession.getBugs();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TestSessionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(TestSessionStatusEnum status) {
        this.status = status;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getFinishDateTime() {
        return finishDateTime;
    }

    public void setFinishDateTime(LocalDateTime finishDateTime) {
        this.finishDateTime = finishDateTime;
    }

    public UUID getTesterId() {
        return testerId;
    }

    public void setTesterId(UUID testerId) {
        this.testerId = testerId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(UUID strategyId) {
        this.strategyId = strategyId;
    }

    public String getBugs() {
        return bugs;
    }
}
