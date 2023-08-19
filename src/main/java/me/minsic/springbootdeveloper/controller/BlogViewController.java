package me.minsic.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.minsic.springbootdeveloper.dto.ArticleListViewResponse;
import me.minsic.springbootdeveloper.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

//RequiredArgsConstructor는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해 줍니다.
@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles = blogService.findAll().stream()
                .map(ArticleListViewResponse::new)
                .toList();
        model.addAttribute("articles",articles);    // 블로그 글 리스트 저장

        return "articlesList";  // articleList.html라는 뷰 조회
    }
}
