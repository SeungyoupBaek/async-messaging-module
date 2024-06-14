package com.youp.async_messaging_module.monitor;

public class MemoryMonitor {

    public static boolean isMemoryUsageHigh() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        return ((double) usedMemory / maxMemory) > 0.8; // 80% threshold
    }
}
