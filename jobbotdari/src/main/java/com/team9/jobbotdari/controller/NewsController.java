package com.team9.jobbotdari.controller;

import com.team9.jobbotdari.dto.NewsArticle;
import com.team9.jobbotdari.service.GroqService;
import com.team9.jobbotdari.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final GroqService groqService;

    /**
     * GET /news/search 엔드포인트를 처리합니다.
     * 클라이언트로부터 "query" 파라미터를 받아 뉴스 기사를 검색하고,
     * 뉴스 기사들의 타이틀에 번호를 붙여 하나의 문자열로 결합한 후,
     * GroqService를 호출하여 요약 정보를 생성합니다.
     * 최종적으로 뉴스 기사 목록과 요약 정보를 JSON 형식으로 반환합니다.
     *
     * @param query 클라이언트가 검색하고자 하는 키워드
     * @return 뉴스 기사 목록과 Groq API 응답 Map을 포함하는 ResponseEntity
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchNews(@RequestParam("query") String query) {
        // 1. NewsService를 사용하여 검색어에 해당하는 뉴스 기사 리스트를 가져옴.
        List<NewsArticle> articles = newsService.searchNews(query);

        // 2. 뉴스 기사들의 타이틀에 번호를 붙여 하나의 문자열로 결합.
        //    예: "1. 첫번째 제목\n2. 두번째 제목\n..."
        String titlesSummaryInput = newsService.generateTitlesSummaryInput(articles);

        // 3. GroqService를 호출하여 결합된 타이틀 문자열을 기반으로 뉴스 요약을 생성.
        //    GroqService는 Map 형태의 응답을 반환합니다.
        Map<?, ?> summary = groqService.getNewsSummary(titlesSummaryInput);

        // 4. 응답 데이터 구성: 뉴스 기사 리스트와 Groq API 응답 Map을 담음.
        Map<String, Object> response = new HashMap<>();
        response.put("articles", articles);
        response.put("summary", summary);

        // 5. 구성한 응답 데이터를 HTTP 200 OK 상태와 함께 반환.
        return ResponseEntity.ok(response);
    }
}
