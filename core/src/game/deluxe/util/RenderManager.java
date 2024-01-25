package game.deluxe.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class RenderManager {
    private SpriteBatch batch;
    private SpriteManager spriteManager;
    private VideoManager videoManager;

    public RenderManager(){
        batch = new SpriteBatch();
        spriteManager = new SpriteManager();
        videoManager = new VideoManager();
        videoManager.play("game/Rat/Jumpscare/room.webm");
    }

    public void render(){
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        videoManager.updateRender(batch);

        batch.end();
    }

    public void dispose(){
        batch.dispose();
    }
}
