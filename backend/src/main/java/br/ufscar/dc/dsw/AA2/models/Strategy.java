package br.ufscar.dc.dsw.AA2.models;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "strategies")
public class Strategy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, length = 2000)
    private String examples;

    @Column(nullable = false, length = 2000)
    private String tips;

    @OneToMany(mappedBy = "strategy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "strategy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestSession> testSessions = new ArrayList<>();

    public Strategy() {
    }

    public Strategy(UUID id, String name, String description, String examples, String tips, List<Image> images, List<TestSession> testSessions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.examples = examples;
        this.tips = tips;
        this.images = images;
        this.testSessions = testSessions;
    }

    public List<TestSession> getTestSessions() {
        return testSessions;
    }

    public void setTestSessions(List<TestSession> testSessions) {
        this.testSessions = testSessions;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getExamples() {
        return examples;
    }

    public void setExamples(String examples) {
        this.examples = examples;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
