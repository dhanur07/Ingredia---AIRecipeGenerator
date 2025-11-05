package com.personalproject.airecipegenerator.Controllers;


import com.personalproject.airecipegenerator.Service.RecipeService;
import com.personalproject.airecipegenerator.Dao.Request;
import com.personalproject.airecipegenerator.Dao.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private RecipeService recipeService;
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Response> generateRecipe(@RequestBody Request request){
    try{

        Response recipe = recipeService.GenerateRecipe(request);
        return ResponseEntity.ok(recipe);

    }
    catch (Exception e){
        e.printStackTrace();
        String errorMessage = "Failed to generate recipe." + e.getMessage();
        return ResponseEntity.status(500).body(new Response(null,errorMessage,0,0,null,null,null, errorMessage));
    }

    }
}
