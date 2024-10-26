package candys3.Game.Objects.Functions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import util.*;

public class Jumpscare {
    static final StringBuilder sb = new StringBuilder();
    static float delay;
    static boolean begin;
    static boolean flip;
    static float time;
    static int width;
    static int position;
    static float frame;
    static float fps;
    static int imageIndex;
    static boolean video;

    public static void reset(){
        frame = 0;
        time = 0;
        delay = 0;
        imageIndex = 0;
        video = false;
        begin = false;
        sb.delete(0, sb.length());
        VideoManager.cancel();
        VideoManager.reset();
    }

    public static void classicSet(String jumpscare, float time, int position, int width, float fps){
        sb.append(jumpscare);
        Jumpscare.time = time;
        Jumpscare.position = position;
        Jumpscare.width = width;
        Jumpscare.fps = fps;
    }

    public static void set(String jumpscare, float delay, boolean flip){
        Jumpscare.flip = flip;
        Jumpscare.delay = delay;
        VideoManager.setRequest(jumpscare);
        sb.append(jumpscare);
        video = true;
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
        if (delay == 0) return VideoManager.render(batch, "candys3", flip, false, 1280, 720);
        else delay = Time.decreaseTimeValue(delay, 0, 1);
        return true;
    }

    public static boolean classicShadowRender(SoundHandler soundHandler, SpriteBatch batch, TextureHandler textureHandler){
        if (!begin) {
            soundHandler.stopAllSounds();
            soundHandler.play("shadowJumpscare");
            if (sb.toString().contains("Cat")) soundHandler.setSoundEffect(SoundHandler.PITCH, "shadowJumpscare", 0.925f);
            begin = true;
        }

        time = Time.decreaseTimeValue(time, 0, 1);
        if (time == 0) {
            soundHandler.stop("shadowJumpscare");
            return false;
        }

        frame = Time.increaseTimeValue(frame, 1, fps);
        if (frame == 1) {
            frame = 0;
            imageIndex = (int) (Math.random() * 6);
        }
        var textureRegion = textureHandler.getRegion(sb.toString(), width, imageIndex);
        batch.draw(textureRegion, CameraManager.getX() + position, CameraManager.getY());
        return true;
    }

    public static boolean isVideo() {
        return video;
    }
}
