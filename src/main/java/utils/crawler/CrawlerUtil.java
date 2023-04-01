package utils.crawler;


import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import utils.enums.LoadWebType;

import static utils.enums.LoadWebType.GET;

/**
 * 爬虫工具类
 * 1、下载网页（get，post，加参数，加头文件）
 * 2、筛选网页 （）
 * 3、保存数据，到（excel，数据库，输出，文本，csv）
 *
 * @Author guofan
 * @Create 2022/2/24
 */

public class CrawlerUtil {
    private Logger log = LoggerFactory.getLogger(getClass());
    private OkHttpClient client;

    /**
     * 下载网页
     *
     * @param url         网页地址
     * @param type        请求类型
     * @param requestBody post请求体
     * @return 结果
     */
    public Response loadWebPage(String url, LoadWebType type, RequestBody requestBody) {
        if (client == null) {
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", 1080));
            final String proxyUsername = "guo";
            final String proxyPassword = "guo";
            Authenticator proxyAuthenticator = new Authenticator() {
                @Override
                public Request authenticate(Route route, Response response) {
                    // 使用Basic认证生成代理服务器需要的凭据
                    String credential = Credentials.basic(proxyUsername, proxyPassword);
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                }
            };
            client = new OkHttpClient.Builder()
                    .proxy(proxy)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
        }
        //构建参数
        Request request = null;
        if (GET.equals(type)) {
            request = new Request.Builder().url(url)
//                    .addHeader("accept-encoding", "gzip, deflate, br")
                    .get()
                    .build();
        } else {
            request = new Request.Builder().url(url).post(requestBody).build();
        }
        Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            log.error("请求地址{}发生了异常：", url, e);
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 解析网页
     *
     * @return Document document 元素
     */
    public Document paresWebPage(Response response) {
        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        String bodyString = null;
        try {
            bodyString = body.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document document = Jsoup.parse(bodyString);
        return document;
    }

    /**
     * 解析网页
     * 根据回掉接口解析
     */
    public void paresWebPage(Response response, ParseLine<?> parseLine) {
        ResponseBody body = response.body();
        if (body == null) {
            return;
        }
        InputStream inputStream = body.byteStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                parseLine.parseLine(line);
            }
        } catch (IOException e) {
            log.error("读取{}行发生了异常", line, e);
        }

    }

    public static String unicodeToUtf8(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

}
