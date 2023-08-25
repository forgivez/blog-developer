package me.minsic.springbootdeveloper.repository;

import me.minsic.springbootdeveloper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /*
    * Java8에서는 Optional<T> 클래스를 사용해 NPE를 방지할 수 있도록 도와준다.
    * Optional<T>는 null이 올 수 있는 값을 감싸는 Wrapper 클래스
    * */
    Optional<User> findByEmail(String email);   // 현재 이메일이 pk처럼 사용
}
