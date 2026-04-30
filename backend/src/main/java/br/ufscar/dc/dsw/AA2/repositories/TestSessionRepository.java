package br.ufscar.dc.dsw.AA2.repositories;

import br.ufscar.dc.dsw.AA2.models.Project;
import br.ufscar.dc.dsw.AA2.models.TestSession;
import br.ufscar.dc.dsw.AA2.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestSessionRepository extends JpaRepository<TestSession, UUID> {
    List<TestSession> findByTester(User tester);

    List<TestSession> findAllByProject(Project project);

    List<TestSession> findAllByProjectAndTester(Project project, User tester);
}
