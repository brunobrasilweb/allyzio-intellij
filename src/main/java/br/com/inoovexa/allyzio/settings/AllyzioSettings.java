package br.com.inoovexa.allyzio.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "AllyzioSettings", storages = @Storage("allyzio_settings.xml"))
public class AllyzioSettings implements PersistentStateComponent<AllyzioSettings> {
    private String openAiApiKey;

    @Nullable
    @Override
    public AllyzioSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AllyzioSettings state) {
        this.openAiApiKey = state.openAiApiKey;
    }

    public String getOpenAiApiKey() {
        return openAiApiKey;
    }

    public void setOpenAiApiKey(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
    }

    public static AllyzioSettings getInstance() {
        return ServiceManager.getService(AllyzioSettings.class);
    }
}
