package com.personalproject.airecipegenerator.Dao;

import java.util.List;

public record Request (List<String> ingredients, String cuisine, String mealType)

{
}
