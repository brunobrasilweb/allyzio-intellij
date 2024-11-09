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
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;

import java.io.IOException;

import static java.util.Objects.isNull;

public class RefactorCodeAction extends AnAction {

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

        String refactorCode = null;
        try {
            refactorCode = requestImprovedCode(project, selectedText);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        if (refactorCode != null) {
            showDifferences(project, editor, selectedText, refactorCode);
        } else {
            Messages.showMessageDialog("Failed to improve code", "Error", Messages.getErrorIcon());
        }
    }

    private String requestImprovedCode(Project project, String code) throws IOException {
        AllyzioSettings settings = AllyzioSettings.getInstance(project);
        ApiRequest request = new ApiRequest();

        String systemPrompt = "You are a software engineering expert and will be making improvements by expanding the following rules of what to do and what not to do in these improvements:\n" +
                "Rules:\n" +
                "- Return only the refactored code.\n" +
                "- Apply the best practices of the programming language.\n" +
                "- Apply design patterns concepts if applicable.\n" +
                "- Apply SOLID principles if applicable.\n" +
                "- If using Java, utilize the new features and versions, e.g., streams, etc.\n" +
                "- Do not suggest changes to variables, methods, or classes if they are correctly named.\n" +
                "- Do not comment on the code.\n" +
                "- Do not import libraries.\n" +
                "- Do not return code in Markdown format.";

        return request.chat(systemPrompt, code, project);
    }

    private void showDifferences(Project project, Editor editor, String originalCode, String improvedCode) {
        var diffContentFactory = DiffContentFactory.getInstance();
        String codeModified = editor.getDocument().getText().replace(originalCode, improvedCode);

        VirtualFile originalFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
        if (originalFile == null) {
            return;
        }

        LightVirtualFile modifiedFile = new LightVirtualFile(originalFile.getName(), originalFile.getFileType(), codeModified);

        SimpleDiffRequest diffRequest = new SimpleDiffRequest(
                "Allyzio: Diff View",
                diffContentFactory.create(project, modifiedFile),
                diffContentFactory.create(project, editor.getDocument()), "Updated", "Original");

        DiffManager.getInstance().showDiff(project, diffRequest);
    }

}
