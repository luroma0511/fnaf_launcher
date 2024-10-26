package util.ui;

import candys3.GameData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import core.Engine;
import util.*;

public class Star extends SpriteObject {
    private final String text;
    private float region;
    private float offset;

    public Star(String path, int x, int y, int width, int height, String text){
        super(path, x, y, width, height, 0);
        this.text = text;
    }

    public void update(String modeName, Caption caption, InputManager inputManager){
        update(modeName, caption, inputManager, -1);
    }

    public void update(String modeName, Caption caption, InputManager inputManager, int time){
        int hour = 0;
        while (time >= 60){
            hour++;
            time -= 60;
        }
        String timeText = String.valueOf(time);
        if (time < 10) timeText = "0" + timeText;
        if (!mouseOver(inputManager) || text == null) return;
        String extra = time == -1 ? "" : "\nBest Time: " + hour + ":" + timeText;
        caption.setText(modeName + text + extra);
        caption.setActive(true);
    }

    public void renderCandys3(TextureHandler textureHandler, SpriteBatch batch, FrameBuffer rainbowFrameBuffer, boolean achieved, boolean rainbow){
        TextureRegion textureRegion = textureHandler.get("menu/" + getPath());
        textureHandler.setFilter(textureRegion.getTexture());
        if (rainbow) {
            batch.setColor(1, 1, 1, 1);
            TextureRegion rainbowRegion = new TextureRegion(rainbowFrameBuffer.getColorBufferTexture());
            rainbowRegion.setRegion(0, 0, 143, 144);
            rainbowRegion.flip(false, true);
            float multiplier = text.equals(" All Challenges") ? 1.5f : 3.6f;
            batch.draw(rainbowRegion, getX(), getY(),
                    rainbowRegion.getRegionWidth() / multiplier,
                    rainbowRegion.getRegionHeight() / multiplier);
            return;
        } else if (!achieved) batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        else if (GameData.night == 2 || text.isEmpty()) batch.setColor(1, 0, 0, 1);
        else if (getPath().equals("star")) batch.setColor(1, 1, 0, 1);
        else batch.setColor(1, 0.25f, 1, 1);
        batch.draw(textureRegion, getX(), getY(), getWidth(), getHeight());
    }

    public void renderCandys2(Engine engine, float offset, boolean star, boolean shadow){
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var textureHandler = engine.appHandler.getTextureHandler();

        if (offset != this.offset) {
            this.region += offset;
            this.offset = offset;
        }

        if (shadow && star){
            region += Time.getDelta() * 15;
            if (region >= 4) region -= 4;
        } else region = 0;

        TextureRegion textureRegion = textureHandler.getRegion("menu/star", 64, (int) region);

        if (!star) batch.setColor(0.25f, 0.25f, 0.375f, renderHandler.screenAlpha);
        else batch.setColor(0.75f, 0.75f, 0.875f, renderHandler.screenAlpha);
        batch.draw(textureRegion, getX(), getY(), getWidth(), getHeight());
    }
}