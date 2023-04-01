package utils.translate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 翻译工具类
 *
 * @Author guofan
 * @Create 2022/2/27
 */
public class TranslateUtil {

    private Logger log = LoggerFactory.getLogger(getClass());

    private static final String URL = "https://openapi.youdao.com/api";

    private static final String APP_KEY = "7861a74b25ad6701";

    private static final String APP_SECRET = "XgVMkEsKtjPQpq4oH1toffNB6VSlQ72m";

    public String translation(String content) {
        String q = content;
        String salt = String.valueOf(System.currentTimeMillis());
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        String signStr = APP_KEY + truncate(q) + salt + curtime + APP_SECRET;
        String sign = getDigest(signStr);
        RequestBody requestBody = new FormBody.Builder()
                .add("from", "en")
                .add("to", "zh-CHS")
                .add("signType", "v3")
                .add("curtime", curtime)
                .add("appKey", APP_KEY)
                .add("q", q)
                .add("salt", salt)
                .add("sign", sign)
                .build();

        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(URL).post(requestBody).build();
        Call call = httpClient.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            ResponseBody responseBody = response.body();
            String string = responseBody.string();

            if (string == null || "".equals(string)) {
                return "异常！没有获取到内容！";
            }
            JsonObject jsonObject = JsonParser.parseString(string).getAsJsonObject();
            JsonElement element = jsonObject.get("translation");
            String asString = element.getAsString();
            log.info("返回结果是：{}", asString);
            return asString;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("翻译{}发生了异常：", content, e);
            return "异常！";
        }
    }

    /**
     * 生成加密字段
     */
    private String getDigest(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest mdInst = MessageDigest.getInstance("SHA-256");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * @param result 音频字节流
     * @param file   存储路径
     */
    private void byte2File(byte[] result, String file) {
        File audioFile = new File(file);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(audioFile);
            fos.write(result);

        } catch (Exception e) {
            log.info(e.toString());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        String result;
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }
}
