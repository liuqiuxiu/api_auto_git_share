package utils;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * properties工具类
 *
 * @author 秀秀
 */
public class PropertiesUtil {
    public static Logger logger = Logger.getLogger(PropertiesUtil.class);

    public static void main(String[] args) {
        String tenant_username = PropertiesUtil.userProperties.getProperty("tenant_username");
    }

    /**
     * 定义一个静态的全局变量
     */
    public static Properties properties;//变量的配置文件
    public static Properties userProperties;//用户的配置文件
    public static Properties jdbcProperties;//jdbc的配置文件
    public static Properties fileProperties;//读取file 文件

    /**
     * 加载properties文件
     */
    static {
        loadProperties();

    }

//    /**
//     * 加载properties文件的方法
//     */
//    public static void loadProperties() {
//        properties = new Properties();
//        userProperties = new Properties();
//        jdbcProperties = new Properties();
//        try {
//            logger.info("加载文件：config.properties");
//            String path = AppUtils.getRootPath() +"/config.properties";
//            System.out.println(path);
//            properties.load(new BufferedReader(new InputStreamReader(PropertiesUtil.class.getResourceAsStream(path))));
//
//            userProperties.load(PropertiesUtil.class.getResourceAsStream("/user_info.properties"));
//            jdbcProperties.load(PropertiesUtil.class.getResourceAsStream("/jdbc.properties"));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 加载properties文件的方法
     */
    public static void loadProperties() {
        properties = new Properties();
        userProperties=new Properties();
        jdbcProperties=new Properties();
        fileProperties=new Properties();
        try {
            logger.info("加载文件：config.properties");
            properties.load(new InputStreamReader(PropertiesUtil.class.getResourceAsStream("/config.properties")));
            userProperties.load(new InputStreamReader(PropertiesUtil.class.getResourceAsStream("/user_info.properties")));
            jdbcProperties.load(new InputStreamReader(PropertiesUtil.class.getResourceAsStream("/jdbc.properties")));
            fileProperties.load(new InputStreamReader(PropertiesUtil.class.getResourceAsStream("/YOYO.zip")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取路径
     *
     * @param key 关键字
     * @return 返回值
     */
    public static String getPath(String key) {
        return properties.getProperty(key);
    }

    /**
     * 往properties文件中写入
     *
     * @param data 写入数据
     */
    public static void setProperty(Map<String, String> data) {
        OutputStream out = null;
        try {
            // 如果data不为空，则给property文件设置
            if (data != null) {
                Iterator<Entry<String, String>> iterator = data.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> entry = iterator.next();
                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    properties.put(key, value);
                    logger.info("往properties文件写入key：" + key + ",value：" + value);
                }
            }
            //创建一个输出流
            out = new FileOutputStream("config.properties");
            logger.info("正在保存properties文件");
            properties.store(out, null);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
    }
}
