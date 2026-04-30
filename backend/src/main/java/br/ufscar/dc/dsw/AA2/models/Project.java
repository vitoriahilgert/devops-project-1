package br.ufscar.dc.dsw.AA2.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @CreationTimestamp
    private LocalDateTime creationDateTime;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestSession> testSessions;

    @ManyToMany(targetEntity = User.class)
    private List<User> allowedMembers;

    public Project() {
    }

    public Project(UUID id, String name, String description, LocalDateTime creationDateTime, List<User> allowedMembers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creationDateTime = creationDateTime;
        this.allowedMembers = allowedMembers;

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public List<User> getAllowedMembers() {
        return allowedMembers;
    }

    public void setAllowedMembers(List<User> allowedMembers) {
        this.allowedMembers = allowedMembers;
    }

    public List<TestSession> getTestSessions() {
        return testSessions;
    }

    public void setTestSessions(List<TestSession> testSessions) {
        this.testSessions = testSessions;
    }

    public void addAllowedMember(User user) {
        if (this.allowedMembers == null) {
            this.allowedMembers = new ArrayList<>();
        }
        if (!this.allowedMembers.contains(user)) {
            this.allowedMembers.add(user);
        }
    }

}



