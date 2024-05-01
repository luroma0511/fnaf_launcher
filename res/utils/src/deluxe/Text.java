package deluxe;

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
        else if (ID == 7) path = "flashDebug";
        else if (ID == 8) path = "hitboxDebug";
        else if (ID == 9) path = "noJumpscares";
        else if (ID == 10) path = "freeScroll";
        else if (ID == 11) path = "infiniteNight";
        else if (ID == 12) path = "restartOnJumpscare";
        else if (ID == 13) return "The Main Cast";
        else if (ID == 14) return "The Main Cast w/ Laser Pointer";
        else if (ID == 15) return "The Main Cast w/ Hard Cassette";
        else if (ID == 16) return "The Main Cast All Challenges";
        else if (ID == 17) return "The Shadow Cast";
        else if (ID == 18) return "The Shadow Cast w/ Laser Pointer";
        else if (ID == 19) return "The Shadow Cast w/ Hard Cassette";
        else if (ID == 20) return "The Shadow Cast All Challenges";
        else if (ID == -1) path = "unavailable";
        else return "";
        return Gdx.files.local("text/" + path + ".txt").readString();
    }
}
