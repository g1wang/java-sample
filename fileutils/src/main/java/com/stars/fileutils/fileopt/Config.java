package com.stars.fileutils.fileopt;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Config {

  private static Properties properties;
  private static final Logger logger = LoggerFactory.getLogger(Config.class);

  static {
    InputStream in = null;
    try {
      PropertyConfigurator
          .configure(System.getProperty("user.dir") + "/config/log4j.properties");
      properties = new Properties();
      // 读取SRC下配置文件 --- 属于读取内部文件
      // properties.load(Config.class.getResourceAsStream("/init.properties"));
      // 读取系统外配置文件 (即Jar包外文件) --- 外部工程引用该Jar包时需要在工程下创建config目录存放配置文件
      String filePath = System.getProperty("user.dir") + "/config/config.properties";
      in = new BufferedInputStream(new FileInputStream(filePath));
      properties.load(in);
    } catch (IOException e) {
      logger.error("读取配置信息出错！", e);
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  public static String getObject(String prepKey) {

    String propVal = properties.getProperty(prepKey);
    return propVal == null ? "" : properties.getProperty(prepKey);
  }

}
