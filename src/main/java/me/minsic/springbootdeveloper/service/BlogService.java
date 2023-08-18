package me.minsic.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.minsic.springbootdeveloper.domain.Article;
import me.minsic.springbootdeveloper.dto.AddArticleRequest;
import me.minsic.springbootdeveloper.dto.UpdateArticleRequest;
import me.minsic.springbootdeveloper.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;

@RequiredArgsConstructor    // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service // 빈으로 등록
public class BlogService {

    private final BlogRepository blogRepository;

    //블로그 글 추가 메서드
    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public void delete(long id) {
        blogRepository.deleteById(id);
    }
    
    @Transactional // 트랜젝션 메서드
    //Transactional 애너테이션은 매칭한 메서드를 하나의 트랜잭션으로 묶는 역할을 한다. 만약 중간에 에러가 발생하도 제대로 된 값수정 보장
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
        
        article.update(request.getTitle(), request.getContent());
        
        return article;
    }


}
