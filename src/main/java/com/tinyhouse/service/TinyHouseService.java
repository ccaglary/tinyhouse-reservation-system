package com.tinyhouse.service;

import com.tinyhouse.dto.request.TinyHouseRequest;
import com.tinyhouse.dto.response.TinyHouseResponse;
import com.tinyhouse.entity.TinyHouse;
import com.tinyhouse.entity.TinyHouseImage;
import com.tinyhouse.entity.User;
import com.tinyhouse.exception.BusinessException;
import com.tinyhouse.exception.ResourceNotFoundException;
import com.tinyhouse.mapper.TinyHouseMapper;
import com.tinyhouse.repository.TinyHouseImageRepository;
import com.tinyhouse.repository.TinyHouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TinyHouseService {
    private final TinyHouseRepository tinyHouseRepository;
    private final TinyHouseImageRepository imageRepository;
    private final TinyHouseMapper tinyHouseMapper;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public Page<TinyHouseResponse> getAllActive(Pageable pageable) {
        return tinyHouseRepository.findByActiveTrueAndDeletedFalse(pageable).map(tinyHouseMapper::toResponse);
    }

    public TinyHouseResponse getById(Long id) {
        return tinyHouseMapper.toResponse(tinyHouseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("TinyHouse", "id", id)));
    }

    public Page<TinyHouseResponse> getByOwner(Long ownerId, Pageable pageable) {
        return tinyHouseRepository.findByOwnerIdAndDeletedFalse(ownerId, pageable).map(tinyHouseMapper::toResponse);
    }

    public List<String> getAllCities() {
        return tinyHouseRepository.findDistinctCities();
    }

    public List<TinyHouseResponse> getTopRated(int limit) {
        return tinyHouseRepository.findTopRated(PageRequest.of(0, limit)).stream()
                .map(tinyHouseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<TinyHouseResponse> search(String city, BigDecimal minPrice, BigDecimal maxPrice,
                                          Integer capacity, Boolean wifi, Boolean parking,
                                          Boolean petAllowed, Pageable pageable) {
        return tinyHouseRepository.search(city, minPrice, maxPrice, capacity, wifi, parking, petAllowed, pageable)
                .map(tinyHouseMapper::toResponse);
    }

    public Page<TinyHouseResponse> searchByKeyword(String keyword, Pageable pageable) {
        return tinyHouseRepository.searchByKeyword(keyword, pageable).map(tinyHouseMapper::toResponse);
    }

    @Transactional
    public TinyHouseResponse create(TinyHouseRequest request, String ownerEmail) {
        User owner = userService.findUserByEmail(ownerEmail);
        TinyHouse house = TinyHouse.builder()
                .title(request.getTitle()).description(request.getDescription())
                .city(request.getCity()).district(request.getDistrict()).address(request.getAddress())
                .latitude(request.getLatitude()).longitude(request.getLongitude())
                .nightlyPrice(request.getNightlyPrice())
                .cleaningFee(request.getCleaningFee() != null ? request.getCleaningFee() : BigDecimal.ZERO)
                .capacity(request.getCapacity())
                .roomCount(request.getRoomCount() != null ? request.getRoomCount() : 1)
                .bedCount(request.getBedCount() != null ? request.getBedCount() : 1)
                .bathroomCount(request.getBathroomCount() != null ? request.getBathroomCount() : 1)
                .wifi(request.isWifi()).parking(request.isParking())
                .airConditioner(request.isAirConditioner()).petAllowed(request.isPetAllowed())
                .active(true).owner(owner).build();
        house = tinyHouseRepository.save(house);
        log.info("TinyHouse created: {} by {}", house.getId(), ownerEmail);
        return tinyHouseMapper.toResponse(house);
    }

    @Transactional
    public TinyHouseResponse update(Long id, TinyHouseRequest request, String ownerEmail) {
        TinyHouse house = tinyHouseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("TinyHouse", "id", id));
        if (!house.getOwner().getEmail().equals(ownerEmail))
            throw new BusinessException("Bu evi düzenleme yetkiniz yok");
        house.setTitle(request.getTitle()); house.setDescription(request.getDescription());
        house.setCity(request.getCity()); house.setDistrict(request.getDistrict());
        house.setAddress(request.getAddress()); house.setNightlyPrice(request.getNightlyPrice());
        house.setCapacity(request.getCapacity()); house.setWifi(request.isWifi());
        house.setParking(request.isParking()); house.setAirConditioner(request.isAirConditioner());
        house.setPetAllowed(request.isPetAllowed());
        return tinyHouseMapper.toResponse(tinyHouseRepository.save(house));
    }

    @Transactional
    public void uploadImages(Long id, MultipartFile[] files, String ownerEmail) {
        TinyHouse house = tinyHouseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("TinyHouse", "id", id));
        if (!house.getOwner().getEmail().equals(ownerEmail))
            throw new BusinessException("Yetkiniz yok");
        boolean first = house.getImages().isEmpty();
        for (MultipartFile file : files) {
            String url = fileStorageService.storeFile(file, "houses/" + id);
            TinyHouseImage img = TinyHouseImage.builder().imageUrl(url).tinyHouse(house).coverImage(first).build();
            imageRepository.save(img); first = false;
        }
    }

    @Transactional
    public void deleteImage(Long imageId, String ownerEmail) {
        TinyHouseImage img = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
        if (!img.getTinyHouse().getOwner().getEmail().equals(ownerEmail))
            throw new BusinessException("Yetkiniz yok");
        fileStorageService.deleteFile(img.getImageUrl()); imageRepository.delete(img);
    }

    @Transactional
    public TinyHouseResponse toggleActive(Long id) {
        TinyHouse house = tinyHouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TinyHouse", "id", id));
        house.setActive(!house.isActive());
        return tinyHouseMapper.toResponse(tinyHouseRepository.save(house));
    }

    @Transactional
    public void softDelete(Long id) {
        TinyHouse house = tinyHouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TinyHouse", "id", id));
        house.setDeleted(true); tinyHouseRepository.save(house);
    }
}
