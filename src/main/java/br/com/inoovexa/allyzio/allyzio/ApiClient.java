package br.com.inoovexa.allyzio.allyzio;

import br.com.inoovexa.allyzio.settings.AllyzioSettings;
import com.intellij.openapi.project.Project;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.UncheckedIOException;

import static java.util.Objects.isNull;

public class ApiClient {

    private static final String API_URL = "https://allyzio.com/api/token-access";

    public ApiClient() {
    }

    public Boolean isTokenValid(Project project) {
        OkHttpClient client = new OkHttpClient();
        AllyzioSettings settings = AllyzioSettings.getInstance(project);

        if (isNull(settings.getAllyzioToken()) || settings.getAllyzioToken().isEmpty()) {
            return false;
        }

        Request request = new Request.Builder()
                .url(API_URL)
                .get()
                .addHeader("Token", settings.getAllyzioToken())
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
