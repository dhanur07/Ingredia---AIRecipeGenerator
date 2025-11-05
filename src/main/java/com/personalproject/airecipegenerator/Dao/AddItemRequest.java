package com.personalproject.airecipegenerator.Dao;

// This record will be used for the JSON body: {"itemName": "milk"}
public record AddItemRequest(String itemName) {}