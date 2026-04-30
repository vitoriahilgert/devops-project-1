package br.ufscar.dc.dsw.AA2.repositories;

import br.ufscar.dc.dsw.AA2.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
}
