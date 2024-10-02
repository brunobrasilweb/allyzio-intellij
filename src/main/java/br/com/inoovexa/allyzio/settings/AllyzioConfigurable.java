package br.com.inoovexa.allyzio.settings;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class AllyzioConfigurable implements Configurable {
    private final AllyzioSettings settings;
    private JTextField allyzioTokenField;
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
        allyzioTokenField = new JTextField(settings.getAllyzioToken(), 30);
        openaiTokenField = new JTextField(settings.getOpenaiToken(), 30);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel allyzioPanel = new JPanel();
        allyzioPanel.add(new JLabel("Allyzio Token:"));
        allyzioPanel.add(allyzioTokenField);

        JPanel openaiPanel = new JPanel();
        openaiPanel.add(new JLabel("OpenAI Token:"));
        openaiPanel.add(openaiTokenField);

        panel.add(allyzioPanel);
        panel.add(openaiPanel);

        return panel;
    }

    @Override
    public boolean isModified() {
        return !allyzioTokenField.getText().equals(settings.getAllyzioToken()) ||
                !openaiTokenField.getText().equals(settings.getOpenaiToken());
    }

    @Override
    public void apply() {
        settings.setAllyzioToken(allyzioTokenField.getText());
        settings.setOpenaiToken(openaiTokenField.getText());
    }

    @Override
    public void reset() {
        allyzioTokenField.setText(settings.getAllyzioToken());
        openaiTokenField.setText(settings.getOpenaiToken());
    }
}
