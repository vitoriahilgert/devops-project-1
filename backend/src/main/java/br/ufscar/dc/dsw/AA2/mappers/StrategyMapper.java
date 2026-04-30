package br.ufscar.dc.dsw.AA2.mappers;

import br.ufscar.dc.dsw.AA2.dtos.strategy.StrategyCreateDTO;
import br.ufscar.dc.dsw.AA2.dtos.strategy.StrategyResponseDTO;
import br.ufscar.dc.dsw.AA2.models.Image;
import br.ufscar.dc.dsw.AA2.models.Strategy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StrategyMapper {

    public StrategyResponseDTO toDTO(Strategy strategy) {
        if (strategy == null) {
            return null;
        }

        List<String> imageUrls = strategy.getImages() != null
                ? strategy.getImages().stream().map(Image::getUrl).collect(Collectors.toList())
                : Collections.emptyList();

        return new StrategyResponseDTO(
                strategy.getId(),
                strategy.getName(),
                strategy.getDescription(),
                strategy.getExamples(),
                strategy.getTips(),
                imageUrls
        );
    }
}