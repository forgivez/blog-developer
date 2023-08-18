package me.minsic.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.minsic.springbootdeveloper.domain.Article;
import me.minsic.springbootdeveloper.dto.AddArticleRequest;
import me.minsic.springbootdeveloper.dto.ArticleResponse;
import me.minsic.springbootdeveloper.dto.UpdateArticleRequest;
import me.minsic.springbootdeveloper.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController // HTTP Response Body에 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
public class BlogApiController {

    private final BlogService blogService;

    // HTTP 메서드가 POST일 때 전달받은 URL과 동일하면 메서드로 매핑
    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request) {
        Article saveArticle = blogService.save(request);
        // body()는 응답코드로 201을 응답하고 테이블에 저장된 객체를 반환한다.
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saveArticle);
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                // 더블콜론의 의미(::) 람다의 간결한 버전중 하나,
                // 왼쪽 객체의 오른쪽 메소드를 사용한다는 내용.
                // :: 는 스트림을 사용할 경우 객체에서 특정 메소드를 호출하는 용도로 사용
                .map(ArticleResponse::new)
                .toList();

        // 찍어보기(삭제예정)
        for(ArticleResponse e : articles) {
            System.out.println(e.getContent()+" "+ e.getTitle());
        }
        return ResponseEntity.ok()
                .body(articles);
    }

    @GetMapping("/api/articles/{id}")
    // URL 경로에서 값 추출
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable Long id){ // @PathVariable 애너테이션은 URL에서 값을 가져오는 애너테이션
        Article article = blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id,
                                                 @RequestBody UpdateArticleRequest request) {
        Article updateArticle = blogService.update(id,request);

        return ResponseEntity.ok()
                .body(updateArticle);
    }
}
