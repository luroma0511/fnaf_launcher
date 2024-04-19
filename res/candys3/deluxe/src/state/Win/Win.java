package state.Win;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import core.Candys3Deluxe;
import util.CameraManager;
import util.ImageManager;
import util.SoundManager;
import util.Time;

public class Win {
    private float cooldown;
    private float alpha;
    private float frame;

    public void load(){
        cooldown = 5;
        alpha = 0;
        frame = 0;
        SoundManager.add("win");
        ImageManager.add("game/Clock");
    }

    public void update(){
        frame = Time.increaseTimeValue(frame, 4, 60);
        if (frame == 4) frame = 0;
        cooldown = Time.decreaseTimeValue(cooldown, 0, 1);
        if (cooldown == 0) {
            alpha = Time.decreaseTimeValue(alpha, 0, 1.5f);
            SoundManager.setVolume("win", alpha);
            if (alpha != 0) return;
            Candys3Deluxe.stateManager.setState((byte) 0);
            SoundManager.stop("win");
        } else {
            if (alpha == 0) SoundManager.play("win");
            alpha = Time.increaseTimeValue(alpha, 1, 1.5f);
        }
    }

    public void render(SpriteBatch batch){
        CameraManager.setOrigin();
        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        batch.enableBlending();
        batch.begin();
        batch.setColor(1, 1, 1, alpha);
        batch.draw(ImageManager.getRegion("game/Clock", 256, (byte) frame), 512, 232);
    }
}
