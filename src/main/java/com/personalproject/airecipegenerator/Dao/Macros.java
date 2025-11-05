package com.personalproject.airecipegenerator.Dao;

// This  record will hold the nutritional data
public record Macros(
        int calories,
        int proteinGrams,
        int fatGrams,
        int carbGrams
) {}