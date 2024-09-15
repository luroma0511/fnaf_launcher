package util;

import java.io.*;

public class FileUtils {
    private static File file;
    private static File scoreFile;

    public static void init(String game){
        System.out.println(Constants.savePath);
        File dir = new File(Constants.savePath + game);
        if (!dir.exists()) dir.mkdirs();
        file = new File(dir.getPath() + "/Guest.json");
        scoreFile = new File(dir.getPath() + "/GuestScore.json");
    }

    public static String readFile(int id){
        File file = getFile(id);
        if (!fileExists(file)) return "";
        StringBuilder sbJson = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = reader.readLine()) != null) sbJson.append(line).append("\n");
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        return sbJson.toString();
    }

    private static boolean fileExists(File file){
        if (!file.exists()) {
            try {
                file.createNewFile();
                return false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    public static <T> void write(JSONHandler jsonHandler, T data, int id){
        File file = getFile(id);
        fileExists(file);
        String value = jsonHandler.write(data);
        System.out.println(value);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            writer.write(value);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private static File getFile(int id){
        if (id == 0) return file;
        return scoreFile;
    }
}