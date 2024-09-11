package candys3.Game.Objects.Functions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import util.SoundHandler;
import util.Time;
import util.VideoManager;

public class Jumpscare {
    static final StringBuilder sb = new StringBuilder();
    static float delay;
    static boolean begin;
    static boolean flip;

    public static void reset(){
        delay = 0;
        begin = false;
        sb.delete(0, sb.length());
        VideoManager.reset();
    }

    public static void set(String jumpscare, float delay, boolean flip){
        Jumpscare.flip = flip;
        Jumpscare.delay = delay;
        VideoManager.setRequest(jumpscare);
        sb.append(jumpscare);
    }

    public static void set(String jumpscare, float delay){
        set(jumpscare, delay, false);
    }

    public static void set(String jumpscare){
        set(jumpscare, 0, false);
    }

    public static boolean render(SoundHandler soundHandler, SpriteBatch batch){
        if (!begin) {
            soundHandler.stopAllSounds();
            begin = true;
        }
        if (delay == 0) return VideoManager.render(batch, flip, false, 1280, 720);
        else delay = Time.decreaseTimeValue(delay, 0, 1);
        return true;
    }
}
