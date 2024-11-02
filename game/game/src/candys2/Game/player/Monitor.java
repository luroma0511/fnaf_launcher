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
            746, 729, 781, 844, 926, 965
    };
    public int[] yPos = new int[]{
            140, 186, 335, 303, 220, 150
    };

    public int[] xButtonPos = new int[]{
            707, 682, 742, 924, 987, 981
    };
    public int[] yButtonPos = new int[]{
            70, 186, 335, 327, 197, 70
    };

    public TextureRegion[] regions = new TextureRegion[6];

    public void reset(){
        activeCamera = 1;
        switched = false;
        glitchCooldown = 0;
        error = false;
    }

    public void load(TextureHandler textureHandler){
        if (loaded) return;
        loaded = true;
        for (int i = 0; i < 6; i++) regions[i] = new TextureRegion(textureHandler.get("game/gui/camera" + (i + 1)));
    }

    public void input(InputManager inputManager, SoundHandler soundHandler, boolean inCamera){
        if (error) {
            if (inCamera && !soundHandler.isPlaying("glitch")){
                soundHandler.play("glitch");
                soundHandler.setSoundEffect(SoundHandler.LOOP, "glitch", 1);
            } else if (!inCamera && soundHandler.isPlaying("glitch")){
                soundHandler.stop("glitch");
            }
            if (inCamera && inputManager.mouseOver(561, 276, 75, 23) && inputManager.isLeftPressed()) {
                error = false;
                glitchCooldown = 3;
                glitchFrame = 0;
                soundHandler.stop("glitch");
                soundHandler.play("cameraBeep");
            }
            return;
        }
        if (glitchCooldown > 0 || !inCamera) return;
        boolean clicked = inputManager.isLeftPressed();
        switched = false;
        for (int i = 0; i < 6; i++) {
            if (inputManager.mouseOver(xPos[i], yPos[i], 48, 32) && clicked){
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
