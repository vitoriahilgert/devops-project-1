package br.ufscar.dc.dsw.AA2.services;

import br.ufscar.dc.dsw.AA2.dtos.strategy.StrategyCreateDTO;
import br.ufscar.dc.dsw.AA2.exceptions.ResourceNotFoundException;
import br.ufscar.dc.dsw.AA2.mappers.StrategyMapper;
import br.ufscar.dc.dsw.AA2.models.Image;
import br.ufscar.dc.dsw.AA2.models.Strategy;
import br.ufscar.dc.dsw.AA2.repositories.ImageRepository;
import br.ufscar.dc.dsw.AA2.repositories.StrategyRepository;
import br.ufscar.dc.dsw.AA2.storage.StorageProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StrategyService {
    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private StorageProvider storageProvider;

    @Autowired
    private StrategyMapper strategyMapper;

    @Transactional()
    public List<Strategy> getAll() {
        return strategyRepository.findAll();
    }

    @Transactional()
    public Optional<Strategy> getById(UUID id) {
        Strategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estratégia", "id", id.toString()));
        return Optional.ofNullable(strategy);
    }

    public Strategy create(StrategyCreateDTO strategyDTO, List<MultipartFile> imagesFiles) {

        Strategy strategy = new Strategy();

        strategy.setName(strategyDTO.name());
        strategy.setDescription(strategyDTO.description());
        strategy.setExamples(strategyDTO.examples());
        strategy.setTips(strategyDTO.tips());

        strategyRepository.save(strategy);

        List<Image> images = new ArrayList<>();
        if (imagesFiles != null && !imagesFiles.isEmpty()) {
            images = this.uploadImages(strategy, imagesFiles);
        }
        strategy.setImages(images);

        return strategy;
    }

    public Strategy update(UUID id, StrategyCreateDTO strategyDTO) {
        Strategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estratégia", "id", id.toString()));

        strategy.setName(strategyDTO.name());
        strategy.setDescription(strategyDTO.description());
        strategy.setTips(strategyDTO.tips());
        strategy.setExamples(strategyDTO.examples());

        return strategyRepository.save(strategy);
    }


    public void deleteById(UUID id) {
        if (!strategyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Estratégia", "id", id.toString());
        }

        strategyRepository.deleteById(id);
    }

    private List<Image> uploadImages(Strategy strategy, List<MultipartFile> imagesFiles) {
        List<Image> images = new ArrayList<>();

        imagesFiles.forEach(imageFile -> {
            if(imageFile != null && !imageFile.isEmpty()) {
                storageProvider.validateFile(imageFile);
                String imageUrl = storageProvider.store(imageFile);

                Image image = new Image();
                image.setUrl(imageUrl);
                image.setStrategy(strategy);
                imageRepository.save(image);

                images.add(image);
            }
        });

        return images;
    }
}
