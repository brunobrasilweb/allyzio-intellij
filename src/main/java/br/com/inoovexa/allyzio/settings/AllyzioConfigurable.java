package br.com.inoovexa.allyzio.settings;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class AllyzioConfigurable implements Configurable {
    private final AllyzioSettings settings;
    private JTextField tokenField;

    public AllyzioConfigurable(Project project) {
        this.settings = AllyzioSettings.getInstance(project);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Allyzio Copilot";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        tokenField = new JTextField(settings.getAllyzioToken(), 30);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Token:"));
        panel.add(tokenField);
        return panel;
    }

    @Override
    public boolean isModified() {
        return !tokenField.getText().equals(settings.getAllyzioToken());
    }

    @Override
    public void apply() {
        settings.setAllyzioToken(tokenField.getText());
    }

    @Override
    public void reset() {
        tokenField.setText(settings.getAllyzioToken());
    }

}