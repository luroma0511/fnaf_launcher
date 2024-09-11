package util;

import com.badlogic.gdx.Gdx;

public class Text {
    public static String read(String dir, String file) {
        return Gdx.files.local("game/text/" + dir + "/" + file).readString();
    }
}
