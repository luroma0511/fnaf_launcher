package candys2.Game.player;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import util.InputManager;
import util.SoundHandler;
import util.TextureHandler;
import util.Time;

import java.util.Arrays;

public class Telephone {
    public boolean ringing;
    public int location;
    private float clickDelay;
    private float timeToRing;
    public int side;
    public int[] cooldownSeconds = new int[6];
    public float[] cooldowns = new float[6];
    public int[] status = new int[6];
    public int[] xPos = new int[]{
            615, 615, 663, 906, 970, 972
    };
    public int[] yPos = new int[]{
            71, 216, 410, 393, 230, 72
    };

    public TextureRegion[] regions = new TextureRegion[6];
    public TextureRegion[] cooldownRegions = new TextureRegion[6];

    public void reset(){
        Arrays.fill(status, 0);
        Arrays.fill(cooldowns, 0);
        Arrays.fill(cooldownSeconds, 0);
        side = 0;
        ringing = false;
        timeToRing = 0;
        location = 0;
        clickDelay = 0;
    }

    public void load(TextureHandler textureHandler){
        Arrays.fill(regions, new TextureRegion(textureHandler.get("game/gui/phone")));
        Arrays.fill(cooldownRegions, new TextureRegion(textureHandler.get("game/gui/phoneCooldown")));
    }

    public void input(InputManager inputManager, SoundHandler soundHandler, int activeCamera, boolean faultyPhones){
        clickDelay -= Time.getDelta();
        if (clickDelay <= 0) clickDelay = 0;
        boolean clicked = inputManager.isLeftPressed() && clickDelay == 0;
        int state = status[activeCamera - 1];
        if (inputManager.mouseOver(xPos[activeCamera - 1] * 1.25f, yPos[activeCamera - 1] * 0.9375f, 32, 32)
                && state != 2 && clicked){
            if (state == 0 && !ringing && cooldowns[activeCamera - 1] == 0 && cooldownSeconds[activeCamera - 1] == 0){
                location = activeCamera;
                status[location - 1] = 1;
                if (location <= 3) side = 0;
                else side = 1;
            } else if (state == 1) {
                status[location - 1] = 0;
                if (faultyPhones){
                    cooldowns[location - 1] = 1;
                    cooldownSeconds[location - 1] = 5;
                }
            }
            else return;
            clickDelay = 0.25f;
            ringing = !ringing;
            soundHandler.play("phoneBeep");
        }
    }

    public void update(SoundHandler soundHandler, boolean faultyPhones, boolean active){
        if (ringing && timeToRing < 6) timeToRing += Time.getDelta();
        else timeToRing = 0;

        if (timeToRing > 6) timeToRing = 6;

        if (timeToRing >= 0.5f && !soundHandler.isPlaying("phoneRinging")) {
            soundHandler.play("phoneRinging");
            soundHandler.setSoundEffect(SoundHandler.LOOP, "phoneRinging", 1);
            soundHandler.setSoundEffect(SoundHandler.PAN, "phoneRinging", side == 0 ? -0.5f : 0.5f, -2);
        } else if (timeToRing < 0.5f) soundHandler.stop("phoneRinging");

        if (timeToRing == 6 && faultyPhones){
            status[location - 1] = 0;
            cooldowns[location - 1] = 1;
            cooldownSeconds[location - 1] = 5;
            ringing = !ringing;
            if (active) soundHandler.play("phoneBeep");
        }

        for (int i = 0; i < cooldowns.length; i++){
            if (cooldowns[i] == 0) continue;
            if (status[i] == 2) {
                cooldowns[i] = 0;
                cooldownSeconds[i] = 0;
            }
            cooldowns[i] -= Time.getDelta();
            if (cooldowns[i] <= 0){
                if (cooldownSeconds[i] > 0){
                    cooldownSeconds[i]--;
                    cooldowns[i]++;
                    if (active) soundHandler.play("cameraBeep");
                    return;
                }
                cooldowns[i] = 0;
                if (active) soundHandler.play("phoneBeep");
            }
        }
    }

    public void destroy(SoundHandler soundHandler, int location){
        int state = status[location - 1];
        if (state == 1) ringing = false;
        status[location - 1] = 2;
        soundHandler.play("phoneBreak");
        soundHandler.setSoundEffect(SoundHandler.VOLUME, "phoneBreak", 0.6f);
    }

    public void dispose(){
        dispose(regions);
        dispose(cooldownRegions);
    }

    private void dispose(TextureRegion[] regions){
        for (int i = 0; i < regions.length; i++){
            if (regions[i] == null) continue;
            regions[i].getTexture().dispose();
            regions[i] = null;
        }
    }
}
