package br.com.inoovexa.allyzio.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "AllyzioState",
        storages = {@Storage("AllyzioState.xml")}
)
@Service(Service.Level.PROJECT)
public final class AllyzioPersistentState implements PersistentStateComponent<AllyzioPersistentState.State> {

    public static class State {
        public int date;
        public int counter;
        public boolean tokenValid;
    }

    private State myState = new State();

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.myState = state;
    }

    public static AllyzioPersistentState getInstance(@NotNull Project project) {
        return project.getService(AllyzioPersistentState.class);
    }

    public int getDate() {
        return myState.date;
    }

    public void setDate(int date) {
        myState.date = date;
    }

    public int getCounter() {
        return myState.counter;
    }

    public void setCounter(int counter) {
        myState.counter = counter;
    }

    public boolean isTokenValid() {
        return myState.tokenValid;
    }

    public void setTokenValid(boolean tokenValid) {
        myState.tokenValid = tokenValid;
    }
}
