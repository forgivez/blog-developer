package me.minsic.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter // getter 생성 필요없이 lombok으로 해결
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 접근 제어자가 protected 기본 생성자를 별도의 코드없이 생성 
public class Article {

    @Id // id 필드를 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키를 자동으로 1씩 추가
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false) // 'title' 이라는 not null 컬럼과 매핑
    private String title;

    @Column(name = "content", nullable = false) // 'content' 이라는 not null 컬럼과 매핑
    private String content;

    /* Builder 패턴
    * 롬북에서 지원
    * 빌더 패턴을 사용하면 객체를 유연하고 직관적으로 생성
    * 예시 빌더패턴 사용시
    * Article.builder()
    *   .title("abc")
    *   .content("def")
    *   .build();
    *
    * 빌더 패턴 미사용
    * new Article("abc","def");
    *  */
    @Builder // 빌더 패턴으로 객체 생성
    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }

//    //게터
//    public Long getId() { return id; }
//    public String getTitle() {return title;}
//    public String getContent() {return content;}
}
