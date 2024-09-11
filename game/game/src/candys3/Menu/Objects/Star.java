package candys3.Menu.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import util.deluxe.GameData;
import util.*;

public class Star extends SpriteObject {
    private final String text;

    public Star(String path, int x, int y, int width, int height, String text){
        super(path, x, y, width, height, 0);
        this.text = text;
    }

    public void update(Caption caption, InputManager inputManager){
        if (!mouseOver(inputManager, false) || text == null) return;
        String name = GameData.night == 0 ? "Main" : GameData.night == 1 ? "Shadow" : "Hell";
        caption.setText("The " + name + " Cast" + text);
        caption.setActive(true);
    }

    public void render(TextureHandler textureHandler, SpriteBatch batch, FrameBuffer rainbowFrameBuffer, boolean achieved, boolean rainbowAchieved){
        TextureRegion region = textureHandler.get("menu/" + getPath());
        region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        if (rainbowAchieved) {
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
        batch.draw(region, getX(), getY(), getWidth(), getHeight());
    }
}