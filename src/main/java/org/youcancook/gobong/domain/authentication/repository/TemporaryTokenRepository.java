package org.youcancook.gobong.domain.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.youcancook.gobong.domain.authentication.entity.TemporaryToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TemporaryTokenRepository extends JpaRepository<TemporaryToken, Long> {
    Optional<TemporaryToken> findByTokenAndExpiredAtAfter(String token, LocalDateTime expiredAt);
}
