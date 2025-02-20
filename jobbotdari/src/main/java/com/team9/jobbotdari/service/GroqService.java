package com.team9.jobbotdari.service;

import com.team9.jobbotdari.config.GroqConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroqService {

    // Spring의 RestClient를 사용하여 HTTP 요청을 수행할 인스턴스 생성.
    private final RestClient restClient = RestClient.create();
    private final GroqConfig groqConfig;

    /**
     * 사용자가 전달한 뉴스 콘텐츠를 포함하여 Groq API에 요청을 보내고,
     * 응답을 Map 형태로 반환합니다.
     *
     * 요청 과정:
     * 1. 시스템 프롬프트 템플릿에 입력된 뉴스 콘텐츠(newsContent)를 포맷팅하여 최종 메시지를 생성합니다.
     * 2. HTTP 헤더에 JSON 콘텐츠 타입과 Bearer 인증 헤더를 설정합니다.
     * 3. 사용자 메시지 객체를 생성하여, role과 content를 포함시킵니다.
     * 4. API 요청 본문에 모델명, 메시지 리스트, 온도 등의 설정값을 포함시킵니다.
     * 5. RestClient를 사용해 POST 요청을 실행하고, 응답을 Map으로 반환합니다.
     *
     * @param newsContent 사용자 입력 뉴스 콘텐츠 (예: 기사 링크 또는 기사 요약)
     * @return Groq API 응답 결과를 담은 Map 객체
     */
    public Map<?, ?> getNewsSummary(String newsContent) {
        // 시스템 프롬프트 템플릿에 newsContent를 삽입하여 최종 메시지를 구성
        String message = String.format(groqConfig.getSystemPromptTemplate(), newsContent);

        // HTTP 요청 헤더 구성: JSON 타입과 Bearer 토큰 인증 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + groqConfig.getGroqApiKey());

        // 사용자 메시지 객체 생성: role은 "user", content는 포맷팅된 메시지
        Map<String, Object> messageObj = new HashMap<>();
        messageObj.put("role", "user");
        messageObj.put("content", message);

        // API 요청 본문 구성:
        // - model: 사용할 모델명 (groqModel)
        // - messages: 사용자 메시지 객체를 리스트 형태로 포함
        // - temperature: 응답 생성 시 사용할 온도 (groqTemperature)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", groqConfig.getGroqModel());
        requestBody.put("messages", List.of(messageObj));
        requestBody.put("temperature", groqConfig.getGroqTemperature());

        // RestClient를 사용하여 Groq API에 POST 요청을 보내고, 응답을 Map 형태로 받아옴.
        return restClient.post()
                .uri(groqConfig.getGroqApiUrl())
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(requestBody)
                .retrieve()
                .body(Map.class);
    }
}
