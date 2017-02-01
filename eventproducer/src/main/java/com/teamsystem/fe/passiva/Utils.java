package com.teamsystem.fe.passiva;

import java.security.MessageDigest;
import java.util.Formatter;

/**
 * Created by mmondora on 01/02/2017.
 */
public class Utils {
    public static String byteToHex(final byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static String calculateSHA1(String string) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.reset();
        messageDigest.update(string.getBytes("ISO-8859-1"));
        return byteToHex(messageDigest.digest());
    }
}
