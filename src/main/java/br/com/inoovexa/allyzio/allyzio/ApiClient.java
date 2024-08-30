package br.com.inoovexa.allyzio.allyzio;

import br.com.inoovexa.allyzio.settings.AllyzioSettings;
import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class ApiClient {

    private static final String API_URL = "https://allyzio.com/api/token-access";

    public ApiClient() {
    }

    public Boolean isTokenValid(Project project) {
        OkHttpClient client = new OkHttpClient();
        AllyzioSettings settings = AllyzioSettings.getInstance(project);

        if (Objects.isNull(settings.getAllyzioToken()) || settings.getAllyzioToken().isEmpty()) {
            return false;
        }

        Request request = new Request.Builder()
                .url(API_URL)
                .get()
                .addHeader("Token", settings.getAllyzioToken())
                .build();

        Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response.isSuccessful();
    }

}
