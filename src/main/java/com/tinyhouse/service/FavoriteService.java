package com.tinyhouse.service;

import com.tinyhouse.dto.response.TinyHouseResponse;
import com.tinyhouse.entity.Favorite;
import com.tinyhouse.entity.TinyHouse;
import com.tinyhouse.entity.User;
import com.tinyhouse.exception.ResourceNotFoundException;
import com.tinyhouse.mapper.TinyHouseMapper;
import com.tinyhouse.repository.FavoriteRepository;
import com.tinyhouse.repository.TinyHouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final TinyHouseRepository tinyHouseRepository;
    private final TinyHouseMapper tinyHouseMapper;
    private final UserService userService;

    @Transactional
    public boolean toggleFavorite(String userEmail, Long tinyHouseId) {
        User user = userService.findUserByEmail(userEmail);
        TinyHouse house = tinyHouseRepository.findByIdAndDeletedFalse(tinyHouseId)
                .orElseThrow(() -> new ResourceNotFoundException("TinyHouse", "id", tinyHouseId));

        var existing = favoriteRepository.findByUserIdAndTinyHouseId(user.getId(), tinyHouseId);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            log.info("Favorite removed: user={}, house={}", user.getId(), tinyHouseId);
            return false;
        } else {
            Favorite fav = Favorite.builder().user(user).tinyHouse(house).build();
            favoriteRepository.save(fav);
            log.info("Favorite added: user={}, house={}", user.getId(), tinyHouseId);
            return true;
        }
    }

    public Page<TinyHouseResponse> getUserFavorites(String userEmail, Pageable pageable) {
        User user = userService.findUserByEmail(userEmail);
        return favoriteRepository.findByUserIdAndDeletedFalse(user.getId(), pageable)
                .map(fav -> tinyHouseMapper.toResponse(fav.getTinyHouse()));
    }

    public boolean isFavorite(String userEmail, Long tinyHouseId) {
        User user = userService.findUserByEmail(userEmail);
        return favoriteRepository.existsByUserIdAndTinyHouseIdAndDeletedFalse(user.getId(), tinyHouseId);
    }
}
