package br.com.inoovexa.allyzio.action;

import br.com.inoovexa.allyzio.openai.ApiRequest;
import br.com.inoovexa.allyzio.settings.AllyzioSettings;
import br.com.inoovexa.allyzio.state.AllyzioPersistentState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.io.IOException;
import java.util.Locale;

import static br.com.inoovexa.allyzio.allyzio.AllyzioUtil.MAX_REQUEST;
import static br.com.inoovexa.allyzio.allyzio.AllyzioUtil.countRequest;
import static br.com.inoovexa.allyzio.allyzio.AllyzioUtil.isTokenValid;
import static java.util.Objects.isNull;

public class ExplainCodeAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getRequiredData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR);
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();

        if (!isTokenValid(project)) {
            Messages.showMessageDialog("You've get token authorization Allyzio Colipot here: https://allyzio.com", "Error", Messages.getErrorIcon());
            return;
        }

        if (isNull(selectedText)) {
            Messages.showMessageDialog("No text selected", "Error", Messages.getErrorIcon());
            return;
        }

        String explainCode = null;
        try {
            explainCode = request(project, selectedText);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        if (explainCode != null) {
            generateView(explainCode);
        } else {
            Messages.showMessageDialog("Failed to explain code", "Error", Messages.getErrorIcon());
        }
    }

    private String request(Project project, String code) throws IOException {
        AllyzioSettings settings = AllyzioSettings.getInstance(project);
        ApiRequest request = new ApiRequest();
        String lang = Locale.getDefault().getLanguage();

        String systemPrompt = "You are a software engineering expert. You will briefly explain how the code works following these rules:\n" +
                "\n" +
                "Rules:\n" +
                "- lang " + lang + "\n" +
                "- Start with a description of how the code works\n" +
                "- Talk only about the code\n" +
                "- You can add comments to the code to make it easier to understand";

        return request.chat(systemPrompt, code, project);
    }

    private void generateView(String code) {
        Messages.showInfoMessage(code, "Explain Code Selected");
    }

}
