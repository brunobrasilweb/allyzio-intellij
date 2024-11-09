package br.com.inoovexa.allyzio.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "AllyzioSettings", storages = @Storage("allyzio_settings.xml"))
@Service(Service.Level.PROJECT)
public final class AllyzioSettings implements PersistentStateComponent<AllyzioSettings> {
    private String openaiToken;
    private String geminiToken;
    private String provider;

    @Nullable
    @Override
    public AllyzioSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AllyzioSettings state) {
        this.openaiToken = state.openaiToken;
        this.geminiToken = state.geminiToken;
        this.provider = state.provider;
    }

    public String getOpenaiToken() {
        return openaiToken;
    }

    public void setOpenaiToken(String openaiToken) {
        this.openaiToken = openaiToken;
    }

    public String getGeminiToken() {
        return geminiToken;
    }

    public void setGeminiToken(String geminiToken) {
        this.geminiToken = geminiToken;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public static AllyzioSettings getInstance(@NotNull Project project) {
        return project.getService(AllyzioSettings.class);
    }

}
