package com.team9.jobbotdari.controller;

import com.team9.jobbotdari.dto.NewsArticle;
import com.team9.jobbotdari.service.NewsService;
import com.team9.jobbotdari.service.ChatGPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    // 뉴스 검색 기능을 제공하는 서비스
    private final NewsService newsService;

    // ChatGPT를 이용해 뉴스 요약 기능을 제공하는 서비스
    private final ChatGPTService chatGPTService;

    /**
     * "/news/search" 엔드포인트에 대한 GET 요청을 처리합니다.
     * 클라이언트로부터 "query" 파라미터(검색어)를 받아 해당 뉴스 기사를 검색하고,
     * 검색 결과의 뉴스 타이틀들을 번호와 함께 결합한 문자열을 ChatGPTService에 전달하여 요약을 생성합니다.
     *
     * @param query 클라이언트가 검색하고자 하는 키워드
     * @return 뉴스 기사 목록과 생성된 요약을 포함하는 응답(ResponseEntity)
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchNews(@RequestParam("query") String query) {
        // 1. NewsService를 사용하여 검색어에 해당하는 뉴스 기사 리스트를 가져옵니다.
        List<NewsArticle> articles = newsService.searchNews(query);

        // 2. 가져온 뉴스 기사들의 타이틀에 번호를 붙여 하나의 문자열로 결합합니다.
        //    generateTitlesSummaryInput 메서드는 "1. 첫번째 제목\n2. 두번째 제목\n..." 형태의 문자열을 반환합니다.
        String titlesSummaryInput = newsService.generateTitlesSummaryInput(articles);

        // 3. 결합된 타이틀 문자열을 ChatGPTService의 getNewsSummary 메서드에 전달하여 뉴스 요약을 생성합니다.
        String summary = chatGPTService.getNewsSummary(titlesSummaryInput);

        // 4. 응답 데이터로 뉴스 기사 리스트와 생성된 요약을 Map 형태로 구성합니다.
        Map<String, Object> response = new HashMap<>();
        response.put("articles", articles);
        response.put("summary", summary);

        // 5. 구성한 응답 데이터를 HTTP 200 OK 상태와 함께 반환합니다.
        return ResponseEntity.ok(response);
    }
}
