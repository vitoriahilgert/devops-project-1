package br.ufscar.dc.dsw.AA2.repositories;

import br.ufscar.dc.dsw.AA2.models.Project;
import br.ufscar.dc.dsw.AA2.models.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query("SELECT p FROM Project p WHERE :member MEMBER OF p.allowedMembers")
    List<Project> findProjectsWhereMemberIsAllowed(@Param("member") User member);
}
