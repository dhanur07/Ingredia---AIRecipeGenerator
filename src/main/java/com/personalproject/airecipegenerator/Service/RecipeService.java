package com.personalproject.airecipegenerator.Service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalproject.airecipegenerator.Dao.Request;
import com.personalproject.airecipegenerator.Dao.Response;
import org.springframework.stereotype.Service;


//Service Handling the Recipe workflow
@Service
public class RecipeService {
    private final PromptService promptService;
    private final LLMService llmService;
    private final ObjectMapper objectMapper;

    public RecipeService(PromptService promptService, LLMService llmService, ObjectMapper objectMapper) {
        this.promptService = promptService;
        this.llmService = llmService;
        this.objectMapper = objectMapper;
    }

    public Response GenerateRecipe(Request request) throws Exception
    {
        String prompt= promptService.createPrompt(request);
        String rawJsonResponse = llmService.getAIRespose(prompt);
        return  objectMapper.readValue(rawJsonResponse, Response.class);
    }
}
