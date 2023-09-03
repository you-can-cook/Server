package org.youcancook.gobong.domain.authentication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.youcancook.gobong.domain.authentication.entity.TemporaryToken;
import org.youcancook.gobong.domain.authentication.exception.TemporaryTokenNotFoundException;
import org.youcancook.gobong.domain.authentication.repository.TemporaryTokenRepository;
import org.youcancook.gobong.global.util.clock.ClockService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemporaryTokenServiceTest {

    @InjectMocks
    private TemporaryTokenService temporaryTokenService;

    @Mock
    private ClockService clockService;

    @Mock
    private TemporaryTokenRepository temporaryTokenRepository;

    @BeforeEach
    void mockingClock() {
        final String localDateTimeOnTestEnv = "2023-07-16T10:15:30";
        when(clockService.getCurrentDateTime())
                .thenReturn(LocalDateTime.parse(localDateTimeOnTestEnv));
    }

    @Test
    @DisplayName("토큰 생성")
    void saveTemporaryToken() {
        // given
        when(temporaryTokenRepository.save(any(TemporaryToken.class)))
                .thenReturn(new TemporaryToken("token", LocalDateTime.now()));
        ReflectionTestUtils.setField(temporaryTokenService, "temporaryTokenExpirationSeconds", 600);

        // when
        String token = temporaryTokenService.saveTemporaryToken();

        // then
        String UUIDRegex = "[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}";
        assertThat(token).matches(UUIDRegex);

        ArgumentCaptor<TemporaryToken> argument = ArgumentCaptor.forClass(TemporaryToken.class);
        verify(temporaryTokenRepository, times(1)).save(argument.capture());
        assertThat(argument.getValue().getToken()).isEqualTo(token);

        LocalDateTime expectedExpiredAt = clockService.getCurrentDateTime().plusSeconds(600);
        assertThat(argument.getValue().getExpiredAt()).isEqualTo(expectedExpiredAt);
    }

    @Test
    @DisplayName("토큰 찾기 실패")
    void findTemporaryTokenFail() {
        // given
        final String token = UUID.randomUUID().toString();
        when(temporaryTokenRepository.findByTokenAndExpiredAtAfter(token, clockService.getCurrentDateTime()))
                .thenReturn(Optional.empty());

        // when, then
        assertThrows(TemporaryTokenNotFoundException.class,
                () -> temporaryTokenService.findTemporaryTokenId(token));

        verify(temporaryTokenRepository, times(1))
                .findByTokenAndExpiredAtAfter(token, clockService.getCurrentDateTime());
    }

    @Test
    @DisplayName("토큰 찾기 성공")
    void findTemporaryTokenSuccess() {
        // given
        final String token = UUID.randomUUID().toString();
        TemporaryToken temporaryToken = new TemporaryToken(token, clockService.getCurrentDateTime().plusMinutes(10));
        ReflectionTestUtils.setField(temporaryToken, "id", 1L);
        when(temporaryTokenRepository.findByTokenAndExpiredAtAfter(token, clockService.getCurrentDateTime()))
                .thenReturn(Optional.of(temporaryToken));

        // when
        Long temporaryTokenId = temporaryTokenService.findTemporaryTokenId(token);

        // then
        assertThat(temporaryTokenId).isEqualTo(temporaryToken.getId());
        verify(temporaryTokenRepository, times(1))
                .findByTokenAndExpiredAtAfter(token, clockService.getCurrentDateTime());
    }

    @Test
    @DisplayName("토큰 삭제")
    void deleteTemporaryToken() {
        // given
        final String token = UUID.randomUUID().toString();
        TemporaryToken temporaryToken = new TemporaryToken(token, clockService.getCurrentDateTime());
        ReflectionTestUtils.setField(temporaryToken, "id", 1L);
        doNothing().when(temporaryTokenRepository).deleteById(temporaryToken.getId());

        // when
        temporaryTokenService.deleteTemporaryToken(temporaryToken.getId());

        // then
        verify(temporaryTokenRepository, times(1))
                .deleteById(temporaryToken.getId());
    }
}