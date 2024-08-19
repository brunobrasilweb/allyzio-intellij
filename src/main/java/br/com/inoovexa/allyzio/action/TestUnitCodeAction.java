package br.com.inoovexa.allyzio.action;

import br.com.inoovexa.allyzio.openai.ApiRequest;
import br.com.inoovexa.allyzio.settings.AllyzioSettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.testFramework.LightVirtualFile;

import java.io.IOException;

import static java.util.Objects.isNull;

public class TestUnitCodeAction extends AnAction {

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

        String testUnitCode = null;
        try {
            testUnitCode = requestCode(project, selectedText);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        if (testUnitCode != null) {
            generateTestUnit(project, editor, testUnitCode);
        } else {
            Messages.showMessageDialog("Failed to test unit code", "Error", Messages.getErrorIcon());
        }
    }

    private String requestCode(Project project, String code) throws IOException {
        AllyzioSettings settings = AllyzioSettings.getInstance(project);
        ApiRequest request = new ApiRequest(settings.getOpenAiApiKey());

        String systemPrompt = "You are a software engineering expert. You will write unit tests for the code with the following rules:\n" +
                "\n" +
                "Rules:\n" +
                "- Only return the unit test code\n" +
                "- do the complete unit test code and not just the comment\n" +
                "- Generate all possible test scenarios for the code\n" +
                "- Do not set up the application\n" +
                "- Do not return code in Markdown format.";

        return request.chat(systemPrompt, code);
    }

    private void generateTestUnit(Project project, Editor editor, String code) {
        LightVirtualFile virtualFile = new LightVirtualFile("TemporaryFile." + editor.getVirtualFile().getExtension(), code);

        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

        if (document != null) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, virtualFile), true);
            });
        }
    }

}
