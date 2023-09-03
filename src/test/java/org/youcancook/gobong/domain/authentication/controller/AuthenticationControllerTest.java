package org.youcancook.gobong.domain.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.youcancook.gobong.domain.authentication.dto.request.ReissueTokenRequest;
import org.youcancook.gobong.domain.authentication.entity.RefreshToken;
import org.youcancook.gobong.domain.authentication.repository.RefreshTokenRepository;
import org.youcancook.gobong.domain.authentication.service.RefreshTokenService;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.repository.UserRepository;
import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.util.token.TokenDto;
import org.youcancook.gobong.global.util.token.TokenManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("리프레시 토큰 재발급 성공")
    void reissueSuccess() throws Exception {
        // given
        User loginUser = saveTestUser();
        TokenDto tokenDto = tokenManager.createTokenDto(loginUser.getId());
        refreshTokenService.saveRefreshToken(loginUser.getId(), tokenDto);

        // when
        ReissueTokenRequest loginRequest = new ReissueTokenRequest(tokenDto.getRefreshToken());
        String request = objectMapper.writeValueAsString(loginRequest);
        ResultActions resultActions = mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens).hasSize(1);

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grantType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").value(refreshTokens.get(0).getRefreshToken()));
    }

    @Test
    @DisplayName("리프레시 토큰 재발급 실패 - 존재하지 않는 토큰")
    void reissueFailByNotFound() throws Exception {
        // given
        User loginUser = saveTestUser();
        TokenDto tokenDto = tokenManager.createTokenDto(loginUser.getId());

        // expect
        ReissueTokenRequest loginRequest = new ReissueTokenRequest(tokenDto.getRefreshToken());
        String request = objectMapper.writeValueAsString(loginRequest);
        mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("리프레시 토큰 재발급 실패 - 요청 토큰이 유효하지 않음")
    void reissueFailByInvalidToken() throws Exception {
        // given
        User loginUser = saveTestUser();
        TokenDto tokenDto = tokenManager.createTokenDto(loginUser.getId());

        // expect
        ReissueTokenRequest loginRequest = new ReissueTokenRequest(tokenDto.getRefreshToken() + "wrong");
        String request = objectMapper.writeValueAsString(loginRequest);
        mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_TOKEN.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_TOKEN.getMessage()))
                .andDo(print());
    }

    private User saveTestUser() {
        User user = User.builder()
                .oAuthId("12345678")
                .oAuthProvider(OAuthProvider.KAKAO)
                .nickname("nickname")
                .profileImageURL("profileImageURL")
                .build();
        return userRepository.save(user);
    }
}