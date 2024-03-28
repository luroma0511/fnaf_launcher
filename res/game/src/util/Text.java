package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Text {
    public static String read(String path) {
        String data;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(PathConstant.getProjectPath() + "text/" + path + ".txt"))){
            data = bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}
