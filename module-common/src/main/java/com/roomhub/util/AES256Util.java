package com.roomhub.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public final class AES256Util {
    private static final String ALG = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256; // bits
    private static final int IV_SIZE = 12; // 96 bits recommended for GCM
    private static final int TAG_LENGTH_BIT = 128; // Auth tag length
    private static final SecureRandom RANDOM = new SecureRandom();

    private AES256Util() {
    }

    /**
     * Generate a random 256-bit key (base64 encoded) -- use for initial key
     * creation.
     */
    public static String generateBase64Key() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALG);
        keyGen.init(KEY_SIZE, RANDOM);
        SecretKey key = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * 암호화
     */
    public static String encrypt(String base64Key, String plainText) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALG);

        byte[] iv = new byte[IV_SIZE];
        RANDOM.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

        byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Prepend IV to ciphertext: [IV (12)] [CIPHERTEXT+TAG]
        ByteBuffer bb = ByteBuffer.allocate(iv.length + cipherBytes.length);
        bb.put(iv);
        bb.put(cipherBytes);
        return Base64.getEncoder().encodeToString(bb.array());
    }

    /**
     * 복호화
     */
    public static String decrypt(String base64Key, String base64IvAndCipher) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALG);

        byte[] ivAndCipher = Base64.getDecoder().decode(base64IvAndCipher);
        if (ivAndCipher.length < IV_SIZE) {
            throw new IllegalArgumentException("Invalid input: too short");
        }

        byte[] iv = new byte[IV_SIZE];
        byte[] cipherBytes = new byte[ivAndCipher.length - IV_SIZE];
        System.arraycopy(ivAndCipher, 0, iv, 0, IV_SIZE);
        System.arraycopy(ivAndCipher, IV_SIZE, cipherBytes, 0, cipherBytes.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

        byte[] plainBytes = cipher.doFinal(cipherBytes);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }
}
