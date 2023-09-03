package org.youcancook.gobong.domain.authentication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.youcancook.gobong.domain.authentication.dto.request.ReissueTokenRequest;
import org.youcancook.gobong.domain.authentication.dto.response.ReissueTokenResponse;
import org.youcancook.gobong.domain.authentication.service.RefreshTokenService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthenticationController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("reissue")
    public ResponseEntity<ReissueTokenResponse> reissueToken(@RequestBody @Valid ReissueTokenRequest request) {
        ReissueTokenResponse response = refreshTokenService.reissueToken(request);
        return ResponseEntity.ok(response);
    }
}
