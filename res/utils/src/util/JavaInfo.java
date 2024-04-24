package util;

public interface JavaInfo {
    String home = System.getProperty("java.home");
    String jre = System.getProperty("java.version");
}