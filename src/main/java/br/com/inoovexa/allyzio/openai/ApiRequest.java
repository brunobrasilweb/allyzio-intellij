package br.com.inoovexa.allyzio.openai;

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

    public ApiRequest() {
    }

    public String chat(String systemPrompt, String userPrompt, Project project) throws IOException {
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
            return result.getChoices().get(0).getMessage().getContent(); // Process the response as needed
        }

        return null;
    }

}
