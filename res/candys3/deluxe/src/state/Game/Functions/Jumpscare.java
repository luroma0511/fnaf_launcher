package state.Game.Functions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import util.SoundManager;
import util.Time;
import util.VideoManager;

public class Jumpscare {
    static StringBuilder sb = new StringBuilder();
    static float play;
    static boolean begin;

    public static void reset(){
        play = 0;
        begin = false;
        sb.delete(0, sb.length());
        VideoManager.reset();
    }

    public static void set(String jumpscare, float play){
        Jumpscare.play = play;
        VideoManager.setRequest(jumpscare);
        sb.append(jumpscare);
    }

    public static void set(String jumpscare){
        set(jumpscare, 0);
    }

    public static boolean render(SpriteBatch batch){
        if (!begin) {
            SoundManager.stopAllSounds();
            begin = true;
        }
        if (play == 0) return VideoManager.render(batch, false, 1280, 720);
        else play = Time.decreaseTimeValue(play, 0, 1);
        return true;
    }
}
