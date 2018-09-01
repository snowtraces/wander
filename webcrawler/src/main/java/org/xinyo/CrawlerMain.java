package org.xinyo;

import org.xinyo.code.Crawler;
import org.xinyo.common.CommonUtils;
import org.xinyo.common.Config;
import org.xinyo.common.Data;

import static org.xinyo.common.Constant.URL_TYPE_TEXT;

public class CrawlerMain {
    public static void main(String[] args) {
        // 1. 初始化参数
        Config.initConfig(CommonUtils.getFilePath("config.properties"));
        Data.initUrl("http://www.shicixuexi.com");

        // 2. 启动线程
        Crawler crawler = new Crawler();
        crawler.startCrawler();

        // 3. 启动线程监听
        crawler.monitor();

    }
}
