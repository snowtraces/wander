package org.xinyo.code;

import org.xinyo.common.Config;

import java.util.HashSet;
import java.util.Set;

import static org.xinyo.common.Constant.THREAD_NUMBER;

public class Crawler {
    private Set<String> unCrawledSet = new HashSet();

    public void startCrawler() {
        // 1. 获取目标线程数
        int threadNumber = Config.getIntValue(THREAD_NUMBER);

        // 2. 初始化线程
        initThread(threadNumber);
    }

    public void initThread(int threadNumber) {
        for (int i = 0; i < threadNumber; i++) {
            new Thread(() -> {
                while (true) {
                    // 执行爬虫
                    doCrawler();
                }
            }, "thread-" + i).start();
        }
    }

    public void doCrawler(){




    }
}
