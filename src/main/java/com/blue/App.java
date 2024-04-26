package com.blue;

/**
 * Hello world!
 */

import java.io.File;

public class App {
    private static final int THREAD_COUNT = 4; // 线程数
    private static long maxSize = 0;
    private static File largestFile = null;

    public static void main(String[] args) throws InterruptedException {
        String path = "c:/"; // 指定要搜索的目录
        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            search(directory);
            if (largestFile != null) {
                System.out.println("最大的文件是：" + largestFile.getAbsolutePath());
                System.out.println("文件大小是：" + largestFile.length() + " bytes");
            } else {
                System.out.println("没有找到文件！");
            }
        }
    }

    private static void search(File directory) throws InterruptedException {
        // 获取目录下的所有文件和子目录
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        // 计算每个线程需要处理的文件数
        int perThreadCount = files.length / THREAD_COUNT;

        // 创建线程数组
        Thread[] threads = new Thread[THREAD_COUNT];

        // 启动线程
        for (int i = 0; i < THREAD_COUNT; i++) {
            int startIndex = i * perThreadCount;
            int endIndex = i == THREAD_COUNT - 1 ? files.length : (i + 1) * perThreadCount;

            threads[i] = new Thread(() -> {
                long max = 0;
                File largest = null;

                for (int j = startIndex; j < endIndex; j++) {
                    File file = files[j];
                    if (file.isFile() && file.length() > max) {
                        max = file.length();
                        largest = file;
                    } else if (file.isDirectory()) {
                        try {
                            search(file); // 递归搜索子目录
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // 使用同步代码块更新全局变量
                synchronized (App.class) {
                    if (max > maxSize) {
                        maxSize = max;
                        largestFile = largest;
                    }
                }
            });

            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
    }
}