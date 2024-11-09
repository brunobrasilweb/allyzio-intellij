package br.com.inoovexa.allyzio.model.gemini;

import java.util.List;

public record Content(String role, List<Part> parts) {}
