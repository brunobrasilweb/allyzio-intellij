package br.com.inoovexa.allyzio.settings;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AllyzioConfigurable implements Configurable {
    private final AllyzioSettings settings;
    private JTextField openaiTokenField;
    private JTextField geminiTokenField;
    private JComboBox<String> providerComboBox;

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
        geminiTokenField = new JTextField(settings.getGeminiToken(), 30);

        String[] providers = {"ChatGPT", "Gemini"};
        providerComboBox = new JComboBox<>(providers);
        providerComboBox.setSelectedItem(settings.getProvider());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel providerPanel = new JPanel();
        providerPanel.add(new JLabel("Choose Provider:"));
        providerPanel.add(providerComboBox);

        JPanel openaiPanel = new JPanel();
        openaiPanel.add(new JLabel("OpenAI Token:"));
        openaiPanel.add(openaiTokenField);

        JPanel geminiPanel = new JPanel();
        geminiPanel.add(new JLabel("Gemini Token:"));
        geminiPanel.add(geminiTokenField);

        panel.add(providerPanel);
        panel.add(openaiPanel);
        panel.add(geminiPanel);

        return panel;
    }

    @Override
    public boolean isModified() {
        boolean isProviderModified = Objects.nonNull(providerComboBox.getSelectedItem()) && !providerComboBox.getSelectedItem().equals(settings.getProvider());
        boolean isOpenaiTokenModified = !openaiTokenField.getText().equals(settings.getOpenaiToken());
        boolean isGeminiTokenModified = !geminiTokenField.getText().equals(settings.getGeminiToken());

        return isProviderModified || isOpenaiTokenModified || isGeminiTokenModified;
    }

    @Override
    public void apply() {
        settings.setProvider((String) providerComboBox.getSelectedItem());
        settings.setOpenaiToken(openaiTokenField.getText());
        settings.setGeminiToken(geminiTokenField.getText());
    }

    @Override
    public void reset() {
        providerComboBox.setSelectedItem(settings.getProvider());
        openaiTokenField.setText(settings.getOpenaiToken());
        geminiTokenField.setText(settings.getGeminiToken());
    }
}