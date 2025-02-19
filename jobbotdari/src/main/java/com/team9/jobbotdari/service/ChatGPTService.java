package com.team9.jobbotdari.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor  // final 필드들을 자동으로 생성자 주입받도록 Lombok이 생성자를 만듭니다.
public class ChatGPTService {

    // ChatClient: OpenAI API와 상호작용하여 Chat 요청을 처리하는 클라이언트
    private final ChatClient chatClient;

    // 시스템 프롬프트 템플릿: 뉴스 기사 요약 작업에 대한 지침을 담은 텍스트 (외부에서 주입받음)
    private final String systemPromptTemplate;

    /**
     * 뉴스 기사 링크를 입력받아 요약된 결과를 반환하는 메서드
     *
     * @param newsContent 뉴스 기사 링크나 뉴스 기사 내용을 나타내는 문자열.
     * @return OpenAI API를 통해 생성된 뉴스 요약 결과 문자열.
     */
    public String getNewsSummary(String newsContent) {

        // 사용자 프롬프트 템플릿: 입력된 뉴스 기사 링크를 포함하는 요청 텍스트를 생성하기 위한 템플릿.
        // 여기서는 "입력 기사 링크:" 라는 안내와 함께 newsContent를 넣도록 구성합니다.
        String userPromptTemplate =
                """
                    입력 기사 링크:
                    %s
                """;

        // 사용자 프롬프트 템플릿에 실제 뉴스 기사 내용을 포맷팅하여 삽입
        String userPrompt = String.format(userPromptTemplate, newsContent);

        // ChatClient를 통해 OpenAI Chat 요청을 구성 및 실행:
        // 1. prompt() : 새로운 Chat 요청을 시작합니다.
        // 2. system(systemPromptTemplate) : 시스템 메시지로, 역할 및 작업 지침을 설정합니다.
        // 3. user(userPrompt) : 사용자 메시지로, 실제 뉴스 기사 링크를 전달합니다.
        // 4. call() : API 호출을 실행합니다.
        // 5. content() : API 응답에서 생성된 요약 텍스트를 반환합니다.
        return chatClient
                .prompt()
                .system(systemPromptTemplate)
                .user(userPrompt)
                .call()
                .content();
    }
}
