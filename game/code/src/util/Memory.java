package util;

public class Memory {
    private static long totalMemory;
    private static long freeMemory;
    private static long usedMemory;

    public static void update(){
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total - free;

        totalMemory = total / (1024 * 1024);
        freeMemory = free / (1024 * 1024);
        usedMemory = used / (1024 * 1024);
    }

    public static String getTotalMemory(){
        return "Total Memory: " + totalMemory + "MB";
    }

    public static String getFreeMemory(){
        return "Free Memory: " + freeMemory + "MB";
    }

    public static String getUsedMemory(){
        return "Used Memory: " + usedMemory + "MB";
    }
}
