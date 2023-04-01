package utils.crawler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.enums.LoadWebType;

/**
 * @Author guofan
 * @Create 2023/4/1
 */

class CrawlerUtilTest {

    private Logger logger;
    private CrawlerUtil crawlerUtil;
    private String url;

    @BeforeEach
    void before() {
        crawlerUtil = new CrawlerUtil();
        logger = LoggerFactory.getLogger(getClass());
        url = "https://coinmarketcap.com/?page=1";
    }

    @Test
    void loadWebPage() {
        logger.info("测试");
        Response response = crawlerUtil.loadWebPage(url, LoadWebType.GET, null);
        String header = response.header("Content-Encoding");
        Document document = crawlerUtil.paresWebPage(response);
        Element body = document.body();
        Document doc = Jsoup.parse(body.html());
        Elements coins = doc.select("tbody tr");
        for (Element coin : coins) {
            String name = coin.select("td:nth-child(3) div a").text();
            String price = coin.select("td:nth-child(5) a").text();
           logger.info("名字：{} 价格：{}",name,price);
        }

    }
}
