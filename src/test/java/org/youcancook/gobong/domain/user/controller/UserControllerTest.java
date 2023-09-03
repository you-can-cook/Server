package org.youcancook.gobong.domain.user.controller;

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
import org.youcancook.gobong.domain.authentication.entity.RefreshToken;
import org.youcancook.gobong.domain.authentication.entity.TemporaryToken;
import org.youcancook.gobong.domain.authentication.repository.RefreshTokenRepository;
import org.youcancook.gobong.domain.authentication.repository.TemporaryTokenRepository;
import org.youcancook.gobong.domain.authentication.service.TemporaryTokenService;
import org.youcancook.gobong.domain.user.dto.request.LoginRequest;
import org.youcancook.gobong.domain.user.dto.request.SignupRequest;
import org.youcancook.gobong.domain.user.dto.request.UpdateUserInformationRequest;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.repository.UserRepository;
import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.util.token.TokenDto;
import org.youcancook.gobong.global.util.token.TokenManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TemporaryTokenRepository temporaryTokenRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TemporaryTokenService temporaryTokenService;

    @Autowired
    private TokenManager tokenManager;

    private static Long userId = 1L;

    @Test
    @DisplayName("임시 토큰 발급")
    void getTemporaryToken() throws Exception {
        // when
        ResultActions resultActions =
                mockMvc.perform(post("/api/users/temporary-token")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print());

        List<TemporaryToken> temporaryTokens = temporaryTokenRepository.findAll();
        assertThat(temporaryTokens).hasSize(1);
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.temporaryToken").value(temporaryTokens.get(0).getToken()));
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() throws Exception {
        // given
        String temporaryToken = temporaryTokenService.saveTemporaryToken();
        User user = saveTestUser();

        // when
        LoginRequest loginRequest = new LoginRequest(user.getOAuthProvider().name(), user.getOAuthId(), temporaryToken);
        String request = objectMapper.writeValueAsString(loginRequest);
        ResultActions resultActions =
                mockMvc.perform(post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andDo(print());

        // then
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens).hasSize(1);

        List<TemporaryToken> temporaryTokens = temporaryTokenRepository.findAll();
        assertThat(temporaryTokens).hasSize(0);

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grantType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").value(refreshTokens.get(0).getRefreshToken()));
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 oAuthProvider")
    void loginFailByOAuthProvider() throws Exception {
        // given
        String temporaryToken = temporaryTokenService.saveTemporaryToken();
        User user = saveTestUser();

        // expect
        LoginRequest loginRequest = new LoginRequest(OAuthProvider.GOOGLE.name(), user.getOAuthId(), temporaryToken);
        String request = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
                .andDo(print());

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens).hasSize(0);
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 oAuthId")
    void loginFailByOAuthId() throws Exception {
        // given
        String temporaryToken = temporaryTokenService.saveTemporaryToken();
        User user = saveTestUser();

        // expect
        LoginRequest loginRequest = new LoginRequest(user.getOAuthProvider().name(), user.getOAuthId() + "1", temporaryToken);
        String request = objectMapper.writeValueAsString(loginRequest);
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
                .andDo(print());

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens).hasSize(0);
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 temporaryToken")
    void loginFailByTemporaryToken() throws Exception {
        // given
        User user = saveTestUser();

        // expect
        LoginRequest loginRequest = new LoginRequest(user.getOAuthProvider().name(), user.getOAuthId(), "notFoundTemporaryToken");
        String request = objectMapper.writeValueAsString(loginRequest);
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.TEMPORARY_TOKEN_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.TEMPORARY_TOKEN_NOT_FOUND.getMessage()))
                .andDo(print());

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens).hasSize(0);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() throws Exception {
        // given
        String temporaryToken = temporaryTokenService.saveTemporaryToken();

        // when
        SignupRequest signupRequest = new SignupRequest("nickname", "KAKAO", "oauthId", temporaryToken, "profileImageURL");
        String request = objectMapper.writeValueAsString(signupRequest);
        ResultActions resultActions =
                mockMvc.perform(post("/api/users/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andDo(print());

        // then
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens).hasSize(1);

        List<TemporaryToken> temporaryTokens = temporaryTokenRepository.findAll();
        assertThat(temporaryTokens).hasSize(0);

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        User user = users.get(0);
        assertThat(user.getNickname()).isEqualTo(signupRequest.getNickname());
        assertThat(user.getProfileImageURL()).isEqualTo(signupRequest.getProfileImageURL());
        assertThat(user.getOAuthProvider()).isEqualTo(OAuthProvider.KAKAO);
        assertThat(user.getOAuthId()).isEqualTo(signupRequest.getOauthId());

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grantType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").value(refreshTokens.get(0).getRefreshToken()));
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임은 한글, 영어, 숫자로 10자 이내만 가능")
    void signupFailByNickname() throws Exception {
        List<String> failNicknames = List.of(" ", "", "\t", "\n", "@", "ab ", " ab", "a b",
                "12345678910", "abcdefghijh", "가나다라마바사아자차카", "ㄱㄴㄷ");

        for (String failNickname : failNicknames) {
            SignupRequest signupRequest
                    = new SignupRequest(failNickname, "KAKAO", "oauthId", "temporaryToken", "profileImageURL");
            String request = objectMapper.writeValueAsString(signupRequest);
            mockMvc.perform(post("/api/users/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_VALUE.getCode()))
                    .andExpect(jsonPath("$.message").value("닉네임은 한글, 영어, 숫자로 10자 이내만 가능합니다."))
                    .andDo(print());
        }
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 닉네임")
    void signupFailByDuplicationNickname() throws Exception {
        // given
        String temporaryToken = temporaryTokenService.saveTemporaryToken();
        User savedUser = saveTestUser();

        // when
        SignupRequest signupRequest = new SignupRequest(savedUser.getNickname(), "KAKAO", "oauthId", temporaryToken, "profileImageURL");
        String request = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_NICKNAME.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_NICKNAME.getMessage()))
                .andDo(print());

        List<TemporaryToken> temporaryTokens = temporaryTokenRepository.findAll();
        assertThat(temporaryTokens).hasSize(1);
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 유저")
    void signupFailByExitUser() throws Exception {
        // given
        String temporaryToken = temporaryTokenService.saveTemporaryToken();
        User savedUser = saveTestUser();

        // when
        SignupRequest signupRequest = new SignupRequest("notExits", savedUser.getOAuthProvider().name(), savedUser.getOAuthId(), temporaryToken, "profileImageURL");
        String request = objectMapper.writeValueAsString(signupRequest);
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_ALREADY_EXISTS.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_ALREADY_EXISTS.getMessage()))
                .andDo(print());

        List<TemporaryToken> temporaryTokens = temporaryTokenRepository.findAll();
        assertThat(temporaryTokens).hasSize(1);
    }

    @Test
    @DisplayName("내 정보 조회")
    void getMyInformation() throws Exception {
        // given
        User savedUser = saveTestUser();
        TokenDto tokenDto = tokenManager.createTokenDto(savedUser.getId());

        // when
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenDto.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(savedUser.getNickname()))
                .andExpect(jsonPath("$.profileImageURL").value(savedUser.getProfileImageURL()))
                .andExpect(jsonPath("$.oAuthProvider").value(savedUser.getOAuthProvider().name()))
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("다른 회원정보 조회")
    void getUserInformation() throws Exception {
        // given
        User savedUser = saveTestUser();
        TokenDto tokenDto = tokenManager.createTokenDto(savedUser.getId());
        User requestUser = saveTestUser();

        // when
        mockMvc.perform(get("/api/users/" + requestUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenDto.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(requestUser.getNickname()))
                .andExpect(jsonPath("$.profileImageURL").value(requestUser.getProfileImageURL()))
                .andExpect(jsonPath("$.oAuthProvider").value(requestUser.getOAuthProvider().name()))
                .andExpect(jsonPath("$.id").value(requestUser.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원정보 수정 성공")
    void updateInformationSuccess() throws Exception {
        // given
        User savedUser = saveTestUser();
        TokenDto tokenDto = tokenManager.createTokenDto(savedUser.getId());

        // when
        UpdateUserInformationRequest updateRequest =
                new UpdateUserInformationRequest("newNick", "newProfileImageURL");
        String request = objectMapper.writeValueAsString(updateRequest);
        mockMvc.perform(patch("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenDto.getAccessToken())
                        .content(request))
                .andExpect(status().isOk())
                .andDo(print());

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        User user = users.get(0);
        assertThat(user.getNickname()).isEqualTo(updateRequest.getNickname());
        assertThat(user.getProfileImageURL()).isEqualTo(updateRequest.getProfileImageURL());
    }

    @Test
    @DisplayName("회원정보 수정 실패 - 중복된 닉네임")
    void updateInformationFailByDuplicatedNickname() throws Exception {
        // given
        userRepository.save(User.builder()
                .oAuthId("987654321")
                .oAuthProvider(OAuthProvider.GOOGLE)
                .nickname("newNick")
                .profileImageURL("imageURL")
                .build());
        User savedUser = saveTestUser();
        TokenDto tokenDto = tokenManager.createTokenDto(savedUser.getId());

        // when
        UpdateUserInformationRequest updateRequest =
                new UpdateUserInformationRequest("newNick", "newProfileImageURL");
        String request = objectMapper.writeValueAsString(updateRequest);
        mockMvc.perform(patch("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenDto.getAccessToken())
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_NICKNAME.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_NICKNAME.getMessage()))
                .andDo(print());
    }

    private User saveTestUser() {
        User user = User.builder()
                .oAuthId("12345678" + userId)
                .oAuthProvider(OAuthProvider.KAKAO)
                .nickname("nickname" + userId)
                .profileImageURL("profileImageURL" + userId)
                .build();
        userId += 1;
        return userRepository.save(user);
    }
}