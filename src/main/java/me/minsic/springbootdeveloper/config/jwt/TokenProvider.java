package me.minsic.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import me.minsic.springbootdeveloper.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static io.jsonwebtoken.Jwts.parser;

@RequiredArgsConstructor // 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해 줍니다.
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);

    }
    /*
    * 토큰을 생성하는 메서드
    * 인자는 만료시간, 유저정보를 받는다.
    * 이 메서드에서는 set 계열의 메서드를 통해 여러 값을 지정한다.
    * 헤더는 typ(타입), 내용은 iss(발급자), iat(발급일시), exp(만요일시), sub(토큰 제목)
    * 클레임에는 유저 ID를 지정한다.
    * 토큰을 만들 때는 프로퍼티즈 파일에 선언해준 비밀값과 함께 HS256 방식으로 암호화한다.
    * */
    // JWT 토근 생성 메서드
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ : JWT
                .setIssuer(jwtProperties.getIssuer()) // 내용 iss : forgivez@naver.com (propertise에서 설정한 값)
                .setIssuedAt(now)   // 내용 iat : 현재시간
                .setExpiration(expiry) // 내용 exp : expiry 멤버 변숫값
                .setSubject(user.getEmail()) // 내용 sub : 유저의 이메일
                .claim("id", user.getId())  // 클레임 id : 유저의 Id
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 서명 : 비밀값과 함께 해시값을 HS256 방식으로 암호화
                .compact();
    }

    /*
    * 토큰이 유효한지 검증하는 메소드이다.
    * 프로퍼티즈 파일에 선언해둔 비밀값과 함게 토큰 복호화를 진행한다. 만약 복호화 과정에서 에러가 발생하면
    * 유효하지 않은 토큰이므로 false를 반환하고 아무 에러도 발생하지 않으면 true를 반환한다.
    * */
    //JWT 토근 유효성 검증 메소드
    public boolean validToken(String token) {
        try {
            parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 비밀값으로 복호화
                    .parseClaimsJws(token);
                    return true;
        } catch (Exception e) { // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
            return false;
        }
    }

    /*
    * 토큰을 받아 인증 정볼르 받은 객체 AuthenticationToken을 반환하는메서드
    * 프로퍼티스 파일에 저장한 비밀값으로 토큰을 복호화 한 뒤 클레임을 가져오는 getClaims()을 호출해서
    * 클레임 정보를 반환받아 사용자 이메일이 들어있는 토큰 제목 sub와 토큰 기반으로 인증정보를 생성한다.
    * 이때 UsernamePasswordAuthenticationToken의 첫 인자로 들어가는 User는 프로젝트에서 만든 User가 아니라
    * org.springframework.security.core.userdetails.User에서 제공하는 객체인 User 클래스를 임포트해야한다.
    * */
    //토근 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject(),
                "", authorities), token, authorities);
    }

    /*
    * 토큰 기반으로 사용자 ID를 가져오는 메소드 이다. 프로퍼티즈 파일에 저장한 비밀값으로 토큰을 복호화 뒤 다음 클레임을 가져오는
    * private 메서드은 getClaims()을 호출해서 클레임 정보를 반환받고 클레임 id 키로 지정된 값을 가져와 반환한다.
    * */
    //토근 기반으로 유저 ID를 가져오는 메소드
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser() // 클레임 조회
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}

