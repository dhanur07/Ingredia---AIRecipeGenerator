package com.personalproject.airecipegenerator.Dao;

import java.util.List;

public record Response(String title,
                       String description,
                       int prepTimeMinutes,
                       int cookTimeMinutes,
                       Macros macros,
                       List<Ingredient> ingredients,
                       List<String> instructions,
                       String error)
{
}
