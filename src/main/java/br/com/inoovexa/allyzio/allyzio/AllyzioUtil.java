package br.com.inoovexa.allyzio.allyzio;

import br.com.inoovexa.allyzio.settings.AllyzioSettings;
import br.com.inoovexa.allyzio.state.AllyzioPersistentState;
import com.intellij.openapi.project.Project;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;

public class AllyzioUtil {

    public static int MAX_REQUEST = 5;

    public static boolean isTokenValid(Project project) {
        try {
            String secret = "VG+XvB+hNmOc=!5T";
            AllyzioSettings settings = AllyzioSettings.getInstance(project);
            String[] parts = settings.getAllyzioToken().split("\\.");

            if (settings.getAllyzioToken().isEmpty()) return false;

            return validateToken(settings.getAllyzioToken(), secret);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean validateToken(String token, String secret) throws Exception {
        if (secret.length() != 16) {
            secret = String.format("%-16s", secret).replace(' ', '0'); // Pad com zeros se necessário
        }

        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            return false; // O token não está no formato correto
        }
        String encryptedData = parts[1];

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));

        String expiryString = new String(decryptedBytes, StandardCharsets.UTF_8);

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date expiryDate = isoFormat.parse(expiryString);

        Date currentDate = new Date();
        return currentDate.before(expiryDate); // Retorna true se o token ainda for válido
    }

    public static void countRequest(Project project) {
        AllyzioPersistentState state = AllyzioPersistentState.getInstance(project);

        if (state.getDate() == LocalDate.now().getDayOfMonth()) {
            state.setCounter(state.getCounter() + 1);
        } else {
            state.setCounter(0);
            state.setDate(LocalDate.now().getDayOfMonth());
        }
    }

}
