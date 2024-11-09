package br.com.inoovexa.allyzio.settings;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class AllyzioConfigurable implements Configurable {
    private final AllyzioSettings settings;
    private JTextField openaiTokenField;

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
        openaiTokenField = new JTextField(settings.getOpenaiToken(), 30);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel openaiPanel = new JPanel();
        openaiPanel.add(new JLabel("OpenAI Token:"));
        openaiPanel.add(openaiTokenField);

        panel.add(openaiPanel);

        return panel;
    }

    @Override
    public boolean isModified() {
        return !openaiTokenField.getText().equals(settings.getOpenaiToken());
    }

    @Override
    public void apply() {
        settings.setOpenaiToken(openaiTokenField.getText());
    }

    @Override
    public void reset() {
        openaiTokenField.setText(settings.getOpenaiToken());
    }
}
