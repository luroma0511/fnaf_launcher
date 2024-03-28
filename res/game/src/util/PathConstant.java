package util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathConstant {
    private static Path projectDir = Paths.get("").toAbsolutePath();
    private static String path;
    private static final String javaPath = System.getProperty("java.home");
    private static final String jre = System.getProperty("java.version");

    public static void fixPath() {
        if (projectDir.getFileName().toString().equals("candys3deluxe")){
            path = projectDir.resolve("game").resolve("assets") + "\\";
            return;
        }
        while (!projectDir.getFileName().toString().equals("game")){
            projectDir = projectDir.getParent();
        }
        path = projectDir.resolve("assets") + "/";
    }

    public static String getAssetsPath(){
        return path;
    }

    public static String getProjectPath(){
        return projectDir.toString() + "/";
    }

    public static String getJavaPath(){
        return javaPath;
    }

    public static String getJre(){
        return jre;
    }
}
