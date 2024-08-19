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

    // Método para obter a instância do serviço usando o Project
    public static AllyzioSettings getInstance(@NotNull Project project) {
        return project.getService(AllyzioSettings.class);
    }

}
