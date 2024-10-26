package util;

public class Text {
    public static String read(String dir, String file) {
        String content = Loader.loadFile("res/text/" + dir + "/" + file);
        assert content != null;
        return content;
    }
}
