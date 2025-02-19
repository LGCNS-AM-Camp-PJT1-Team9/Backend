package com.team9.jobbotdari.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ChatGptConfig {

    // application.yml에 정의된 openai.api-key 값을 주입받음
    @Value("${openai.api-key}")
    private String apiKey;

    /**
     * ChatClient 빈을 생성하는 메소드
     * ChatClient는 OpenAI Chat API와 상호작용하여 대화형 응답을 생성하는 클라이언트입니다.
     *
     * @return 설정된 ChatClient 인스턴스.
     */
    @Bean
    public ChatClient chatClient() {
        // OpenAiChatOptions 빌더를 통해 채팅 모델의 옵션을 구성.
        // 여기서 사용할 모델("gpt-4o-mini")과 응답 생성 시의 온도(0.7)를 설정합니다.
        // 온도 값이 낮을수록 응답이 더 결정적(deterministic)이고, 높을수록 무작위성이 증가합니다.
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("gpt-4o-mini")
                .temperature(0.7)
                .build();

        // OpenAiApi 빌더를 통해 OpenAiApi 인스턴스를 생성합니다.
        // 이 API 인스턴스는 실제 OpenAI API와의 HTTP 통신에 사용되며,
        // API 키를 사용하여 인증을 수행합니다.
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(apiKey)
                .build();

        // OpenAiChatModel 빌더를 사용하여 ChatModel 인스턴스를 생성합니다.
        // ChatModel은 OpenAiApi와 옵션을 포함하여 채팅 모델의 동작을 정의합니다.
        ChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)       // OpenAiApi 인스턴스를 설정하여 API 호출을 수행
                .defaultOptions(options)    // 채팅 옵션(모델, 온도 등)을 기본 옵션으로 설정
                .build();

        // 최종적으로 ChatModel을 이용하여 ChatClient를 생성합니다.
        // ChatClient는 대화 요청(prompt)과 응답 처리(call, content 등)를 위한 엔트리 포인트입니다.
        return ChatClient.builder(chatModel).build();
    }

    /**
     * 시스템 프롬프트 템플릿 빈을 생성하는 메소드
     * 이 템플릿은 ChatClient 요청 시 시스템 메시지로 사용되며,
     * 뉴스 기사를 분석하는 역할과 작업 지침을 포함합니다.
     *
     * @return 시스템 프롬프트 템플릿 문자열.
     */
    @Bean
    public String systemPromptTemplate() {
        return """
            당신은 취업준비생을 위한 기업 분석 전문가입니다. 제공된 뉴스 기사를 분석하여, 해당 기업이 최근 어떤 이슈에 주목하고 있으며, 경영 전략, 신사업, 기술 투자, 시장 동향 등을 파악해 주세요.
            
            요약 방식:
            1. 핵심 내용 요약: 기사에서 다루는 주요 내용을 3~5줄 이내로 요약합니다.
            2. 기업이 주목하는 키워드 및 방향성: 해당 기업이 어떤 기술, 시장, 정책, 전략 등에 집중하고 있는지 정리합니다.
            3. 취업준비생 관점 분석: 이 뉴스를 통해 취업준비생이 얻을 수 있는 인사이트를 제공합니다. (예: 기업이 원하는 인재상, 기술 스택, 관련 직무 전망 등)
            4. 활용 팁: 지원서나 면접에서 활용할 수 있는 포인트를 제안합니다.
            """;
    }
}
