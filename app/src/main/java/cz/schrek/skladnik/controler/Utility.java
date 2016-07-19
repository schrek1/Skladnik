package cz.schrek.skladnik.controler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ondra on 19. 7. 2016.
 */
public class Utility {
    public static String getHash(String text) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(text.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
