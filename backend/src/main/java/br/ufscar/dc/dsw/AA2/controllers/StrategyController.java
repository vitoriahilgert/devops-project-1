package br.ufscar.dc.dsw.AA2.controllers;

import br.ufscar.dc.dsw.AA2.dtos.strategy.StrategyCreateDTO;
import br.ufscar.dc.dsw.AA2.dtos.strategy.StrategyResponseDTO;
import br.ufscar.dc.dsw.AA2.mappers.StrategyMapper;
import br.ufscar.dc.dsw.AA2.models.Strategy;
import br.ufscar.dc.dsw.AA2.services.StrategyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/strategies")
public class StrategyController {

    @Autowired
    private StrategyService strategyService;

    @Autowired
    private StrategyMapper strategyMapper;

    @GetMapping
    public ResponseEntity<List<StrategyResponseDTO>> getAllStrategies() {
        List<StrategyResponseDTO> dtos = strategyService.getAll().stream()
                .map(strategyMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StrategyResponseDTO> getStrategyById(@PathVariable UUID id) {
        return strategyService.getById(id)
                .map(strategyMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<StrategyResponseDTO> createStrategy(
            @Valid @RequestPart("strategy") StrategyCreateDTO strategyDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        Strategy createdStrategy = strategyService.create(strategyDTO, images);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdStrategy.getId())
                .toUri();

        return ResponseEntity.created(location).body(strategyMapper.toDTO(createdStrategy));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<StrategyResponseDTO> updateStrategy(
            @PathVariable UUID id,
            @Valid @RequestBody StrategyCreateDTO strategyDTO) {

        Strategy updatedStrategy = strategyService.update(id, strategyDTO);
        return ResponseEntity.ok(strategyMapper.toDTO(updatedStrategy));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<Void> deleteStrategy(@PathVariable UUID id) {
        strategyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}