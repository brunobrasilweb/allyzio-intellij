package br.com.inoovexa.allyzio.allyzio;

import br.com.inoovexa.allyzio.state.AllyzioPersistentState;
import com.intellij.openapi.project.Project;

import java.time.LocalDate;

public class AllyzioUtil {

    public static int MAX_REQUEST = 5;

    public static boolean isTokenValid(Project project) {
        AllyzioPersistentState state = AllyzioPersistentState.getInstance(project);

        ApiClient apiClient = new ApiClient();

        if (state.getDate() != LocalDate.now().getDayOfMonth()) {
            state.setTokenValid(apiClient.isTokenValid(project));
        }

        return state.isTokenValid();
    }

    public static void countRequest(Project project) {
        AllyzioPersistentState state = AllyzioPersistentState.getInstance(project);

        if (state.getDate() == LocalDate.now().getDayOfMonth()) {
            state.setCounter(state.getCounter() + 1);
        } else {
            state.setCounter(0);
            state.setDate(LocalDate.now().getDayOfMonth());
        }
    }

}
