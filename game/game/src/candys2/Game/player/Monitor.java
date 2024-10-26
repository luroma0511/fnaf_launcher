package candys2.Game.player;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import util.InputManager;
import util.SoundHandler;
import util.TextureHandler;
import util.Time;

public class Monitor {
    private boolean loaded;
    public int activeCamera;
    public boolean switched;
    public float glitchCooldown;
    public float glitchFrame;
    public boolean error;

    public int[] xPos = new int[]{
            684, 664, 729, 791, 888, 931
    };
    public int[] yPos = new int[]{
            171, 216, 410, 357, 266, 183
    };

    public int[] xCheatPos = new int[]{
            834, 725, 874, 1072, 1212, 1195,
            959, 1004, 1049
    };

    public int[] yCheatPos = new int[]{
            106, 203, 351, 360, 249, 118,
            281, 196, 128
    };

    public TextureRegion[] regions = new TextureRegion[6];
    public TextureRegion[] mapDebugMode1Regions = new TextureRegion[4];

    public void reset(){
        activeCamera = 1;
        switched = false;
        glitchCooldown = 0;
        error = false;
    }

    public void load(TextureHandler textureHandler, boolean mapDebug){
        if (loaded) return;
        loaded = true;
        for (int i = 0; i < 6; i++) regions[i] = new TextureRegion(textureHandler.get("game/gui/camera" + (i + 1)));
        if (!mapDebug) return;
        for (int i = 0; i < 4; i++) mapDebugMode1Regions[i] = new TextureRegion(textureHandler.get("game/cheat1"));
    }

    public void input(InputManager inputManager, SoundHandler soundHandler, int roomFrame){
        if (error) {
            if (roomFrame == 17 && !soundHandler.isPlaying("glitch")){
                soundHandler.play("glitch");
                soundHandler.setSoundEffect(SoundHandler.LOOP, "glitch", 1);
            } else if (roomFrame < 17 && soundHandler.isPlaying("glitch")){
                soundHandler.stop("glitch");
            }
            if (roomFrame == 17 && inputManager.mouseOver(561, 318, 75, 23) && inputManager.isLeftPressed()) {
                error = false;
                glitchCooldown = 3;
                glitchFrame = 0;
                soundHandler.stop("glitch");
                soundHandler.play("cameraBeep");
            }
            return;
        }
        if (glitchCooldown > 0 || roomFrame < 17) return;
        boolean clicked = inputManager.isLeftPressed();
        switched = false;
        for (int i = 0; i < 6; i++) {
            if (inputManager.mouseOver(xPos[i] * 1.25f, yPos[i] * 0.9375f, 48, 32) && clicked){
                if (activeCamera == i + 1) break;
                activeCamera = i + 1;
                switched = true;
                soundHandler.play("cameraBeep");
                break;
            }
        }
    }

    public void update(SoundHandler soundHandler){
        if (glitchCooldown == 0) return;
        glitchFrame += Time.getDelta() * 5;
        if (glitchFrame >= 4) glitchFrame = 0;
        glitchCooldown -= Time.getDelta();
        if (glitchCooldown > 0) return;
        glitchCooldown = 0;
        soundHandler.play("phoneBeep");
    }

    public void dispose(){
        dispose(regions);
        dispose(mapDebugMode1Regions);
        loaded = false;
    }

    private void dispose(TextureRegion[] regions){
        for (int i = 0; i < regions.length; i++){
            if (regions[i] == null) continue;
            regions[i].getTexture().dispose();
            regions[i] = null;
        }
    }
}
