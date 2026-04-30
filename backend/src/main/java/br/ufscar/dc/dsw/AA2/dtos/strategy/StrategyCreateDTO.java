package br.ufscar.dc.dsw.AA2.dtos.strategy;

public record StrategyCreateDTO(
        String name,
        String description,
        String examples,
        String tips
) {
}