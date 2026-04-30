package br.ufscar.dc.dsw.AA2.models;

import br.ufscar.dc.dsw.AA2.models.enums.TestSessionStatusEnum;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="test_sessions")
public class TestSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private int duration;

    @Column
    private String description;

    @Column(nullable = false)
    private TestSessionStatusEnum status = TestSessionStatusEnum.CREATED;

    @Column
    private String bugs;

    @CreationTimestamp
    private LocalDateTime creationDateTime;

    @Column
    private LocalDateTime startDateTime;

    @Column
    private LocalDateTime finishDateTime;

    @ManyToOne
    @JoinColumn(name="project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tester_id")
    private User tester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="strategy_id")
    private Strategy strategy;

    public TestSession() {
    }

    public TestSession(UUID id, int duration, String description, TestSessionStatusEnum status, LocalDateTime creationDateTime, LocalDateTime startDateTime, LocalDateTime finishDateTime, User tester, Strategy strategy) {
        this.id = id;
        this.duration = duration;
        this.description = description;
        this.status = status;
        this.creationDateTime = creationDateTime;
        this.startDateTime = startDateTime;
        this.finishDateTime = finishDateTime;
        this.tester = tester;
        this.strategy = strategy;
    }

    public UUID getId() {
        return id;
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

    public User getTester() {
        return tester;
    }

    public void setTester(User tester) {
        this.tester = tester;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getBugs() {
        return bugs;
    }

    public void setBugs(String bugs) {
        this.bugs = bugs;
    }
}
