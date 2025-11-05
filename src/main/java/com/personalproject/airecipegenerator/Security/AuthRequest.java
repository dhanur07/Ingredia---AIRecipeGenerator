package com.personalproject.airecipegenerator.Security;

// A DTO (record) for the login/register request body
public record AuthRequest(String username, String password) {}