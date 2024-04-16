package util;

import com.badlogic.gdx.Gdx;

public class Text {
    public static String read(int ID) {
        String path;
        if (ID == 1) path = "ratText";
        else if (ID == 2) path = "catText";
        else if (ID == 3) path = "ratShadowText";
        else if (ID == 4) path = "catShadowText";
        else if (ID == 5) path = "laserPointer";
        else if (ID == 6) path = "hardCassette";
        else if (ID == 7) path = "nightmareCandy0";
        else return "";
        return Gdx.files.local("text/" + path + ".txt").readString();
    }
}
