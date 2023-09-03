package org.youcancook.gobong.domain.authentication.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    private Long userId;

    private String refreshToken;
    private Date expiredAt;

    @Builder
    public RefreshToken(Long userId, String refreshToken, Date expiredAt) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiredAt = expiredAt;
    }
}
