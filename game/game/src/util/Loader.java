package util;

import java.io.InputStream;
import java.nio.charset.Charset;

public class Loader {
    private static final ClassLoader loader = ClassLoader.getSystemClassLoader();

    public static String loadFile(String file){
        try (InputStream inputStream = loader.getResourceAsStream(file)){
            assert inputStream != null;
            int length = inputStream.available();
            byte[] bytes = new byte[length];
            for (int i = 0; i < bytes.length; i++){
                bytes[i] = (byte) inputStream.read();
            }
            return new String(bytes, Charset.defaultCharset());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
