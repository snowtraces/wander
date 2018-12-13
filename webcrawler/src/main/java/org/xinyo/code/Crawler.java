package org.xinyo.code;

import org.xinyo.common.Config;
import org.xinyo.common.Data;
import org.xinyo.entity.WebUrl;

import java.io.InputStream;

import static org.xinyo.common.Constant.THREAD_NUMBER;
import static org.xinyo.common.FileUtils.log;
import static org.xinyo.common.FileUtils.save;
import static org.xinyo.common.RequestUtils.request;

public class Crawler {
    private static final String SINGLE = "single";
    private volatile int activeThread = 0;

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
            System.out.println("开始请求: " + webUrl);
            InputStream inputStream = request(webUrl);
            save(webUrl, inputStream);
            System.out.println("完成请求: " + webUrl);
            log(webUrl);
            wake();
        } else {
            sleep();
        }
    }

    public void monitor(){
        try {
            while (true) {
                Thread.sleep(10000);
                System.err.println("当前活动线程数: " + activeThread);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 等待
     * @throws InterruptedException
     */
    private void sleep() throws InterruptedException {
        synchronized (SINGLE) {
            if(activeThread > 0){
                activeThread--;
                SINGLE.wait();
            }
        }
    }

    /**
     * 唤醒
     */
    private void wake() {
        synchronized (SINGLE) {
            while(activeThread < Config.getIntValue(THREAD_NUMBER)){
                activeThread++;
                SINGLE.notify();
            }
        }
    }
}
