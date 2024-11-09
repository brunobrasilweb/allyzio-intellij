package br.com.inoovexa.allyzio.model.gemini;

import java.util.List;

public record ChatResponse(List<Candidates> candidates) {}
