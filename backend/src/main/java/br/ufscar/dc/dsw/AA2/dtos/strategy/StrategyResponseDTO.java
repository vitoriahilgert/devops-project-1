package br.ufscar.dc.dsw.AA2.dtos.strategy;

import java.util.List;
import java.util.UUID;

public record StrategyResponseDTO(
        UUID id,
        String name,
        String description,
        String examples,
        String tips,
        List<String> imageUrls
) {
}