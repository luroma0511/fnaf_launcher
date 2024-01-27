package game.engine.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class RenderManager {
    private final SpriteBatch batch;
    private final SpriteManager spriteManager;
    private final VideoManager videoManager;

    public RenderManager(){
        batch = new SpriteBatch();
        spriteManager = new SpriteManager();
        videoManager = new VideoManager();


    }

    public void render(){
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        videoManager.updateRender(batch);

        batch.end();
    }

    public void dispose(){
        batch.dispose();
        videoManager.dispose();
        spriteManager.dispose();
    }
}
