/*
    By SHZTD - Thomas
    Prohibido el uso comercial exceptio sin previo permiso de mi.
 */

package org.vinyes.asistencia.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Encryption {
    // key hardcodeada, si le hacen ingieneria inversa al programa we are fucked
    private static final String SECRET_KEY = "H=X2J7Wb#jV</5zd";

    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar", e);
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decoded));
        } catch (Exception e) {
            throw new RuntimeException("Error al descifrar", e);
        }
    }

    // registramos el usuario tambien de manera hardcodeada buff
    private static final String ucrypt = "ZFmETBB6ITBl/5Sr2Oi+sw==";
    private static final String pcrypt = "wlbGlVvchPMotlmtNfnzJg==";

    public static boolean validateCredentials(String inputUser, String inputPass) {
        String decryptedUser = decrypt(ucrypt);
        String decryptedPass = decrypt(pcrypt);

        return decryptedUser.equals(inputUser) && decryptedPass.equals(inputPass);
    }

    /*
        administrador
        lesVinyes1234#
     */

}
