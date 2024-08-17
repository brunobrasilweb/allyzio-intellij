package br.com.inoovexa.allyzio.model.openai;

import java.util.List;

public record ChatPayload(String model, List<Message> messages) {
}
