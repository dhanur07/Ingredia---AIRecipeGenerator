package com.personalproject.airecipegenerator.Dao;

// This new record holds the two parts of the ingredient for ingredient extraction
public record Ingredient(
        String display, // "1 cup flour, sifted"
        String item     // "flour"
) {}