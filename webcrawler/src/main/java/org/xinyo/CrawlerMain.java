package org.xinyo;

import org.xinyo.code.Crawler;
import org.xinyo.common.CommonUtils;
import org.xinyo.common.Config;
import org.xinyo.common.Data;

import java.util.ArrayList;
import java.util.List;

public class CrawlerMain {
    public static void main(String[] args) {
        // 1. 初始化参数
        Config.initConfig(CommonUtils.getFilePath("config.properties"));
//        Data.initUrl("https://movie.douban.com/tag/#/");
        List<String> urls = new ArrayList<>();
        for (int i = 1319529; i < 1500000; i++) {
            urls.add(String.format("http://api.douban.com/v2/movie/subject/%s?apikey=0df993c66c0c636e29ecbb5344252a4a", i));
        }

        Data.initUrlAndFilter(urls);

        // 2. 启动线程
        Crawler crawler = new Crawler();
        crawler.startCrawler();

        // 3. 启动线程监听
        crawler.monitor();

    }
}
