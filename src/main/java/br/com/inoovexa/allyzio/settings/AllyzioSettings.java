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
    private String allyzioToken;

    @Nullable
    @Override
    public AllyzioSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AllyzioSettings state) {
        this.allyzioToken = state.allyzioToken;
    }

    public String getAllyzioToken() {
        return allyzioToken;
    }

    public void setAllyzioToken(String allyzioToken) {
        this.allyzioToken = allyzioToken;
    }

    public static AllyzioSettings getInstance(@NotNull Project project) {
        return project.getService(AllyzioSettings.class);
    }

}
