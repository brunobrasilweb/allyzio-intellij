package br.com.inoovexa.allyzio.allyzio;

import br.com.inoovexa.allyzio.settings.AllyzioSettings;
import br.com.inoovexa.allyzio.state.AllyzioPersistentState;
import com.intellij.openapi.project.Project;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
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
            if (parts.length != 2) return false;

            String encryptedDate = parts[1];

            String decryptedDate = decrypt(encryptedDate, secret);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            Date expiryDate = dateFormat.parse(decryptedDate);

            Date currentDate = new Date();
            return !currentDate.after(expiryDate);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

    public static String decrypt(String encrypted, String secret) throws Exception {
        if (secret.length() != 16) {
            secret = String.format("%-16s", secret).substring(0, 16);
        }

        SecretKeySpec key = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        byte[] original = cipher.doFinal(decoded);

        return new String(original, "UTF-8");
    }

}
