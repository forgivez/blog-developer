package me.minsic.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.minsic.springbootdeveloper.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

/*
* @RequiredArgsConstructor는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해 줍니다.
* 새로운 필드를 추가할 때 다시 생성자를 만들어서 관리해야하는 번거로움을 없애준다
* */
@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userService;

    // 스프링 시큐리티 기능 비활성 (스프링 시큐리티 모든 기능을 비활성화)
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/static/**");
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests() // 인증, 인가 설정
                //requestMatchers() 특정 요청과 일치하는 url에 대한 엑세스 설정
                //permitAll() 누구나 접근이 가능하게 설정 ("/login", "/signup", "/user")의 요청은 인증/인가 없이 접근가능
                .requestMatchers("/login", "/signup", "/user").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()    // 폼 기반 로그인 설정 
                .loginPage("/login") // 로그인 페이지 경로 설정
                .defaultSuccessUrl("/articles") // 로그인이 완료되었을때 이동할 경로 설정
                .and()
                .logout()
                .logoutSuccessUrl("/login") // 로그아웃 설정 (로그아웃이 완료 되었을때 이동할 경로 설정
                .invalidateHttpSession(true) // 로그아웃 이후에 세션을 전체 삭제할지 여부를 설정
                .and()
                .csrf().disable() // csrf 비활성화
                .build();
    }

    //인증 관리자 관련 설정
    /*
    * 인증 관리자 관련 설정이며 사용자 정보를 가져올 서비스를 재정의 하거나,
    * 인증방법, LDAP,JDBC 기반 인증 등 설정할때 사용
    * */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService)
        throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService) // 사용자 정보 서비스 설정(사용자 서비스를 가져올 서비스를 설정한다. 이때 설정하는 서비스 클래스는 반드시 UserDetailService를 상속받은 클래스여야한다.)
                .passwordEncoder(bCryptPasswordEncoder) // 비밀번호를 암호화하기 위한 인코더를 설정
                .and()
                .build();
    }

    //패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
