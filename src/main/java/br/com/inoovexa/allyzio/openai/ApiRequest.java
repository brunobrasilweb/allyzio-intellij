package br.com.inoovexa.allyzio.openai;

import br.com.inoovexa.allyzio.model.gemini.*;
import br.com.inoovexa.allyzio.model.openai.ChatPayload;
import br.com.inoovexa.allyzio.model.openai.Message;
import br.com.inoovexa.allyzio.settings.AllyzioSettings;
import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import okhttp3.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ApiRequest {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public ApiRequest() {
    }

    public String chat(String systemPrompt, String userPrompt, Project project) throws IOException {
        AllyzioSettings settings = AllyzioSettings.getInstance(project);

        if (settings.getProvider().equals("ChatGPT")) {
            return callOpenAi(systemPrompt, userPrompt, project);
        } else {
            return callGemini(systemPrompt, userPrompt, project);
        }
    }

    private String callOpenAi(String systemPrompt, String userPrompt, Project project) throws IOException {
        AllyzioSettings settings = AllyzioSettings.getInstance(project);
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        List<Message> messages = Arrays.asList(
                new Message("system", systemPrompt),
                new Message("user", userPrompt)
        );

        ChatPayload payload = new ChatPayload("gpt-3.5-turbo", messages);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), gson.toJson(payload));

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + settings.getOpenaiToken())
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful() && response.body() != null) {
            ChatCompletionResult result = gson.fromJson(response.body().string(), ChatCompletionResult.class);
            return result.getChoices().get(0).getMessage().getContent().replaceAll("^```[a-zA-Z0-9]*\\s*|```$", "").trim();
        }

        return null;
    }

    private String callGemini(String systemPrompt, String userPrompt, Project project) throws IOException {
        AllyzioSettings settings = AllyzioSettings.getInstance(project);
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        List<Part> partsContent = List.of(new Part(userPrompt));
        List<Part> partsSystem = List.of(new Part(systemPrompt));
        List<Content> contents = List.of(new Content("user", partsContent));
        SystemInstruction systemInstruction = new SystemInstruction("user", partsSystem);
        GenerationConfig generationConfig = new GenerationConfig(1, 40, 0.95, 8192, "text/plain");

        br.com.inoovexa.allyzio.model.gemini.ChatPayload payload = new br.com.inoovexa.allyzio.model.gemini.ChatPayload(contents, systemInstruction, generationConfig);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), gson.toJson(payload));

        Request request = new Request.Builder()
                .url(GEMINI_API_URL + "?key=" + settings.getGeminiToken())
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful() && response.body() != null) {
            ChatResponse result = gson.fromJson(response.body().string(), ChatResponse.class);
            return result.candidates().get(0).content().parts().get(0).text().replaceAll("^```[a-zA-Z0-9]*\\s*|```$", "").trim();
        }

        return null;
    }

}
