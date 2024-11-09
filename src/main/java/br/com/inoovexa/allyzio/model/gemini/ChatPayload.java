package br.com.inoovexa.allyzio.model.gemini;

import br.com.inoovexa.allyzio.model.openai.Message;

import java.util.List;

public record ChatPayload(List<Content> contents, SystemInstruction systemInstruction, GenerationConfig generationConfig) {

}
