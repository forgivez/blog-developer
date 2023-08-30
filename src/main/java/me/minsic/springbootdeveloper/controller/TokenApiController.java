package me.minsic.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.minsic.springbootdeveloper.config.jwt.TokenProvider;
import me.minsic.springbootdeveloper.dto.CreateAccessTokenRequest;
import me.minsic.springbootdeveloper.dto.CreateAccessTokenResponse;
import me.minsic.springbootdeveloper.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {
    private final TokenService tokenService;

    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken (@RequestBody CreateAccessTokenRequest request){
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}
