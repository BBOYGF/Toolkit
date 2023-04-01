package utils.crawler;

/**
 * 行解析
 *
 * @Author guofan
 * @Create 2022/2/25
 */
public interface ParseLine<T> {
    /**
     * 行解析返回对象
     *
     * @param line
     * @return
     */
    void parseLine(String line);
}
