package me.flyray.bsin.blockchain.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密钥加密工具类
 * 用于私钥的安全加密和解密
 */
public class KeyEncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final int SALT_LENGTH = 32;

    /**
     * 生成随机盐值
     * @return Base64编码的盐值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 根据DID和盐值生成加密密钥
     * @param did DID标识符
     * @param salt 盐值
     * @return AES密钥
     */
    private static SecretKey deriveKey(String did, String salt) {
        try {
            // 使用DID和盐值创建种子
            String seed = did + ":" + salt;
            byte[] keyBytes = new byte[32]; // 256位密钥
            
            // 使用SHA-256派生密钥
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(seed.getBytes(StandardCharsets.UTF_8));
            System.arraycopy(hash, 0, keyBytes, 0, 32);
            
            return new SecretKeySpec(keyBytes, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Failed to derive encryption key", e);
        }
    }

    /**
     * 加密私钥
     * @param privateKey 私钥明文
     * @param did DID标识符
     * @param salt 盐值
     * @return Base64编码的加密数据
     */
    public static String encryptPrivateKey(String privateKey, String did, String salt) {
        try {
            SecretKey secretKey = deriveKey(did, salt);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            
            // 生成随机IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            byte[] encryptedData = cipher.doFinal(privateKey.getBytes(StandardCharsets.UTF_8));
            
            // 将IV和加密数据合并
            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt private key", e);
        }
    }

    /**
     * 解密私钥
     * @param encryptedPrivateKey Base64编码的加密私钥
     * @param did DID标识符
     * @param salt 盐值
     * @return 私钥明文
     */
    public static String decryptPrivateKey(String encryptedPrivateKey, String did, String salt) {
        try {
            SecretKey secretKey = deriveKey(did, salt);
            byte[] combined = Base64.getDecoder().decode(encryptedPrivateKey);
            
            // 分离IV和加密数据
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedData = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt private key", e);
        }
    }

    /**
     * 验证加密解密功能
     * @param originalData 原始数据
     * @param did DID标识符
     * @param salt 盐值
     * @return 验证是否成功
     */
    public static boolean verifyEncryption(String originalData, String did, String salt) {
        try {
            String encrypted = encryptPrivateKey(originalData, did, salt);
            String decrypted = decryptPrivateKey(encrypted, did, salt);
            return originalData.equals(decrypted);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成测试用的AES密钥
     * @return Base64编码的密钥
     */
    public static String generateAESKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate AES key", e);
        }
    }

    /**
     * 使用AES加密任意字符串
     * @param plainText 待加密的明文字符串
     * @param did DID标识符，用于派生密钥
     * @param salt 盐值，用于派生密钥
     * @return Base64编码的加密数据
     */
    public static String encryptString(String plainText, String did, String salt) {
        try {
            SecretKey secretKey = deriveKey(did, salt);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            
            // 生成随机IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // 将IV和加密数据合并
            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt string", e);
        }
    }

    /**
     * 使用AES解密字符串
     * @param encryptedData Base64编码的加密数据
     * @param did DID标识符，用于派生密钥
     * @param salt 盐值，用于派生密钥
     * @return 解密后的明文字符串
     */
    public static String decryptString(String encryptedData, String did, String salt) {
        try {
            SecretKey secretKey = deriveKey(did, salt);
            byte[] combined = Base64.getDecoder().decode(encryptedData);
            
            // 分离IV和加密数据
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            
            byte[] decryptedData = cipher.doFinal(encrypted);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt string", e);
        }
    }
} 