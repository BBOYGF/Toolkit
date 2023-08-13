package utils.crypto;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.crawler.CrawlerUtil;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;


/**
 * @Author guofan
 * @Create 2023/8/9
 */
class CryptoUtilTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void cryptoTest() throws Exception {
        // 生成密钥对
        KeyPair keyPair = CryptoUtil.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String publicKeyString = CryptoUtil.getPublicKeyString(publicKey);
        String privateKeyString = CryptoUtil.getPrivateKeyString(privateKey);
        logger.info("公钥是:{}", publicKeyString);
        logger.info("私钥是:{}", privateKeyString);
        PublicKey publicKey1 = CryptoUtil.loadPublicKey(publicKeyString);
        PrivateKey privateKey1 = CryptoUtil.loadPrivateKey(privateKeyString);

        // 要加密的明文
        String plaintext = "测试一下是否被加试测试测测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试";
        byte[] bytes = plaintext.getBytes(StandardCharsets.UTF_8);
        logger.info("当前文件长度:{}", bytes.length);

        // 加密
        byte[] ciphertext = CryptoUtil.encrypt(plaintext, publicKey1);

        // 解密
        String decryptedText = CryptoUtil.decrypt(ciphertext, privateKey);

        // 输出结果
        System.out.println("Plaintext: " + plaintext);
        System.out.println("Ciphertext: " + new String(ciphertext, StandardCharsets.UTF_8));
        System.out.println("Decrypted Text: " + decryptedText);
    }
}
