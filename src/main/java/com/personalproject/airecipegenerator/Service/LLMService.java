package com.personalproject.airecipegenerator.Service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//Service Handling the LLM workflow
@Service
public class LLMService {

    @Value("${llm.api.key}")
    private String API_KEY;

    @Value("${llm.api.url}")
    private String API_URL;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper= new ObjectMapper();


//    Returns the prompt response in string
    public String getAIRespose(String prompt) throws Exception {

        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = """
        {
          "contents": [
            {
              "parts": [
                {
                  "text": "%s"
                }
              ]
            }
          ]
        }
        """.formatted(prompt.replace("\"", "\\\""));

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        String fullApiUrl = API_URL + "?key=" + API_KEY;
        String rawResponse = restTemplate.postForObject(fullApiUrl, entity, String.class);

        return extractContent(rawResponse);
    }

//    Extracts Data from JSON format returned by Gemini AI
    private String extractContent(String rawResponse) throws Exception {

        var json = objectMapper.readTree(rawResponse);
        String text = json.path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text")
                .asText();

        return text.replace("```json", "").replace("```", "").trim();    }
}
