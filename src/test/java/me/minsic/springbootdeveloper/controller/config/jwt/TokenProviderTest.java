package me.minsic.springbootdeveloper.controller.config.jwt;

import io.jsonwebtoken.Jwts;
import me.minsic.springbootdeveloper.config.jwt.JwtProperties;
import me.minsic.springbootdeveloper.config.jwt.TokenProvider;
import me.minsic.springbootdeveloper.domain.User;
import me.minsic.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

@SpringBootTest
public class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    // generateToken() 검증 테스트
    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        /*
        * 토큰에 유저 정보를 추가하기 위한 테스트 유저를 만든다.
        * */
        //given
        User testUser = userRepository.save(User.builder()
                .email("user@gamil.com")
                .password("test")
                .build());
        /*
         * 토큰 제공자의 generateToken() 메서드를 호출해 토큰을 만든다.
         * */
        //when
        String toekn = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        /*
         * jjwt 라이브러리를 사용해 토큰을 복호화한다. 토큰을 만들 때 클레임으로 넣어둔 id값이 given절에서 만든
         * 유저 ID와 동일한지 확인한다.
         * */
        //then
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(toekn)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    // validToken() 검증 테스트
    @DisplayName("validToken(): 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {

        /*
        * jjwt라이브러리를 사용해 토큰을 생성한다. 이때 만료 시간은 1970년 1월 1일부터 현재 시간을
        * 밀리초 단위로 치환한 값(new Date().getTime()에 1000을 빼, 이미 만료된 토큰으로 생성한다.)
        * */
        //given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);
        /*
         * 토큰 제공자의 vaildToken() 메소드를 호출해 유효한 토큰인지 검증한뒤 결과값을 반환받는다.
         * */
        // when
        boolean result = tokenProvider.validToken(token);

        /*
         * 반환값이 fase(유효한 토큰이 아님)인 것을 확인한다.
         * */
        // then
        assertThat(result).isFalse();
    }

    @DisplayName("validToken(): 유효한 토큰인 때에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {

        /*
         * jjwt 라이브러리를 사용해 토큰을 생성한다. 만료 시간은 혀재 시간으로부터 14일 뒤로, 만료되지 않은 토큰으로 생성한다.
         * */
        //given
        String token = JwtFactory.withDefaultValues()
                .createToken(jwtProperties);

        /*
         * 토큰 제공자의 vaildToken() 메서드를 호출해 유효한 토큰인지 검증한 뒤 결과값을 반환받는다.
         * */
        //when
        boolean result = tokenProvider.validToken(token);

        /*
         * 반환값이 true(유효한 토큰임)인 것을 확인한다.
         * */
        //then
        assertThat(result).isTrue();
    }

    //getAuthentication() 검증 테스트
    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다")
    @Test
    void getAuthentication() {
        /*
         * jjwt 라이브러리를 사용해 토큰을 생성합니다. 이때 토큰의 제목인 subject는 "user@naver.com"라는 값을 사용한다.
         * */
        //given
        String userEamil = "user@naver.com";
        String token = JwtFactory.builder()
                .subject(userEamil)
                .build()
                .createToken(jwtProperties);

        /*
         * 토큰 제공자의 getAuthentication() 메서드를 호출해 인증 객체를 반환받는다.
         * */
        //when
        Authentication authenticator = tokenProvider.getAuthentication(token);

        /*
         * 반환받은 인증 객체의 유저 이름을 가져와 given절에서 설정한 subject값인 "user@naver.com"과 같은지 확인한다.
         * */
        //then
        assertThat(((UserDetails) authenticator.getPrincipal()).getUsername()).isEqualTo(userEamil);
    }

    //getUserId() 검증 테스트
    @DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        /*
         * jjwt 라이브러리를 사용해 토큰을 생성한다. 이때 클레임을 추가한다.
         * 키는 "id", 값은 1이라는 유저의 ID
         * */
        //given
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id",userId))
                .build()
                .createToken(jwtProperties);

        /*
         * 토큰 제공자의 getUserId() 메서드를 호출해 유저 ID를 반환받는다.
         * */
        //when
        Long userIdByToken = tokenProvider.getUserId(token);

        /*
         * 반환받은 유저 ID가 given절에서 설정한 유저 ID값인 1과 같은지 확인한다.
         * */
        //then
        assertThat(userIdByToken).isEqualTo(userId);
    }


}
