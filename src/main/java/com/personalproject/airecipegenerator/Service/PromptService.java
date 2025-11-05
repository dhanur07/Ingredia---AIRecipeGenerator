package com.personalproject.airecipegenerator.Service;

import com.personalproject.airecipegenerator.Dao.Request;
import org.springframework.stereotype.Service;

@Service
public class PromptService {


    //Custom Prompt that can be modified to get custom results.
    // Following prompt returns recipe made my the ingredients mentioned in a JSON format
    public String createPrompt(Request request) {
        String ingredientList = String.join(", ", request.ingredients());


        return """
        You are an expert chef and culinary AI. Your task is to create a 
        delicious recipe and return it in a specific JSON format.
        
        --- CRITICAL RULES ---
        1.  You MUST only use the following ingredients as main ingredients (spices and regular household ingrediencts like condaments are acceptable): %s
        2.  The recipe must fit the cuisine type: %s
        3.  The recipe must be a: %s
        4.  You MUST provide a non-empty "instructions" array. This is a mandatory field.
        5.  You MUST reply ONLY with a single, valid JSON object.
        6.  If the "instructions" array is missing or empty, the entire response is invalid.
        
        --- JSON SCHEMA (MUST BE FOLLOWED) ---
        record Macros(
            int calories,
            int proteinGrams,
            int fatGrams,
            int carbGrams
        ) {}
        
        record Ingredient(
            String display, // e.g., "1/2 cup onion, finely diced"
            String item     // e.g., "onion". Must be null for salt, pepper, water, or garnish.
        ) {}
        
        record RecipeResponse(
            String title,
            String description,
            int prepTimeMinutes,
            int cookTimeMinutes,
            Macros macros,
            List<Ingredient> ingredients,
            List<String> instructions, // <-- THIS IS A REQUIRED FIELD
            String error
        ) {}

        --- EXAMPLE OF A PERFECT RESPONSE ---
        {
          "title": "Example Lemon Chicken",
          "description": "A quick and easy lemon chicken dish.",
          "prepTimeMinutes": 10,
          "cookTimeMinutes": 15,
          "macros": {
            "calories": 300,
            "proteinGrams": 30,
            "fatGrams": 10,
            "carbGrams": 5
          },
          "ingredients": [
            { "display": "1 lb chicken breast", "item": "chicken breast" },
            { "display": "1 lemon, juiced", "item": "lemon" },
            { "display": "Salt and pepper, to taste", "item": null }
          ],
          "instructions": [
            "Step 1: Season the chicken.",
            "Step 2: Cook in a hot pan for 5-7 minutes per side.",
            "Step 3: Squeeze lemon juice over the top and serve."
          ],
          "error": null
        }
        ---
        
        Now, generate the recipe for the user. Do not forget the "instructions".
        """.formatted(ingredientList, request.cuisine(), request.mealType());
    }
}