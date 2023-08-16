package me.minsic.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.minsic.springbootdeveloper.domain.Article;
import me.minsic.springbootdeveloper.dto.AddArticleRequest;
import me.minsic.springbootdeveloper.repository.BlogRepository;
import me.minsic.springbootdeveloper.service.BlogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest // 테스트용 애플리케이션 컨텍스트
@AutoConfigureMockMvc   // MockMvc 생성 및 자동 구성
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    /* objcetMapper 클래스 설명
    * 이 클래스로 만든 자바 객체를 JSON 데이터로 변환하는 직렬화 또는
    * 반대로 JSON 데이터를 자바에서 사용하기 위해서 객체로 변환하는 역직렬화 할때 ㅅ용
    *  */
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        blogRepository.deleteAll();
    }

    @DisplayName("addArtictle: 블로그 글 추가에 성공한다")
    @Test
    public void addArticle() throws Exception {
        //given
        final String url = "/api/articles";
        final String title = "제목";
        final String content = "컨텐츠 글적기";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);
        System.out.println(userRequest.getContent());

        //객체 JSOM으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);
        System.out.println(requestBody.toString());

        // when
        /* perform에 대한 설명
        * perform에 요청 설정 메서드를 통해성 요청에 대한 설정을 할 수 있다.
        * perform에 Expect 메서드를 통해서 테스트를 진행할 수 있다.
        *  */
        // 설정한 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        /*
         * mockMvc로 요청한 내용을 result로 받아 상태값 201이 맞다면 통과
         * */
        result.andExpect(status().isCreated());

        // 전체 조회
        List<Article> articles = blogRepository.findAll();
        System.out.println(articles.size() + " => articles의 List 크기 검증");
        System.out.println(articles.get(0).getTitle() + " => Title 값 검증" );
        System.out.println(articles.get(0).getContent() + " => content 값 검증");

        assertThat(articles.size()).isEqualTo(1);   // 전체 조회된 값의 리스트의 크기가 1인지 검증
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("findAllArticles : 블로그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception {
        // given
        final String url = "/api/articles";
        final String title = "제목!!";
        final String content = "글의 내용!!";

        blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        // when
        final ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(content))
                .andExpect(jsonPath("$[0].title").value(title));
    }


}