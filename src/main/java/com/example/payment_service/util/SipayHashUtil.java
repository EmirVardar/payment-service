package com.example.payment_service.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;


public class SipayHashUtil {

    private static final SecureRandom RNG = new SecureRandom();

    public static String generateHashKey(String total, int installment,
                                         String currencyCode, String merchantKey,
                                         String invoiceId, String appSecret) {
        // 1) data = total|installment|currency_code|merchant_key|invoice_id
        String data = String.join("|",
                total, String.valueOf(installment), currencyCode, merchantKey, invoiceId);

        // 2) iv = substr(sha1(mt_rand()), 0, 16)
        String ivHex16 = substr(sha1Hex(String.valueOf(RNG.nextInt())), 0, 16);
        byte[] ivBytes = ivHex16.getBytes(StandardCharsets.UTF_8); // PHP'deki gibi 16 char → 16 byte

        // 3) password = sha1(app_secret)
        String passwordHex = sha1Hex(appSecret);

        // 4) salt = substr(sha1(mt_rand()), 0, 4)
        String salt = substr(sha1Hex(String.valueOf(RNG.nextInt())), 0, 4);

        // 5) saltWithPassword = sha256(password + salt)
        String saltWithPasswordHex = sha256Hex(passwordHex + salt);

        // 6) AES-256-CBC ile şifrele (PHP, 64-char hex key'i direkt string olarak veriyor → 32 byte'a truncate)
        byte[] keyBytes = truncateTo(saltWithPasswordHex.getBytes(StandardCharsets.UTF_8), 32);
        byte[] encrypted = aes256CbcEncrypt(data.getBytes(StandardCharsets.UTF_8), keyBytes, ivBytes);
        String encryptedB64 = Base64.getEncoder().encodeToString(encrypted);

        // 7) bundle = iv:salt:encrypted (ve '/' → '__')
        String bundle = ivHex16 + ":" + salt + ":" + encryptedB64;
        return bundle.replace("/", "__");
    }

    // === helpers ===
    private static String substr(String s, int start, int len) {
        int end = Math.min(s.length(), start + len);
        return s.substring(start, end);
    }

    private static String sha1Hex(String s) {
        return digestHex("SHA-1", s.getBytes(StandardCharsets.UTF_8));
    }

    private static String sha256Hex(String s) {
        return digestHex("SHA-256", s.getBytes(StandardCharsets.UTF_8));
    }

    private static String digestHex(String algo, byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance(algo);
            byte[] d = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] truncateTo(byte[] src, int n) {
        byte[] out = new byte[n];
        System.arraycopy(src, 0, out, 0, Math.min(src.length, n));
        return out;
    }

    private static byte[] aes256CbcEncrypt(byte[] plain, byte[] key32, byte[] iv16) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(key32, "AES"),
                    new IvParameterSpec(iv16));
            return cipher.doFinal(plain);
        } catch (Exception e) {
            throw new RuntimeException("AES-256-CBC encrypt failed", e);
        }
    }
}
