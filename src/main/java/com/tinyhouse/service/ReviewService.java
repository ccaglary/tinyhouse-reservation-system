package com.tinyhouse.service;

import com.tinyhouse.dto.request.ReviewRequest;
import com.tinyhouse.dto.response.ReviewResponse;
import com.tinyhouse.entity.Review;
import com.tinyhouse.entity.TinyHouse;
import com.tinyhouse.entity.User;
import com.tinyhouse.enums.NotificationType;
import com.tinyhouse.exception.BusinessException;
import com.tinyhouse.exception.ResourceNotFoundException;
import com.tinyhouse.mapper.ReviewMapper;
import com.tinyhouse.repository.ReviewRepository;
import com.tinyhouse.repository.TinyHouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final TinyHouseRepository tinyHouseRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public ReviewResponse create(ReviewRequest request, String tenantEmail) {
        User tenant = userService.findUserByEmail(tenantEmail);
        TinyHouse house = tinyHouseRepository.findByIdAndDeletedFalse(request.getTinyHouseId())
                .orElseThrow(() -> new ResourceNotFoundException("TinyHouse", "id", request.getTinyHouseId()));

        if (reviewRepository.existsByTenantIdAndTinyHouseIdAndDeletedFalse(tenant.getId(), house.getId())) {
            throw new BusinessException("Bu ev için zaten yorum yapmışsınız");
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .tinyHouse(house)
                .tenant(tenant)
                .build();

        review = reviewRepository.save(review);

        BigDecimal avg = reviewRepository.calculateAverageRating(house.getId());
        if (avg != null) {
            house.setAverageRating(avg);
            tinyHouseRepository.save(house);
        }

        notificationService.createNotification(house.getOwner().getId(),
                "Yeni Yorum", tenant.getFirstName() + " " + house.getTitle() + " için yorum bıraktı.",
                NotificationType.REVIEW_RECEIVED);

        log.info("Review created for house {} by {}", house.getId(), tenantEmail);
        return reviewMapper.toResponse(review);
    }

    public Page<ReviewResponse> getByTinyHouse(Long tinyHouseId, Pageable pageable) {
        return reviewRepository.findByTinyHouseIdAndDeletedFalse(tinyHouseId, pageable)
                .map(reviewMapper::toResponse);
    }

    public Page<ReviewResponse> getByTenant(String tenantEmail, Pageable pageable) {
        User tenant = userService.findUserByEmail(tenantEmail);
        return reviewRepository.findByTenantIdAndDeletedFalse(tenant.getId(), pageable)
                .map(reviewMapper::toResponse);
    }

    @Transactional
    public ReviewResponse replyToReview(Long reviewId, String reply, String ownerEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getTinyHouse().getOwner().getEmail().equals(ownerEmail)) {
            throw new BusinessException("Bu yoruma yanıt verme yetkiniz yok");
        }

        review.setOwnerReply(reply);
        review = reviewRepository.save(review);
        log.info("Owner replied to review {}", reviewId);
        return reviewMapper.toResponse(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        review.setDeleted(true);
        reviewRepository.save(review);

        BigDecimal avg = reviewRepository.calculateAverageRating(review.getTinyHouse().getId());
        TinyHouse house = review.getTinyHouse();
        house.setAverageRating(avg != null ? avg : BigDecimal.ZERO);
        tinyHouseRepository.save(house);
    }
}
