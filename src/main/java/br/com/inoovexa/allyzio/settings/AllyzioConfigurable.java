package br.com.inoovexa.allyzio.settings;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class AllyzioConfigurable implements Configurable {
    private final AllyzioSettings settings;
    private JTextField apiKeyField;

    public AllyzioConfigurable() {
        this.settings = AllyzioSettings.getInstance();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Allyzio Copilot";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        apiKeyField = new JTextField(settings.getOpenAiApiKey(), 30);
        JPanel panel = new JPanel();
        panel.add(new JLabel("OpenAI API Key:"));
        panel.add(apiKeyField);
        return panel;
    }

    @Override
    public boolean isModified() {
        return !apiKeyField.getText().equals(settings.getOpenAiApiKey());
    }

    @Override
    public void apply() {
        settings.setOpenAiApiKey(apiKeyField.getText());
    }

    @Override
    public void reset() {
        apiKeyField.setText(settings.getOpenAiApiKey());
    }

}