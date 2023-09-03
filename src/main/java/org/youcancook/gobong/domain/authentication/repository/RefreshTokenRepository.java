package org.youcancook.gobong.domain.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.youcancook.gobong.domain.authentication.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long userId);
}
