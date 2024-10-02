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
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;

import java.io.IOException;

import static br.com.inoovexa.allyzio.allyzio.AllyzioUtil.isTokenValid;
import static java.util.Objects.isNull;

public class TestUnitCodeAction extends AnAction {

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
        ApiRequest request = new ApiRequest();

        String systemPrompt = "You are a software engineering expert. You will write unit tests for the code with the following rules:\n" +
                "\n" +
                "Rules:\n" +
                "- Only return the unit test code\n" +
                "- do the complete unit test code and not just the comment\n" +
                "- Generate all possible test scenarios for the code\n" +
                "- Do not set up the application\n" +
                "- Do not return code in Markdown format.";

        return request.chat(systemPrompt, code, project);
    }

    private void generateTestUnit(Project project, Editor editor, String code) {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        String fileExtension = (psiFile != null && psiFile.getVirtualFile() != null) ? psiFile.getVirtualFile().getExtension() : "txt";

        LightVirtualFile virtualFile = new LightVirtualFile("TemporaryFile." + fileExtension, code);

        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

        if (document != null) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, virtualFile), true);
            });
        }
    }

}
