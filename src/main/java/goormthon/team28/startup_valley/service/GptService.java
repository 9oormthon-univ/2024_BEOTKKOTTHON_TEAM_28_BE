package goormthon.team28.startup_valley.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import goormthon.team28.startup_valley.dto.gpt.request.GptRequest;
import goormthon.team28.startup_valley.dto.gpt.request.Message;
import goormthon.team28.startup_valley.dto.gpt.response.GptResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GptService {
    @Value("${gpt.token}")
    private String gptSecretKey;
    private RestTemplate restTemplate = new RestTemplate();
    public String sendMessage(List<String> works) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(gptSecretKey);
        log.info("gpt 헤더 설정 완료");

        HttpEntity httpEntity = new HttpEntity(makeRequest(works), headers);
        log.info("gpt http entity 생성 완료");

        GptResponse gptResponse = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                httpEntity,
                GptResponse.class
        ).getBody();
        log.info("gpt api 응답 완료");

        return gptResponse.getChoices().get(0).getMessage().getContent();
    }
    private GptRequest makeRequest(List<String> works){
        List<Message> messages = new ArrayList<>();
        messages.add(Message.makeFirstMessage());
        works.forEach(work -> messages.add(Message.makeUserWork(work)));
        log.info("message size = work's size + 1 = {}", messages.size());
        return GptRequest.builder()
                .messages(messages)
                .temperature(0.5)
                .build();
    }
}
