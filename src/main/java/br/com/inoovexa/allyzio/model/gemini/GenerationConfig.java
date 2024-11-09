package br.com.inoovexa.allyzio.model.gemini;

public record GenerationConfig(int temperature, int topK, double topP, int maxOutputTokens, String responseMimeType) {}
