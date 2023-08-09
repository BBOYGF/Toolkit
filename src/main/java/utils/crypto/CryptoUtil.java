package utils.crypto;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加密工具类 封装一些常用的加密算法
 * <p>
 * 使用方法
 * // 生成密钥对
 * KeyPair keyPair = CryptoUtil.generateKeyPair();
 * PublicKey publicKey = keyPair.getPublic();
 * PrivateKey privateKey = keyPair.getPrivate();
 * <p>
 * String publicKeyString = CryptoUtil.getPublicKeyString(publicKey);
 * String privateKeyString = CryptoUtil.getPrivateKeyString(privateKey);
 * logger.info("公钥是:{}", publicKeyString);
 * logger.info("私钥是:{}", privateKeyString);
 * PublicKey publicKey1 = CryptoUtil.loadPublicKey(publicKeyString);
 * PrivateKey privateKey1 = CryptoUtil.loadPrivateKey(privateKeyString);
 * <p>
 * // 要加密的明文
 * String plaintext = "测试一下是否被加密";
 * <p>
 * // 加密
 * byte[] ciphertext = CryptoUtil.encrypt(plaintext, publicKey1);
 * <p>
 * // 解密
 * String decryptedText = CryptoUtil.decrypt(ciphertext, privateKey);
 * <p>
 * // 输出结果
 * System.out.println("Plaintext: " + plaintext);
 * System.out.println("Ciphertext: " + new String(ciphertext, StandardCharsets.UTF_8));
 * System.out.println("Decrypted Text: " + decryptedText);
 *
 * @Author guofan
 * @Create 2023/8/9
 */
public class CryptoUtil {

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] encrypt(String plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
    }

    public static String decrypt(byte[] ciphertext, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] plaintextBytes = cipher.doFinal(ciphertext);
        return new String(plaintextBytes, StandardCharsets.UTF_8);
    }

    public static PublicKey loadPublicKey(String publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey loadPrivateKey(String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static String getPublicKeyString(PublicKey publicKey) {
        byte[] publicKeyBytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }

    public static String getPrivateKeyString(PrivateKey privateKey) {
        byte[] privateKeyBytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }
}
