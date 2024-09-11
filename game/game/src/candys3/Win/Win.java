package candys3.Win;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import core.Engine;
import util.*;

public class Win {
    private float cooldown;
    private float alpha;
    private float frame;

    public void load(SoundHandler soundHandler, TextureHandler textureHandler){
        cooldown = 5;
        alpha = 0;
        frame = 0;
        soundHandler.add("win");
        textureHandler.add("game/Clock");
    }

    public void update(Engine engine){
        var soundHandler = engine.appHandler.soundHandler;

        frame = Time.increaseTimeValue(frame, 8192, 50);
        if (frame == 8192) frame = 0;
        cooldown = Time.decreaseTimeValue(cooldown, 0, 1);
        if (cooldown > 0) {
            if (alpha == 0) soundHandler.play("win");
            alpha = Time.increaseTimeValue(alpha, 1, 1.5f);
            return;
        }
        alpha = Time.decreaseTimeValue(alpha, 0, 1.5f);
        soundHandler.setSoundEffect(soundHandler.VOLUME, "win", alpha);
        if (alpha != 0) return;
        engine.candys3Deluxe.setState(0);
        soundHandler.stop("win");
    }

    public void render(Engine engine){
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var textureHandler = engine.appHandler.getTextureHandler();
        var fontManager = engine.appHandler.getFontManager();
        var captionFont = fontManager.getFont("candys3/captionFont");

        CameraManager.setOrigin();
        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        batch.enableBlending();
        ScreenUtils.clear(0, 0, 0, 1);
        renderHandler.batchBegin();
        batch.setColor(1, 1, 1, alpha);
        textureHandler.get("game/Clock").getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        batch.draw(textureHandler.getRegion("game/Clock", 256, (byte) (frame % 4)), 512, 232);

        if (engine.gamejoltManager != null
                && (!engine.gamejoltManager.threadRunning()
                || !engine.gamejoltManager.trophy.isAddProcessing())) return;
        fontManager.setCurrentFont(captionFont);
        fontManager.setSize(15);
        captionFont.setColor(1, 1, 1, 1);
        fontManager.setText("Saving data...");
        fontManager.setPosition(20, 700);
        fontManager.render(batch);
    }
}