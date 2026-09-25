package com.kunal.simplecrudapp.service.encryptiondecryption;


import com.kunal.simplecrudapp.dao.HybridEncryptedData;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class EncryptionDecryptionUtils {

    private  final String RSA_ALGO = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private  final String AES_ALGO = "AES/GCM/NoPadding";

    private  final int AES_KEY_SIZE = 256;
    private  final int IV_SIZE = 12;
    private  final int TAG_SIZE = 128;

    /**
     * Main Encryption Method
     */
    public  HybridEncryptedData encrypt(String plainText, PublicKey publicKey) throws Exception {
        SecretKey aesKey = generateAESKey();
        byte[] iv = generateIV();
        String encryptedPayload = encryptAES(plainText, aesKey, iv);
        String encryptedAESKey = encryptAESKey(aesKey, publicKey);
        return new HybridEncryptedData(encryptedAESKey, Base64.getEncoder().encodeToString(iv), encryptedPayload
        );
    }

    /**
     * Main Decryption Method
     */
    public  String decrypt(HybridEncryptedData request,PrivateKey privateKey) throws Exception {
        SecretKey aesKey =decryptAESKey(request.getEncryptedKey(), privateKey);
        byte[] iv = Base64.getDecoder().decode(request.getIv());
        return decryptAES(request.getPayload(), aesKey, iv);
    }

    /**
     * Generate AES Key
     */
    private  SecretKey generateAESKey() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(AES_KEY_SIZE);
        return generator.generateKey();
    }

    /**
     * Generate IV
     */
    private  byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * AES Encrypt
     */
    private  String encryptAES(String data, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_SIZE, iv));
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * AES Decrypt
     */
    private  String decryptAES(String encryptedData, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_SIZE, iv));
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * RSA Encrypt AES Key
     */
    private  String encryptAESKey(SecretKey key, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(key.getEncoded());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * RSA Decrypt AES Key
     */
    private SecretKey decryptAESKey(String encryptedKey, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] keyBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedKey));
        return new SecretKeySpec(keyBytes, "AES");
    }


    public PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    public PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

}
