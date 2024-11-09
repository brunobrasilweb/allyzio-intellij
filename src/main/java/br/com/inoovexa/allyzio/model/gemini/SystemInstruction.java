package br.com.inoovexa.allyzio.model.gemini;

import java.util.List;

public record SystemInstruction(String role, List<Part> parts) {}
