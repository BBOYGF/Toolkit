package utils.crypto;

import com.google.gson.Gson;
import utils.crypto.pojo.RegisterPo;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

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

    public static final int DATA_LENGTH = 245;
    public static final int DECODER_DATA_LENGTH = 256;

    /**
     * 获取秘钥对
     *
     * @return 密钥对
     * @throws Exception
     */
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] encrypt(String plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = plaintext.getBytes(StandardCharsets.UTF_8);
        int count = bytes.length / DATA_LENGTH;
        int m = bytes.length % DATA_LENGTH;
        int k = 0;
        List<byte[]> bytesArray = new ArrayList<>();
        for (int j = 0; j < count; j++) {
            byte[] copyOfRange = Arrays.copyOfRange(bytes, k, k + DATA_LENGTH);
            byte[] encryptArray = cipher.doFinal(copyOfRange);
            bytesArray.add(encryptArray);
            k += DATA_LENGTH;
        }
        if (m != 0) {
            byte[] copyOfRange = Arrays.copyOfRange(bytes, k, k + m);
            byte[] encryptArray = cipher.doFinal(copyOfRange);
            bytesArray.add(encryptArray);
        }
        byte[] toArray = convertListToArray(bytesArray);
        return toArray;
    }

    public static String decrypt(byte[] ciphertext, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        List<byte[]> bytes = new ArrayList<>();
        int i1 = (ciphertext.length / DECODER_DATA_LENGTH);
        int i2 = ciphertext.length % DECODER_DATA_LENGTH;
        int k = 0;
        for (int i = 0; i < i1; i++) {
            byte[] bytes1 = Arrays.copyOfRange(ciphertext, k, k + DECODER_DATA_LENGTH);
            byte[] plaintextBytes = cipher.doFinal(bytes1);
            bytes.add(plaintextBytes);
            k += DECODER_DATA_LENGTH;
        }
        if (i2 != 0) {
            byte[] bytes1 = Arrays.copyOfRange(ciphertext, k, ciphertext.length - 1);
            byte[] plaintextBytes = cipher.doFinal(bytes1);
            bytes.add(plaintextBytes);
        }
        byte[] bytes1 = convertListToArray(bytes);
//        byte[] plaintextBytes = cipher.doFinal(bytes1);
        return new String(bytes1, StandardCharsets.UTF_8);
    }

    public static byte[] convertListToArray(List<byte[]> byteList) {
        // 计算目标数组的总长度
        int totalLength = 0;
        for (byte[] bytes : byteList) {
            totalLength += bytes.length;
        }

        // 创建目标数组
        byte[] result = new byte[totalLength];

        // 逐个复制元素到目标数组
        int currentIndex = 0;
        for (byte[] bytes : byteList) {
            System.arraycopy(bytes, 0, result, currentIndex, bytes.length);
            currentIndex += bytes.length;
        }

        return result;
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

    /**
     * 生成令牌
     *
     * @param transactionId 订单好
     * @param deviceId      设备号
     * @param overDate      结束数据
     * @return token 字符串
     * @throws Exception 异常
     */
    public static String generateLicence(String transactionId, String deviceId, Date overDate) throws Exception {
        RegisterPo registerPo = new RegisterPo();
        registerPo.setTransactionId(transactionId);
        registerPo.setRegistrationExpiry(overDate);
        registerPo.setDeviceId(deviceId);
        Gson gson = new Gson();
        String msgSessionStr = gson.toJson(registerPo);
        KeyPair keyPair = CryptoUtil.generateKeyPair();
        PublicKey aPublic = keyPair.getPublic();
        PrivateKey aPrivate = keyPair.getPrivate();
        String privateKeyString = CryptoUtil.getPrivateKeyString(aPrivate);
        String publicKeyString = CryptoUtil.getPublicKeyString(aPublic);
        byte[] encrypt = CryptoUtil.encrypt(msgSessionStr, aPublic);
        String encodeToString = Base64.getEncoder().encodeToString(encrypt);
        String token = encodeToString + "." + privateKeyString;
        return token;
    }

}
