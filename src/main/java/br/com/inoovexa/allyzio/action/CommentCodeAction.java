package br.com.inoovexa.allyzio.action;

import br.com.inoovexa.allyzio.openai.ApiRequest;
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
import java.util.Locale;

import static br.com.inoovexa.allyzio.allyzio.AllyzioUtil.isTokenValid;
import static java.util.Objects.isNull;

public class CommentCodeAction extends AnAction {

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
        ApiRequest request = new ApiRequest();
        String lang = Locale.getDefault().getLanguage();

        String systemPrompt = "You are a software engineering expert, provide comments explaining the code in a simple way in lang " + lang + ".\n" +
                "\n" +
                "What you should not do in the code:\n" +
                "- dont return markdownalter the code structure\n" +
                "- alter the code structure\n" +
                "- Do not remove original code\n" +
                "- Do not return code in Markdown format.\n" +
                "- do not response only comment\n" +
                "- add comment at code sended\n";

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
