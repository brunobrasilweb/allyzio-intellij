package br.com.inoovexa.allyzio.action;

import br.com.inoovexa.allyzio.openai.ApiRequest;
import br.com.inoovexa.allyzio.settings.AllyzioSettings;
import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.io.IOException;
import java.util.Locale;

import static java.util.Objects.isNull;

public class CommentCodeAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getRequiredData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR);
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();

        if (isNull(selectedText)) {
            Messages.showMessageDialog("No text selected", "Error", Messages.getErrorIcon());
            return;
        }

        String commentCode = null;
        try {
            commentCode = requestCommentCode(project, selectedText);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        if (commentCode != null) {
            showDifferences(project, editor, selectedText, commentCode);
        } else {
            Messages.showMessageDialog("Failed to comment code", "Error", Messages.getErrorIcon());
        }
    }

    private String requestCommentCode(Project project, String code) throws IOException {
        AllyzioSettings settings = AllyzioSettings.getInstance(project);
        ApiRequest request = new ApiRequest(settings.getOpenAiApiKey());
        String lang = Locale.getDefault().getLanguage();

        String systemPrompt = "You are a software engineering expert, provide comments explaining the code in a simple way in lang " + lang + ". Return only the code with the comments without using markdown.\n" +
                "\n" +
                "What you should not do in the code:\n" +
                "- alter the code structure\n" +
                "- remove any code";

        return request.chat(systemPrompt, code);
    }

    private void showDifferences(Project project, Editor editor, String originalCode, String improvedCode) {
        var diffContentFactory = DiffContentFactory.getInstance();
        String codeModified = editor.getDocument().getText().replace(originalCode, improvedCode);

        SimpleDiffRequest diffRequest = new SimpleDiffRequest(
                "Allyzio: Diff View",
                diffContentFactory.create(project, new DocumentImpl(codeModified)),
                diffContentFactory.create(project, editor.getDocument()), "Updated", "Original");

        DiffManager.getInstance().showDiff(project, diffRequest);
    }

}
