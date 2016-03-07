package com.teguholica.vmalogin.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {

    static public String hashMD5(String s) {
        MessageDigest algorithm = null;
        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            System.out.println("Cannot find digest algorithm");
            System.exit(1);
        }
        byte[] defaultBytes = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            defaultBytes[i] = (byte) (0xFF & s.charAt(i));
        }
        algorithm.reset();
        algorithm.update(defaultBytes);
        byte messageDigest[] = algorithm.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte aMessageDigest : messageDigest) {
            String hex = Integer.toHexString(0xFF & aMessageDigest);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
