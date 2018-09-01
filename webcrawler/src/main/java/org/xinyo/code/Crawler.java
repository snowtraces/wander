package org.xinyo.code;

import org.xinyo.common.Config;
import org.xinyo.common.Data;
import org.xinyo.entity.WebUrl;

import java.io.InputStream;

import static org.xinyo.common.Constant.THREAD_NUMBER;
import static org.xinyo.common.FileUtils.save;
import static org.xinyo.common.RequestUtil.request;

public class Crawler {
    private static final String SINGLE = "single";

    /**
     * 爬虫启动方法
     */
    public void startCrawler() {
        // 1. 获取目标线程数
        int threadNumber = Config.getIntValue(THREAD_NUMBER);

        // 2. 初始化线程
        initThread(threadNumber);
    }

    /**
     * 初始化线程
     *
     * @param threadNumber
     */
    public void initThread(int threadNumber) {
        for (int i = 0; i < threadNumber; i++) {
            new Thread(() -> {
                while (true) {
                    try {
                        doCrawler();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "thread-" + i).start();
        }
    }

    /**
     * 设计执行方法
     * @throws InterruptedException
     */
    public void doCrawler() throws InterruptedException {
        WebUrl webUrl = Data.getUrl();
        if (webUrl != null) {
            // 页面请求
            InputStream inputStream = request(webUrl);
            save(webUrl, inputStream);
            wake();
        } else {
            sleep();
        }
    }

    /**
     * 等待
     * @throws InterruptedException
     */
    private void sleep() throws InterruptedException {
        synchronized (SINGLE) {
            SINGLE.wait();
        }
    }

    /**
     * 唤醒
     */
    private void wake() {
        synchronized (SINGLE) {
            SINGLE.notify();
        }
    }
}
